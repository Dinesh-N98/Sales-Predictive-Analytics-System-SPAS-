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
import lk.spas.backend.dto.SaleCreateDto;
import lk.spas.backend.dto.SaleDto;
import lk.spas.backend.entity.Client;
import lk.spas.backend.entity.Policy;
import lk.spas.backend.entity.PolicyStatus;
import lk.spas.backend.entity.Sale;
import lk.spas.backend.entity.SalesExecutive;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.OwnershipService;
import lk.spas.backend.validation.ValidationRules;

@Path("sales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SaleResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @jakarta.inject.Inject
    private OwnershipService ownershipService;

    @GET
    @Secured({"MANAGER", "EXECUTIVE"})
    public List<SaleDto> getAll(@QueryParam("page") Integer page, @QueryParam("size") Integer size,
            @Context SecurityContext securityContext) {
        String query = isExecutive(securityContext) ? "SELECT s FROM Sale s WHERE s.salesExecutive.id = :seId ORDER BY s.id ASC"
            : "SELECT s FROM Sale s ORDER BY s.id ASC";
        var typedQuery = em.createQuery(query, Sale.class);
        if (isExecutive(securityContext)) typedQuery.setParameter("seId", executiveId(securityContext));
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
        Sale sale = em.find(Sale.class, id);
        if (sale != null && isExecutive(securityContext) && !belongsToCurrentUser(sale.getSalesExecutive(), securityContext)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return sale == null ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(toDto(sale)).build();
    }

    @POST
    @Secured({"MANAGER", "EXECUTIVE"})
    @Transactional
    public Response create(SaleCreateDto request, @Context UriInfo uriInfo, @Context SecurityContext securityContext) {
        if (isExecutive(securityContext) && request != null) {
            SalesExecutive executive = ownershipService.getCurrentExecutive(securityContext);
            if (executive == null) return badRequest("Authenticated executive id is invalid or not found");
            request.setSeId(executive.getId());
        }
        Response validationError = validateRequest(request, true);
        if (validationError != null) return validationError;
        if (isExecutive(securityContext)) {
            Client client = em.find(Client.class, request.getClientId());
            if (!belongsToCurrentUser(client == null ? null : client.getSalesExecutive(), securityContext)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        Sale sale = new Sale();
        Response relationshipError = applyRelationships(sale, request);
        if (relationshipError != null) return relationshipError;
        applyMutableFields(sale, request);
        em.persist(sale);
        em.flush();
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(sale.getId())).build();
        return Response.created(location).entity(toDto(sale)).build();
    }

    @PUT
    @Path("{id}")
    @Secured({"MANAGER", "EXECUTIVE"})
    @Transactional
    public Response update(@PathParam("id") Integer id, SaleCreateDto request, @Context SecurityContext securityContext) {
        Sale sale = em.find(Sale.class, id);
        if (sale == null) return Response.status(Response.Status.NOT_FOUND).build();
        if (isExecutive(securityContext) && !belongsToCurrentUser(sale.getSalesExecutive(), securityContext)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Response validationError = validateRequest(request, false);
        if (validationError != null) return validationError;
        Response relationshipError = validateImmutableRelationships(sale, request);
        if (relationshipError != null) return relationshipError;
        if (!ValidationRules.allowedPolicyStatusTransition(sale.getStatus().getId(), request.getStatusId())) {
            return badRequest("Invalid policy status transition");
        }
        // Client, policy, and se are immutable sale history; only these fields may change.
        sale.setRenewalDate(request.getRenewalDate());
        sale.setPremiumAmount(request.getPremiumAmount());
        sale.setStatus(em.find(PolicyStatus.class, request.getStatusId()));
        sale.setHasClaimed(request.isHasClaimed());
        return Response.ok(toDto(sale)).build();
    }

    @DELETE
    @Path("{id}")
    @Secured({"MANAGER"})
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        Sale sale = em.find(Sale.class, id);
        if (sale == null) return Response.status(Response.Status.NOT_FOUND).build();
        em.remove(sale);
        return Response.noContent().build();
    }

    private Response applyRelationships(Sale sale, SaleCreateDto request) {
        Client client = em.find(Client.class, request.getClientId());
        Policy policy = em.find(Policy.class, request.getPolicyId());
        SalesExecutive salesExecutive = em.find(SalesExecutive.class, request.getSeId());
        PolicyStatus status = em.find(PolicyStatus.class, request.getStatusId());
        if (client == null) return badRequest("Invalid clientId: client not found");
        if (policy == null) return badRequest("Invalid policyId: policy not found");
        if (salesExecutive == null) return badRequest("Invalid seId: sales executive not found");
        if (status == null) return badRequest("Invalid statusId: policy status not found");
        sale.setClient(client);
        sale.setPolicy(policy);
        sale.setSalesExecutive(salesExecutive);
        sale.setStatus(status);
        return null;
    }

    private Response validateImmutableRelationships(Sale sale, SaleCreateDto request) {
        Response existenceError = validateRelationshipIds(request);
        if (existenceError != null) return existenceError;
        if (!sale.getClient().getId().equals(request.getClientId())) return badRequest("clientId cannot be changed");
        if (!sale.getPolicy().getId().equals(request.getPolicyId())) return badRequest("policyId cannot be changed");
        if (!sale.getSalesExecutive().getId().equals(request.getSeId())) return badRequest("seId cannot be changed");
        return null;
    }

    private Response validateRequest(SaleCreateDto request, boolean requireIssueDate) {
        if (request == null) return badRequest("Request body is required");
        if (request.getClientId() == null) return badRequest("clientId is required");
        if (request.getPolicyId() == null) return badRequest("policyId is required");
        if (request.getSeId() == null) return badRequest("seId is required");
        if (requireIssueDate && request.getIssueDate() == null) return badRequest("issueDate is required");
        if (request.getPremiumAmount() == null) return badRequest("premiumAmount is required");
        String amountError = ValidationRules.amount(request.getPremiumAmount(), "premiumAmount");
        if (amountError != null) return badRequest(amountError);
        String renewalDateError = ValidationRules.dateOrder(request.getIssueDate(), request.getRenewalDate(), "renewalDate", "issueDate");
        if (renewalDateError != null) return badRequest(renewalDateError);
        if (request.getStatusId() == null) return badRequest("statusId is required");
        return validateRelationshipIds(request);
    }

    private Response validateRelationshipIds(SaleCreateDto request) {
        if (em.find(Client.class, request.getClientId()) == null) return badRequest("Invalid clientId: client not found");
        if (em.find(Policy.class, request.getPolicyId()) == null) return badRequest("Invalid policyId: policy not found");
        if (em.find(SalesExecutive.class, request.getSeId()) == null) return badRequest("Invalid seId: sales executive not found");
        if (em.find(PolicyStatus.class, request.getStatusId()) == null) return badRequest("Invalid statusId: policy status not found");
        return null;
    }

    private void applyMutableFields(Sale sale, SaleCreateDto request) {
        sale.setIssueDate(request.getIssueDate());
        sale.setRenewalDate(request.getRenewalDate());
        sale.setPremiumAmount(request.getPremiumAmount());
        sale.setHasClaimed(request.isHasClaimed());
    }

    private SaleDto toDto(Sale sale) {
        return new SaleDto(sale.getId(), sale.getClient().getId(), sale.getClient().getFullName(),
                sale.getPolicy().getId(), sale.getPolicy().getPolicyName(), sale.getSalesExecutive().getId(),
            sale.getSalesExecutive().getFullName(), sale.getActivityLog() == null ? null : sale.getActivityLog().getId(),
            sale.getIssueDate(), sale.getRenewalDate(),
                sale.getPremiumAmount(), sale.getStatus().getId(), sale.getStatus().getStatusName(),
                sale.isHasClaimed(), sale.getCreatedAt(), sale.getUpdatedAt());
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", message)).build();
    }

    private boolean isExecutive(SecurityContext securityContext) {
        return securityContext != null && securityContext.isUserInRole("EXECUTIVE");
    }

    private Integer executiveId(SecurityContext securityContext) {
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