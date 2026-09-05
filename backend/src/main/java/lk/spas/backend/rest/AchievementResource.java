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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lk.spas.backend.dto.AchievementCreateDto;
import lk.spas.backend.dto.AchievementDto;
import lk.spas.backend.entity.Achievement;
import lk.spas.backend.entity.SalesExecutive;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.OwnershipService;
import lk.spas.backend.validation.ValidationRules;

@Path("achievements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AchievementResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @jakarta.inject.Inject
    private OwnershipService ownershipService;

    @GET
    @Secured({"MANAGER", "EXECUTIVE"})
    public List<AchievementDto> getAll(@QueryParam("page") Integer page, @QueryParam("size") Integer size,
            @Context SecurityContext securityContext) {
        boolean executive = securityContext.isUserInRole("EXECUTIVE");
        String query = executive ? "SELECT a FROM Achievement a WHERE a.salesExecutive.id = :seId ORDER BY a.id ASC"
            : "SELECT a FROM Achievement a ORDER BY a.id ASC";
        var typedQuery = em.createQuery(query, Achievement.class);
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
        Achievement achievement = em.find(Achievement.class, id);
        if (achievement != null && securityContext.isUserInRole("EXECUTIVE")
                && !belongsToCurrentUser(achievement.getSalesExecutive(), securityContext)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return achievement == null ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(toDto(achievement)).build();
    }

    @POST
    @Secured({"MANAGER"})
    @Transactional
    public Response create(AchievementCreateDto request, @Context UriInfo uriInfo) {
        Response validationError = validateRequest(request);
        if (validationError != null) return validationError;
        Response duplicateError = validateDuplicate(request.getSeId(), request.getMonthYear(), null);
        if (duplicateError != null) return duplicateError;

        Achievement achievement = new Achievement();
        achievement.setSalesExecutive(em.find(SalesExecutive.class, request.getSeId()));
        applyFields(achievement, request);
        em.persist(achievement);
        em.flush();
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(achievement.getId())).build();
        return Response.created(location).entity(toDto(achievement)).build();
    }

    @PUT
    @Path("{id}")
    @Secured({"MANAGER"})
    @Transactional
    public Response update(@PathParam("id") Integer id, AchievementCreateDto request) {
        Achievement achievement = em.find(Achievement.class, id);
        if (achievement == null) return Response.status(Response.Status.NOT_FOUND).build();
        Response validationError = validateRequest(request);
        if (validationError != null) return validationError;
        Response duplicateError = validateDuplicate(request.getSeId(), request.getMonthYear(), id);
        if (duplicateError != null) return duplicateError;

        achievement.setSalesExecutive(em.find(SalesExecutive.class, request.getSeId()));
        applyFields(achievement, request);
        return Response.ok(toDto(achievement)).build();
    }

    @DELETE
    @Path("{id}")
    @Secured({"MANAGER"})
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        Achievement achievement = em.find(Achievement.class, id);
        if (achievement == null) return Response.status(Response.Status.NOT_FOUND).build();
        em.remove(achievement);
        return Response.noContent().build();
    }

    private Response validateRequest(AchievementCreateDto request) {
        if (request == null) return badRequest("Request body is required");
        if (request.getSeId() == null) return badRequest("seId is required");
        if (request.getTargetAmount() == null) return badRequest("targetAmount is required");
        String targetError = ValidationRules.amount(request.getTargetAmount(), "targetAmount");
        if (targetError != null) return badRequest(targetError);
        String achievedError = ValidationRules.amount(request.getAchievedAmount(), "achievedAmount");
        if (achievedError != null) return badRequest(achievedError);
        if (request.getMonthYear() == null) return badRequest("monthYear is required");
        if (em.find(SalesExecutive.class, request.getSeId()) == null) return badRequest("Invalid seId: sales executive not found");
        return null;
    }

    private Response validateDuplicate(Integer seId, java.time.LocalDate monthYear, Integer excludedId) {
        String query = "SELECT COUNT(a) FROM Achievement a WHERE a.salesExecutive.id = :seId AND a.monthYear = :monthYear"
                + (excludedId == null ? "" : " AND a.id <> :excludedId");
        var typedQuery = em.createQuery(query, Long.class)
                .setParameter("seId", seId).setParameter("monthYear", monthYear);
        if (excludedId != null) typedQuery.setParameter("excludedId", excludedId);
        if (typedQuery.getSingleResult() > 0) return badRequest("Achievement already exists for this executive and month");
        return null;
    }

    private void applyFields(Achievement achievement, AchievementCreateDto request) {
        achievement.setTargetAmount(request.getTargetAmount());
        achievement.setAchievedAmount(request.getAchievedAmount() == null ? BigDecimal.ZERO : request.getAchievedAmount());
        achievement.setMonthYear(request.getMonthYear());
    }

    private AchievementDto toDto(Achievement achievement) {
        return new AchievementDto(achievement.getId(), achievement.getSalesExecutive().getId(),
                achievement.getSalesExecutive().getFullName(), achievement.getTargetAmount(),
                achievement.getAchievedAmount(), achievement.getMonthYear());
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