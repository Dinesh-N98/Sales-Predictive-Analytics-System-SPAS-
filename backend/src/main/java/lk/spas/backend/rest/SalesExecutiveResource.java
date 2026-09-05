package lk.spas.backend.rest;

import lk.spas.backend.security.CurrentUser;
import lk.spas.backend.security.Secured;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
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
import lk.spas.backend.dto.SalesExecutiveCreateDto;
import lk.spas.backend.dto.SalesExecutiveDto;
import lk.spas.backend.dto.SalesExecutiveSelfUpdateDto;
import lk.spas.backend.entity.SalesExecutive;
import lk.spas.backend.entity.SeLevel;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.validation.ValidationRules;

@Path("sales-executives")
@Secured({"MANAGER"})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SalesExecutiveResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @jakarta.inject.Inject
    private CurrentUser currentUser;

    @GET
    @Secured({"MANAGER"})
    public List<SalesExecutiveDto> getAll(@QueryParam("page") Integer page, @QueryParam("size") Integer size) {
        int normalizedPage = normalizePage(page);
        int normalizedSize = normalizeSize(size);
        List<SalesExecutive> executives = em.createQuery("SELECT s FROM SalesExecutive s ORDER BY s.id ASC", SalesExecutive.class)
                .setFirstResult(normalizedPage * normalizedSize)
                .setMaxResults(normalizedSize)
                .getResultList();
        return executives.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GET
    @Path("me")
    @Secured({"EXECUTIVE"})
    public Response getCurrentProfile(@Context SecurityContext securityContext) {
        SalesExecutive executive = currentUser.getExecutive(securityContext);
        if (executive == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(toDto(executive)).build();
    }

    @GET
    @Path("{id}")
    @Secured({"MANAGER"})
    public Response getById(@PathParam("id") Integer id) {
        SalesExecutive executive = em.find(SalesExecutive.class, id);
        if (executive == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(toDto(executive)).build();
    }

    @POST
    @Transactional
    public Response create(SalesExecutiveCreateDto request, @Context UriInfo uriInfo) {
        Response validationError = validateCreateRequest(request, true);
        if (validationError != null) {
            return validationError;
        }

        SeLevel seLevel = em.find(SeLevel.class, request.getSeLevelId());
        if (seLevel == null) {
            return badRequest("Invalid seLevelId: se level not found");
        }

        SalesExecutive executive = new SalesExecutive();
        executive.setFullName(request.getFullName());
        executive.setActive(request.isActive());
        executive.setSeLevel(seLevel);
        executive.setEmail(request.getEmail());
        executive.setPhoneNumber(request.getPhoneNumber());
        executive.setPasswordHash(hashPassword(request.getPassword()));

        try {
            em.persist(executive);
            em.flush();
        } catch (PersistenceException e) {
            return badRequest("Email already exists");
        }

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(executive.getId()))
                .build();

        return Response.created(location).entity(toDto(executive)).build();
    }

    @PUT
    @Path("me")
    @Secured({"EXECUTIVE"})
    @Transactional
    public Response updateCurrentProfile(SalesExecutiveSelfUpdateDto request, @Context SecurityContext securityContext) {
        SalesExecutive executive = currentUser.getExecutive(securityContext);
        if (executive == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Response validationError = validateSelfUpdateRequest(request);
        if (validationError != null) {
            return validationError;
        }

        executive.setFullName(request.getFullName());
        executive.setEmail(request.getEmail());
        executive.setPhoneNumber(request.getPhoneNumber());

        return Response.ok(toDto(executive)).build();
    }

    @PUT
    @Path("{id}")
    @Secured({"MANAGER"})
    @Transactional
    public Response update(@PathParam("id") Integer id, SalesExecutiveCreateDto request) {
        SalesExecutive executive = em.find(SalesExecutive.class, id);
        if (executive == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Response validationError = validateCreateRequest(request, false);
        if (validationError != null) {
            return validationError;
        }

        SeLevel seLevel = em.find(SeLevel.class, request.getSeLevelId());
        if (seLevel == null) {
            return badRequest("Invalid seLevelId: se level not found");
        }

        executive.setFullName(request.getFullName());
        executive.setActive(request.isActive());
        executive.setSeLevel(seLevel);
        executive.setEmail(request.getEmail());
        executive.setPhoneNumber(request.getPhoneNumber());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            executive.setPasswordHash(hashPassword(request.getPassword()));
        }

        return Response.ok(toDto(executive)).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Integer id) {
        SalesExecutive executive = em.find(SalesExecutive.class, id);
        if (executive == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (hasReferences("SELECT COUNT(c) FROM Client c WHERE c.salesExecutive.id = :id", id)
                || hasReferences("SELECT COUNT(s) FROM Sale s WHERE s.salesExecutive.id = :id", id)
                || hasReferences("SELECT COUNT(a) FROM ActivityLog a WHERE a.salesExecutive.id = :id", id)
                || hasReferences("SELECT COUNT(a) FROM Achievement a WHERE a.salesExecutive.id = :id", id)
                || hasReferences("SELECT COUNT(f) FROM ClientFeedback f WHERE f.salesExecutive.id = :id", id)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiError(409, "Conflict", "Sales executive cannot be deleted while business records reference it"))
                    .build();
        }

        em.remove(executive);
        return Response.noContent().build();
    }

    private SalesExecutiveDto toDto(SalesExecutive executive) {
        SeLevel seLevel = executive.getSeLevel();
        return new SalesExecutiveDto(
                executive.getId(),
                executive.getFullName(),
                executive.isActive(),
                executive.getEmail(),
                executive.getPhoneNumber(),
                seLevel != null ? seLevel.getId() : null,
                seLevel != null ? seLevel.getLevelName() : null);
    }

    private String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private Response validateCreateRequest(SalesExecutiveCreateDto request, boolean requirePassword) {
        if (request == null) {
            return badRequest("Request body is required");
        }
        String fullNameError = ValidationRules.required(request.getFullName(), "fullName");
        if (fullNameError != null) return badRequest(fullNameError);
        String fullNameLengthError = ValidationRules.maxLength(request.getFullName(), "fullName", 100);
        if (fullNameLengthError != null) return badRequest(fullNameLengthError);
        if (request.getSeLevelId() == null) {
            return badRequest("seLevelId is required");
        }
        String emailRequiredError = ValidationRules.required(request.getEmail(), "email");
        if (emailRequiredError != null) return badRequest(emailRequiredError);
        String emailError = ValidationRules.email(request.getEmail(), "email");
        if (emailError != null) return badRequest(emailError);
        String phoneRequiredError = ValidationRules.required(request.getPhoneNumber(), "phoneNumber");
        if (phoneRequiredError != null) return badRequest(phoneRequiredError);
        String phoneError = ValidationRules.phone(request.getPhoneNumber(), "phoneNumber");
        if (phoneError != null) return badRequest(phoneError);
        if (request.getEmail().length() > 100) return badRequest("email must not exceed 100 characters");
        if (request.getPassword() != null && request.getPassword().length() > 255) {
            return badRequest("password must not exceed 255 characters");
        }
        if (requirePassword && (request.getPassword() == null || request.getPassword().isBlank())) {
            return badRequest("password is required");
        }
        return null;
    }

    private Response validateSelfUpdateRequest(SalesExecutiveSelfUpdateDto request) {
        if (request == null) {
            return badRequest("Request body is required");
        }
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            return badRequest("fullName is required");
        }
        String emailRequiredError = ValidationRules.required(request.getEmail(), "email");
        if (emailRequiredError != null) return badRequest(emailRequiredError);
        String emailError = ValidationRules.email(request.getEmail(), "email");
        if (emailError != null) return badRequest(emailError);
        String phoneRequiredError = ValidationRules.required(request.getPhoneNumber(), "phoneNumber");
        if (phoneRequiredError != null) return badRequest(phoneRequiredError);
        String phoneError = ValidationRules.phone(request.getPhoneNumber(), "phoneNumber");
        if (phoneError != null) return badRequest(phoneError);
        if (request.getEmail().length() > 100) return badRequest("email must not exceed 100 characters");
        return null;
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("message", message))
                .build();
    }

    private boolean hasReferences(String query, Integer id) {
        return em.createQuery(query, Long.class).setParameter("id", id).getSingleResult() > 0;
    }

    private int normalizePage(Integer page) {
        return page == null || page < 0 ? 0 : page;
    }

    private int normalizeSize(Integer size) {
        return size == null || size <= 0 ? 20 : Math.min(size, 100);
    }
}
