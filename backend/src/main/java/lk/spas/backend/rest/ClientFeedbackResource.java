package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lk.spas.backend.dto.ClientFeedbackCreateDto;
import lk.spas.backend.dto.ClientFeedbackDto;
import lk.spas.backend.dto.ClientFeedbackUpdateDto;
import lk.spas.backend.entity.ActivityLog;
import lk.spas.backend.entity.Client;
import lk.spas.backend.entity.ClientFeedback;
import lk.spas.backend.entity.FeedbackImprovement;
import lk.spas.backend.entity.FeedbackStrength;
import lk.spas.backend.entity.SalesExecutive;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.OwnershipService;
import lk.spas.backend.validation.ValidationRules;

@Path("client-feedbacks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientFeedbackResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @jakarta.inject.Inject
    private OwnershipService ownershipService;

    @GET
    @Secured({"MANAGER", "EXECUTIVE"})
    public List<ClientFeedbackDto> getAll(@QueryParam("page") Integer page, @QueryParam("size") Integer size,
            @Context SecurityContext securityContext) {
        boolean executive = securityContext.isUserInRole("EXECUTIVE");
        String query = executive ? "SELECT f FROM ClientFeedback f WHERE f.salesExecutive.id = :seId ORDER BY f.id ASC"
            : "SELECT f FROM ClientFeedback f ORDER BY f.id ASC";
        var typedQuery = em.createQuery(query, ClientFeedback.class);
        if (executive) typedQuery.setParameter("seId", currentExecutiveId(securityContext));
        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        typedQuery.setFirstResult(normalizedPage * normalizedSize).setMaxResults(normalizedSize);
        return typedQuery.getResultList().stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    @Secured({"MANAGER", "EXECUTIVE"})
    public Response getById(@PathParam("id") Integer id, @Context SecurityContext securityContext) {
        ClientFeedback feedback = em.find(ClientFeedback.class, id);
        if (feedback != null && securityContext.isUserInRole("EXECUTIVE")
                && !belongsToCurrentUser(feedback.getSalesExecutive(), securityContext)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return feedback == null ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(toDto(feedback)).build();
    }

    @POST
    @Secured({"EXECUTIVE"})
    @Transactional
    public Response create(ClientFeedbackCreateDto request, @Context UriInfo uriInfo,
            @Context SecurityContext securityContext) {
        Response validationError = validateCreateRequest(request);
        if (validationError != null) return validationError;
        SalesExecutive salesExecutive = ownershipService.getCurrentExecutive(securityContext);
        if (salesExecutive == null) return badRequest("Authenticated executive id is invalid or not found");
        ActivityLog activityLog = em.find(ActivityLog.class, request.getActivityLogId());
        Client client = em.find(Client.class, request.getClientId());
        if (activityLog == null || !belongsToCurrentUser(activityLog.getSalesExecutive(), securityContext)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (client == null || !activityLog.getClient().getId().equals(client.getId())
                || !salesExecutive.getId().equals(activityLog.getSalesExecutive().getId())) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (feedbackForActivityExists(request.getActivityLogId(), null)) {
            return badRequest("Feedback already exists for this activity log");
        }

        ClientFeedback feedback = new ClientFeedback();
        feedback.setActivityLog(activityLog);
        feedback.setClient(client);
        feedback.setSalesExecutive(salesExecutive);
        applyFields(feedback, request.getRating(), request.getStrengthId(), request.getImprovementId(), request.getComments());
        em.persist(feedback);
        em.flush();
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(feedback.getId())).build();
        return Response.created(location).entity(toDto(feedback)).build();
    }

    @PUT
    @Path("{id}")
    @Secured({"EXECUTIVE"})
    @Transactional
    public Response update(@PathParam("id") Integer id, ClientFeedbackUpdateDto request, @Context SecurityContext securityContext) {
        ClientFeedback feedback = em.find(ClientFeedback.class, id);
        if (feedback == null) return Response.status(Response.Status.NOT_FOUND).build();
        if (!belongsToCurrentUser(feedback.getSalesExecutive(), securityContext)) return Response.status(Response.Status.NOT_FOUND).build();
        Response validationError = validateUpdateRequest(request);
        if (validationError != null) return validationError;
        Response lookupError = validateOptionalLookups(request.getStrengthId(), request.getImprovementId());
        if (lookupError != null) return lookupError;
        applyFields(feedback, request.getRating(), request.getStrengthId(), request.getImprovementId(), request.getComments());
        return Response.ok(toDto(feedback)).build();
    }

    @DELETE
    @Path("{id}")
    @Secured({"MANAGER"})
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        ClientFeedback feedback = em.find(ClientFeedback.class, id);
        if (feedback == null) return Response.status(Response.Status.NOT_FOUND).build();
        em.remove(feedback);
        return Response.noContent().build();
    }

    private Response validateCreateRequest(ClientFeedbackCreateDto request) {
        if (request == null) return badRequest("Request body is required");
        if (request.getActivityLogId() == null) return badRequest("activityLogId is required");
        if (request.getClientId() == null) return badRequest("clientId is required");
        if (request.getRating() == null) return badRequest("rating is required");
        Response ratingError = validateRating(request.getRating());
        if (ratingError != null) return ratingError;
        String commentsError = ValidationRules.maxLength(request.getComments(), "comments", 10000);
        if (commentsError != null) return badRequest(commentsError);
        if (em.find(ActivityLog.class, request.getActivityLogId()) == null) return badRequest("Invalid activityLogId: activity log not found");
        if (em.find(Client.class, request.getClientId()) == null) return badRequest("Invalid clientId: client not found");
        return validateOptionalLookups(request.getStrengthId(), request.getImprovementId());
    }

    private Response validateUpdateRequest(ClientFeedbackUpdateDto request) {
        if (request == null) return badRequest("Request body is required");
        if (request.getRating() == null) return badRequest("rating is required");
        Response ratingError = validateRating(request.getRating());
        if (ratingError != null) return ratingError;
        String commentsError = ValidationRules.maxLength(request.getComments(), "comments", 10000);
        return commentsError == null ? null : badRequest(commentsError);
    }

    private Response validateRating(Integer rating) {
        return rating < 1 || rating > 5 ? badRequest("rating must be between 1 and 5") : null;
    }

    private Response validateOptionalLookups(Integer strengthId, Integer improvementId) {
        if (strengthId != null && em.find(FeedbackStrength.class, strengthId) == null) return badRequest("Invalid strengthId: feedback strength not found");
        if (improvementId != null && em.find(FeedbackImprovement.class, improvementId) == null) return badRequest("Invalid improvementId: feedback improvement not found");
        return null;
    }

    private boolean feedbackForActivityExists(Integer activityLogId, Integer excludedId) {
        String query = "SELECT COUNT(f) FROM ClientFeedback f WHERE f.activityLog.id = :activityLogId"
                + (excludedId == null ? "" : " AND f.id <> :excludedId");
        var typedQuery = em.createQuery(query, Long.class).setParameter("activityLogId", activityLogId);
        if (excludedId != null) typedQuery.setParameter("excludedId", excludedId);
        return typedQuery.getSingleResult() > 0;
    }

    private void applyFields(ClientFeedback feedback, Integer rating, Integer strengthId,
            Integer improvementId, String comments) {
        feedback.setRating(rating);
        feedback.setStrength(strengthId == null ? null : em.find(FeedbackStrength.class, strengthId));
        feedback.setImprovement(improvementId == null ? null : em.find(FeedbackImprovement.class, improvementId));
        feedback.setComments(comments);
    }

    private ClientFeedbackDto toDto(ClientFeedback feedback) {
        FeedbackStrength strength = feedback.getStrength();
        FeedbackImprovement improvement = feedback.getImprovement();
        return new ClientFeedbackDto(feedback.getId(), feedback.getActivityLog().getId(), feedback.getClient().getId(),
                feedback.getClient().getFullName(), feedback.getSalesExecutive().getId(),
                feedback.getSalesExecutive().getFullName(), feedback.getRating(),
                strength == null ? null : strength.getId(), strength == null ? null : strength.getStrengthName(),
                improvement == null ? null : improvement.getId(), improvement == null ? null : improvement.getImprovementName(),
                feedback.getComments(), feedback.getCreatedAt());
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", message)).build();
    }

    private Integer currentExecutiveId(SecurityContext securityContext) {
        return ownershipService.currentExecutiveId(securityContext);
    }

    private int normalizePage(Integer page) {
        return page == null || page < 0 ? 0 : page;
    }

    private int normalizeSize(Integer size) {
        return size == null || size <= 0 ? 20 : Math.min(size, 100);
    }

    private boolean belongsToCurrentUser(SalesExecutive executive, SecurityContext securityContext) {
        return ownershipService.belongsToCurrentExecutive(executive, securityContext);
    }
}