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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lk.spas.backend.dto.PolicyCreateDto;
import lk.spas.backend.dto.PolicyDto;
import lk.spas.backend.entity.Policy;
import lk.spas.backend.entity.PolicyCategory;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.security.Secured;
import lk.spas.backend.validation.ValidationRules;

@Path("policies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PolicyResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Secured({"MANAGER", "EXECUTIVE"})
    public List<PolicyDto> getAll() {
        return em.createQuery("SELECT p FROM Policy p", Policy.class).getResultList().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    @Secured({"MANAGER", "EXECUTIVE"})
    public Response getById(@PathParam("id") Integer id) {
        Policy policy = em.find(Policy.class, id);
        return policy == null ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(toDto(policy)).build();
    }

    @POST
    @Secured({"MANAGER"})
    @Transactional
    public Response create(PolicyCreateDto request, @Context UriInfo uriInfo) {
        Response validationError = validateRequest(request);
        if (validationError != null) return validationError;

        Policy policy = new Policy();
        Response categoryError = applyRequest(policy, request);
        if (categoryError != null) return categoryError;
        em.persist(policy);
        em.flush();

        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(policy.getId())).build();
        return Response.created(location).entity(toDto(policy)).build();
    }

    @PUT
    @Path("{id}")
    @Secured({"MANAGER"})
    @Transactional
    public Response update(@PathParam("id") Integer id, PolicyCreateDto request) {
        Policy policy = em.find(Policy.class, id);
        if (policy == null) return Response.status(Response.Status.NOT_FOUND).build();

        Response validationError = validateRequest(request);
        if (validationError != null) return validationError;
        Response categoryError = applyRequest(policy, request);
        return categoryError != null ? categoryError : Response.ok(toDto(policy)).build();
    }

    @DELETE
    @Path("{id}")
    @Secured({"MANAGER"})
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        Policy policy = em.find(Policy.class, id);
        if (policy == null) return Response.status(Response.Status.NOT_FOUND).build();
        if (em.createQuery("SELECT COUNT(a) FROM ActivityLog a WHERE a.clientPolicy.id = :id", Long.class)
            .setParameter("id", id).getSingleResult() > 0
            || em.createQuery("SELECT COUNT(s) FROM Sale s WHERE s.policy.id = :id", Long.class)
            .setParameter("id", id).getSingleResult() > 0) {
            return Response.status(Response.Status.CONFLICT)
                .entity(new ApiError(409, "Conflict", "Policy cannot be deleted while activities or sales reference it"))
                .build();
        }
        em.remove(policy);
        return Response.noContent().build();
    }

    private Response applyRequest(Policy policy, PolicyCreateDto request) {
        PolicyCategory category = em.find(PolicyCategory.class, request.getPolicyCategoryId());
        if (category == null) return badRequest("Invalid policyCategoryId: policy category not found");

        policy.setPolicyCategory(category);
        policy.setPolicyName(request.getPolicyName());
        policy.setPolicyDetails(request.getPolicyDetails());
        return null;
    }

    private Response validateRequest(PolicyCreateDto request) {
        if (request == null) return badRequest("Request body is required");
        if (request.getPolicyCategoryId() == null) return badRequest("policyCategoryId is required");
        if (request.getPolicyName() == null || request.getPolicyName().isBlank()) {
            return badRequest("policyName is required");
        }
        String nameLengthError = ValidationRules.maxLength(request.getPolicyName(), "policyName", 100);
        if (nameLengthError != null) return badRequest(nameLengthError);
        String detailsLengthError = ValidationRules.maxLength(request.getPolicyDetails(), "policyDetails", 10000);
        if (detailsLengthError != null) return badRequest(detailsLengthError);
        return null;
    }

    private PolicyDto toDto(Policy policy) {
        PolicyCategory category = policy.getPolicyCategory();
        return new PolicyDto(policy.getId(), category.getId(), category.getCategoryName(),
                policy.getPolicyName(), policy.getPolicyDetails());
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", message)).build();
    }
}