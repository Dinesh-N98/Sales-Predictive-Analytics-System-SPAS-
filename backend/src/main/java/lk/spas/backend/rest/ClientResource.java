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
import lk.spas.backend.dto.ClientCreateDto;
import lk.spas.backend.dto.ClientDto;
import lk.spas.backend.dto.LastPolicyDto;
import lk.spas.backend.entity.Client;
import lk.spas.backend.entity.ClientType;
import lk.spas.backend.entity.FinancialLevel;
import lk.spas.backend.entity.LeadSource;
import lk.spas.backend.entity.RejectionReason;
import lk.spas.backend.entity.ActivityLog;
import lk.spas.backend.entity.SalesExecutive;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.OwnershipService;
import lk.spas.backend.validation.ValidationRules;

@Path("clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @jakarta.inject.Inject
    private OwnershipService ownershipService;

    @GET
    @Secured({"EXECUTIVE"})
    public List<ClientDto> getAll(@QueryParam("page") Integer page, @QueryParam("size") Integer size,
            @Context SecurityContext securityContext) {
        String query = isExecutive(securityContext) ? "SELECT c FROM Client c WHERE c.salesExecutive.id = :seId ORDER BY c.id ASC"
            : "SELECT c FROM Client c ORDER BY c.id ASC";
        var typedQuery = em.createQuery(query, Client.class);
        if (isExecutive(securityContext)) typedQuery.setParameter("seId", executiveId(securityContext));
        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        typedQuery.setFirstResult(normalizedPage * normalizedSize).setMaxResults(normalizedSize);
        return typedQuery.getResultList().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    @Secured({"EXECUTIVE"})
    public Response getById(@PathParam("id") Integer id, @Context SecurityContext securityContext) {
        Client client = em.find(Client.class, id);
        if (client != null && isExecutive(securityContext) && !belongsToCurrentUser(client.getSalesExecutive(), securityContext)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return client == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(toDto(client)).build();
    }

    @GET
    @Path("{id}/last-policy")
    @Secured({"EXECUTIVE"})
    public Response getLastPolicy(@PathParam("id") Integer id, @Context SecurityContext securityContext) {
        Client client = em.find(Client.class, id);
        if (client == null || (isExecutive(securityContext) && !belongsToCurrentUser(client.getSalesExecutive(), securityContext))) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<ActivityLog> activities = em.createQuery(
            "SELECT a FROM ActivityLog a WHERE a.client.id = :clientId ORDER BY a.createdAt DESC, a.id DESC",
            ActivityLog.class)
                .setParameter("clientId", id)
                .setMaxResults(1)
                .getResultList();
        if (activities.isEmpty() || activities.get(0).getClientPolicy() == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        ActivityLog activity = activities.get(0);
        return Response.ok(new LastPolicyDto(activity.getClientPolicy().getId(), activity.getClientPolicy().getPolicyName(),
            activity.getActivityDate(), activity.getCreatedAt())).build();
    }

    @POST
    @Secured({"EXECUTIVE"})
    @Transactional
    public Response create(ClientCreateDto request, @Context UriInfo uriInfo, @Context SecurityContext securityContext) {
        if (isExecutive(securityContext) && request != null) {
            SalesExecutive executive = ownershipService.getCurrentExecutive(securityContext);
            if (executive == null) return badRequest("Authenticated executive id is invalid or not found");
            request.setSeId(executive.getId());
        }
        Response validationError = validateRequest(request);
        if (validationError != null) return validationError;
        Client client = new Client();
        Response relationshipError = applyRequest(client, request);
        if (relationshipError != null) return relationshipError;
        em.persist(client);
        em.flush();

        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(client.getId())).build();
        return Response.created(location).entity(toDto(client)).build();
    }

    @PUT
    @Path("{id}")
    @Secured({"EXECUTIVE"})
    @Transactional
    public Response update(@PathParam("id") Integer id, ClientCreateDto request, @Context SecurityContext securityContext) {
        Client client = em.find(Client.class, id);
        if (client == null) return Response.status(Response.Status.NOT_FOUND).build();
        if (isExecutive(securityContext) && !belongsToCurrentUser(client.getSalesExecutive(), securityContext)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Response validationError = validateRequest(request);
        if (validationError != null) return validationError;
        if (isExecutive(securityContext) && !client.getSalesExecutive().getId().equals(request.getSeId())) {
            return badRequest("seId cannot be changed by an executive");
        }
        Response relationshipError = applyRequest(client, request);
        return relationshipError != null ? relationshipError : Response.ok(toDto(client)).build();
    }

    @DELETE
    @Path("{id}")
    @Secured({"EXECUTIVE"})
    @Transactional
    public Response delete(@PathParam("id") Integer id, @Context SecurityContext securityContext) {
        Client client = em.find(Client.class, id);
        if (client == null) return Response.status(Response.Status.NOT_FOUND).build();
        if (!belongsToCurrentUser(client.getSalesExecutive(), securityContext)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (hasReferences("SELECT COUNT(a) FROM ActivityLog a WHERE a.client.id = :id", id)
                || hasReferences("SELECT COUNT(s) FROM Sale s WHERE s.client.id = :id", id)
                || hasReferences("SELECT COUNT(f) FROM ClientFeedback f WHERE f.client.id = :id", id)) {
            return conflict("Client cannot be deleted while activities, sales, or feedback reference it");
        }
        em.remove(client);
        return Response.noContent().build();
    }

    private Response applyRequest(Client client, ClientCreateDto request) {
        SalesExecutive salesExecutive = em.find(SalesExecutive.class, request.getSeId());
        ClientType clientType = em.find(ClientType.class, request.getClientTypeId());
        FinancialLevel financialLevel = request.getFinancialLevelId() == null ? null
                : em.find(FinancialLevel.class, request.getFinancialLevelId());
        RejectionReason rejectionReason = request.getRejectionReasonId() == null ? null
                : em.find(RejectionReason.class, request.getRejectionReasonId());
        LeadSource leadSource = em.find(LeadSource.class, request.getLeadSourceId());

        if (salesExecutive == null) return badRequest("Invalid seId: sales executive not found");
        if (clientType == null) return badRequest("Invalid clientTypeId: client type not found");
        if (financialLevel == null && request.getFinancialLevelId() != null) {
            return badRequest("Invalid financialLevelId: financial level not found");
        }
        if (rejectionReason == null && request.getRejectionReasonId() != null) {
            return badRequest("Invalid rejectionReasonId: rejection reason not found");
        }
        if (leadSource == null) return badRequest("Invalid leadSourceId: lead source not found");

        client.setSalesExecutive(salesExecutive);
        client.setFullName(request.getFullName());
        client.setAddress(request.getAddress());
        client.setContactNumber(request.getContactNumber());
        client.setClientType(clientType);
        client.setFinancialLevel(financialLevel);
        client.setRejectionReason(rejectionReason);
        client.setLeadSource(leadSource);
        return null;
    }

    private Response validateRequest(ClientCreateDto request) {
        if (request == null) return badRequest("Request body is required");
        if (request.getSeId() == null) return badRequest("seId is required");
        String fullNameError = ValidationRules.required(request.getFullName(), "fullName");
        if (fullNameError != null) return badRequest(fullNameError);
        String fullNameLengthError = ValidationRules.maxLength(request.getFullName(), "fullName", 100);
        if (fullNameLengthError != null) return badRequest(fullNameLengthError);
        String contactRequiredError = ValidationRules.required(request.getContactNumber(), "contactNumber");
        if (contactRequiredError != null) return badRequest(contactRequiredError);
        String contactError = ValidationRules.phone(request.getContactNumber(), "contactNumber");
        if (contactError != null) return badRequest(contactError);
        String addressError = ValidationRules.maxLength(request.getAddress(), "address", 255);
        if (addressError != null) return badRequest(addressError);
        String clientTypeRequiredError = ValidationRules.required(request.getClientTypeId(), "clientTypeId");
        if (clientTypeRequiredError != null) return badRequest(clientTypeRequiredError);
        String financialLevelRequiredError = ValidationRules.required(request.getFinancialLevelId(), "financialLevelId");
        if (financialLevelRequiredError != null) return badRequest(financialLevelRequiredError);
        String leadSourceRequiredError = ValidationRules.required(request.getLeadSourceId(), "leadSourceId");
        if (leadSourceRequiredError != null) return badRequest(leadSourceRequiredError);
        return null;
    }

    private ClientDto toDto(Client client) {
        ClientType clientType = client.getClientType();
        FinancialLevel financialLevel = client.getFinancialLevel();
        RejectionReason rejectionReason = client.getRejectionReason();
        LeadSource leadSource = client.getLeadSource();
        SalesExecutive salesExecutive = client.getSalesExecutive();
        return new ClientDto(client.getId(), salesExecutive.getId(), client.getFullName(), client.getAddress(),
            client.getContactNumber(), clientType.getId(), clientType.getTypeName(),
                financialLevel == null ? null : financialLevel.getId(), financialLevel == null ? null : financialLevel.getLevelName(),
                rejectionReason == null ? null : rejectionReason.getId(), rejectionReason == null ? null : rejectionReason.getReasonName(),
                leadSource.getId(), leadSource.getSourceName(), client.getCreatedAt(), client.getUpdatedAt());
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", message)).build();
    }

    private Response conflict(String message) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ApiError(409, "Conflict", message)).build();
    }

    private boolean hasReferences(String query, Integer id) {
        return em.createQuery(query, Long.class).setParameter("id", id).getSingleResult() > 0;
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