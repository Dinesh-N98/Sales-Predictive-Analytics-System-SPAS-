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
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lk.spas.backend.dto.ActivityLogCreateDto;
import lk.spas.backend.dto.ActivityLogDto;
import lk.spas.backend.entity.ActivityLog;
import lk.spas.backend.entity.ActivityType;
import lk.spas.backend.entity.Client;
import lk.spas.backend.entity.LeadStatus;
import lk.spas.backend.entity.Policy;
import lk.spas.backend.entity.PolicyStatus;
import lk.spas.backend.entity.Sale;
import lk.spas.backend.entity.SalesExecutive;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.AchievementService;
import lk.spas.backend.service.OwnershipService;
import lk.spas.backend.validation.ValidationRules;

@Path("activity-logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Secured({"EXECUTIVE"})
public class ActivityLogResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @jakarta.inject.Inject
    private OwnershipService ownershipService;

    @jakarta.inject.Inject
    private AchievementService achievementService;

    @GET
    public List<ActivityLogDto> getAll(@QueryParam("page") Integer page, @QueryParam("size") Integer size,
            @Context SecurityContext securityContext) {
        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        return em.createQuery("SELECT a FROM ActivityLog a WHERE a.salesExecutive.id = :seId ORDER BY a.createdAt DESC, a.id DESC", ActivityLog.class)
                .setParameter("seId", currentExecutiveId(securityContext))
                .setFirstResult(normalizedPage * normalizedSize).setMaxResults(normalizedSize).getResultList().stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Integer id, @Context SecurityContext securityContext) {
        ActivityLog activityLog = em.find(ActivityLog.class, id);
        if (activityLog != null && !belongsToCurrentUser(activityLog.getSalesExecutive(), securityContext)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return activityLog == null ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(toDto(activityLog)).build();
    }

    @POST
    @Transactional
    public Response create(ActivityLogCreateDto request, @Context UriInfo uriInfo,
            @Context SecurityContext securityContext) {
        Response validationError = validateRequest(request);
        if (validationError != null) return validationError;

        SalesExecutive executive = ownershipService.getCurrentExecutive(securityContext);
        Client client = em.find(Client.class, request.getClientId());
        if (executive == null) {
            return badRequest("Authenticated executive id is invalid or not found");
        }
        if (!belongsToCurrentUser(client == null ? null : client.getSalesExecutive(), securityContext)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        ActivityLog activityLog = new ActivityLog();
        activityLog.setSalesExecutive(executive);
        activityLog.setFeedbackToken(UUID.randomUUID().toString());
        Response relationshipError = applyRelationships(activityLog, request);
        if (relationshipError != null) return relationshipError;
        applyMutableFields(activityLog, request);
        int followupCount = em.createQuery(
            "SELECT COUNT(a) FROM ActivityLog a WHERE a.salesExecutive.id = :seId AND a.client.id = :clientId",
            Long.class)
            .setParameter("seId", executive.getId())
            .setParameter("clientId", client.getId())
            .getSingleResult().intValue();
        activityLog.setFollowupCount(followupCount);
        em.persist(activityLog);
        em.flush();
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(activityLog.getId())).build();
        return Response.created(location).entity(toDto(activityLog)).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response update(@PathParam("id") Integer id, ActivityLogCreateDto request, @Context SecurityContext securityContext) {
        ActivityLog activityLog = em.find(ActivityLog.class, id);
        if (activityLog == null) return Response.status(Response.Status.NOT_FOUND).build();
        if (!belongsToCurrentUser(activityLog.getSalesExecutive(), securityContext)) return Response.status(Response.Status.NOT_FOUND).build();
        Response validationError = validateRequest(request);
        if (validationError != null) return validationError;
        if (request.getStatusId() == 3 && request.getPremiumAmount() == null) {
            return badRequest("premiumAmount is required when marking Sold");
        }
        if (request.getStatusId() == 3) {
            String amountError = ValidationRules.amount(request.getPremiumAmount(), "premiumAmount");
            if (amountError != null) return badRequest(amountError);
        }
        Client client = em.find(Client.class, request.getClientId());
        if (!belongsToCurrentUser(client == null ? null : client.getSalesExecutive(), securityContext)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (!activityLog.getClient().getId().equals(request.getClientId())) {
            return badRequest("clientId cannot be changed");
        }
        Integer existingPolicyId = activityLog.getClientPolicy() == null ? null : activityLog.getClientPolicy().getId();
        if (existingPolicyId == null ? request.getClientPolicyId() != null
                : !existingPolicyId.equals(request.getClientPolicyId())) {
            return badRequest("clientPolicyId cannot be changed");
        }

        Response relationshipError = validateRelationships(request);
        if (relationshipError != null) return relationshipError;
        if (!ValidationRules.allowedLeadStatusTransition(activityLog.getStatus().getId(), request.getStatusId())) {
            return badRequest("Invalid lead status transition");
        }
        activityLog.setActivityType(em.find(ActivityType.class, request.getActivityTypeId()));
        activityLog.setStatus(em.find(LeadStatus.class, request.getStatusId()));
        activityLog.setActivityDate(request.getActivityDate());
        activityLog.setClientPolicy(request.getClientPolicyId() == null ? null
                : em.find(Policy.class, request.getClientPolicyId()));
        activityLog.setNextFollowUpDate(request.getNextFollowUpDate());
        activityLog.setRemarks(request.getRemarks());
        activityLog.setDurationMinutes(request.getDurationMinutes());
        if (request.getStatusId() == 3 && activityLog.getClientPolicy() != null) {
            Sale existingSale = em.createQuery("SELECT s FROM Sale s WHERE s.activityLog.id = :activityLogId", Sale.class)
                    .setParameter("activityLogId", activityLog.getId())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (existingSale != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(Map.of("message", "A sale already exists for this activity log"))
                        .build();
            }

            SalesExecutive executive = ownershipService.getCurrentExecutive(securityContext);
            if (executive == null) {
                return badRequest("Authenticated executive id is invalid or not found");
            }
            int seId = executive.getId();
            BigDecimal premiumAmount = request.getPremiumAmount();
            Sale sale = new Sale();
            sale.setActivityLog(activityLog);
            sale.setClient(activityLog.getClient());
            sale.setPolicy(activityLog.getClientPolicy());
            sale.setSalesExecutive(executive);
            sale.setIssueDate(LocalDate.now());
            sale.setPremiumAmount(premiumAmount);
            sale.setStatus(em.find(PolicyStatus.class, 1));
            sale.setHasClaimed(false);
            em.persist(sale);
            achievementService.applySaleToAchievement(seId, sale.getIssueDate(), premiumAmount);
        }
        return Response.ok(toDto(activityLog)).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id, @Context SecurityContext securityContext) {
        ActivityLog activityLog = em.find(ActivityLog.class, id);
        if (activityLog == null) return Response.status(Response.Status.NOT_FOUND).build();
        if (!belongsToCurrentUser(activityLog.getSalesExecutive(), securityContext)) return Response.status(Response.Status.NOT_FOUND).build();
        if (em.createQuery("SELECT COUNT(f) FROM ClientFeedback f WHERE f.activityLog.id = :id", Long.class)
            .setParameter("id", id).getSingleResult() > 0) {
            return Response.status(Response.Status.CONFLICT)
                .entity(new ApiError(409, "Conflict", "Activity log cannot be deleted while client feedback references it"))
                .build();
        }
        em.remove(activityLog);
        return Response.noContent().build();
    }

    private Response validateRequest(ActivityLogCreateDto request) {
        if (request == null) return badRequest("Request body is required");
        if (request.getClientId() == null) return badRequest("clientId is required");
        if (request.getActivityTypeId() == null) return badRequest("activityTypeId is required");
        if (request.getStatusId() == null) return badRequest("statusId is required");
        if (request.getActivityDate() == null) return badRequest("activityDate is required");
        String durationError = ValidationRules.required(request.getDurationMinutes(), "durationMinutes");
        if (durationError != null) return badRequest(durationError);
        if (request.getDurationMinutes() != null && request.getDurationMinutes() < 0) {
            return badRequest("durationMinutes must not be negative");
        }
        String remarksError = ValidationRules.maxLength(request.getRemarks(), "remarks", 10000);
        if (remarksError != null) return badRequest(remarksError);
        String followUpError = ValidationRules.dateOrder(request.getActivityDate(), request.getNextFollowUpDate(),
            "nextFollowUpDate", "activityDate");
        if (followUpError != null) return badRequest(followUpError);
        return validateRelationships(request);
    }

    private Response validateRelationships(ActivityLogCreateDto request) {
        if (em.find(Client.class, request.getClientId()) == null) return badRequest("Invalid clientId: client not found");
        if (em.find(ActivityType.class, request.getActivityTypeId()) == null) return badRequest("Invalid activityTypeId: activity type not found");
        if (em.find(LeadStatus.class, request.getStatusId()) == null) return badRequest("Invalid statusId: lead status not found");
        if (request.getClientPolicyId() != null && em.find(Policy.class, request.getClientPolicyId()) == null) {
            return badRequest("Invalid clientPolicyId: policy not found");
        }
        return null;
    }

    private Response applyRelationships(ActivityLog activityLog, ActivityLogCreateDto request) {
        activityLog.setClient(em.find(Client.class, request.getClientId()));
        activityLog.setActivityType(em.find(ActivityType.class, request.getActivityTypeId()));
        activityLog.setStatus(em.find(LeadStatus.class, request.getStatusId()));
        activityLog.setClientPolicy(request.getClientPolicyId() == null ? null
                : em.find(Policy.class, request.getClientPolicyId()));
        return null;
    }

    private void applyMutableFields(ActivityLog activityLog, ActivityLogCreateDto request) {
        activityLog.setActivityDate(request.getActivityDate());
        activityLog.setNextFollowUpDate(request.getNextFollowUpDate());
        activityLog.setRemarks(request.getRemarks());
        activityLog.setDurationMinutes(request.getDurationMinutes());
    }

    private ActivityLogDto toDto(ActivityLog activityLog) {
        Policy policy = activityLog.getClientPolicy();
        return new ActivityLogDto(activityLog.getId(), activityLog.getSalesExecutive().getId(),
            activityLog.getClient().getId(), activityLog.getFollowupCount(), activityLog.getClient().getFullName(),
                activityLog.getActivityType().getId(), activityLog.getActivityType().getActivityName(),
                activityLog.getStatus().getId(), activityLog.getStatus().getStatusName(), activityLog.getActivityDate(),
                policy == null ? null : policy.getId(), policy == null ? null : policy.getPolicyName(),
                activityLog.getNextFollowUpDate(), activityLog.getRemarks(), activityLog.getDurationMinutes(),
                activityLog.getFeedbackToken(), activityLog.getCreatedAt(), activityLog.getUpdatedAt());
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