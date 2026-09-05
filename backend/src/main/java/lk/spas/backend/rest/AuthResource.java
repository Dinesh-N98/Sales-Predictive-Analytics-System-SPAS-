package lk.spas.backend.rest;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.spas.backend.dto.LoginRequestDto;
import lk.spas.backend.dto.LoginResponseDto;
import lk.spas.backend.entity.Manager;
import lk.spas.backend.entity.SalesExecutive;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.security.JwtUtil;

@Path("auth")
public class AuthResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequestDto request) {
        if (request == null || request.getPhoneNumber() == null || request.getPassword() == null
                || request.getPhoneNumber().isBlank() || request.getPassword().isBlank()) {
            return unauthorizedResponse();
        }

        // Try manager first
        try {
            Manager manager = em.createQuery(
                    "SELECT m FROM Manager m WHERE m.phoneNumber = :phoneNumber", Manager.class)
                    .setParameter("phoneNumber", request.getPhoneNumber())
                    .getSingleResult();

            if (manager.isActive() && verify(request.getPassword(), manager.getPasswordHash())) {
                String token = JwtUtil.generateToken(String.valueOf(manager.getId()), "MANAGER");
                return Response.ok(new LoginResponseDto(token, "MANAGER", manager.getFullName())).build();
            }
            return unauthorizedResponse();
        } catch (NoResultException ex) {
            // not a manager, fall through to check sales executives
        }

        // Try sales executive
        try {
            SalesExecutive exec = em.createQuery(
                    "SELECT s FROM SalesExecutive s WHERE s.phoneNumber = :phoneNumber", SalesExecutive.class)
                    .setParameter("phoneNumber", request.getPhoneNumber())
                    .getSingleResult();

            if (exec.isActive() && verify(request.getPassword(), exec.getPasswordHash())) {
                String token = JwtUtil.generateToken(String.valueOf(exec.getId()), "EXECUTIVE");
                return Response.ok(new LoginResponseDto(token, "EXECUTIVE", exec.getFullName())).build();
            }
        } catch (NoResultException ex) {
            // not found — fall through to generic 401
        }

        return unauthorizedResponse();
    }

    private boolean verify(String rawPassword, String hash) {
        return BCrypt.verifyer().verify(rawPassword.toCharArray(), hash).verified;
    }

    private Response unauthorizedResponse() {
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity(new ApiError(401, "Unauthorized", "Invalid phone number or password"))
                .build();
    }
}