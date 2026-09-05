package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import lk.spas.backend.dto.PublicFeedbackCreateDto;
import lk.spas.backend.entity.ActivityLog;
import lk.spas.backend.entity.ClientFeedback;
import lk.spas.backend.entity.FeedbackImprovement;
import lk.spas.backend.entity.FeedbackStrength;
import lk.spas.backend.validation.ValidationRules;

@Path("public/client-feedbacks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicFeedbackResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @POST
    @Path("{token}")
    @Transactional
    public Response submit(@PathParam("token") String token, PublicFeedbackCreateDto request) {
        ActivityLog activityLog = em.createQuery(
                "SELECT a FROM ActivityLog a WHERE a.feedbackToken = :token", ActivityLog.class)
                .setParameter("token", token).getResultStream().findFirst().orElse(null);
        if (activityLog == null) return Response.status(Response.Status.NOT_FOUND).build();
        if (request == null) return badRequest("Request body is required");
        if (request.getRating() == null) return badRequest("rating is required");
        if (request.getRating() < 1 || request.getRating() > 5) return badRequest("rating must be between 1 and 5");
        String commentsError = ValidationRules.maxLength(request.getComments(), "comments", 10000);
        if (commentsError != null) return badRequest(commentsError);
        if (request.getStrengthId() != null && em.find(FeedbackStrength.class, request.getStrengthId()) == null) {
            return badRequest("Invalid strengthId: feedback strength not found");
        }
        if (request.getImprovementId() != null && em.find(FeedbackImprovement.class, request.getImprovementId()) == null) {
            return badRequest("Invalid improvementId: feedback improvement not found");
        }
        Long feedbackCount = em.createQuery(
                "SELECT COUNT(f) FROM ClientFeedback f WHERE f.activityLog.id = :activityLogId", Long.class)
                .setParameter("activityLogId", activityLog.getId()).getSingleResult();
        if (feedbackCount > 0) return badRequest("Feedback already submitted for this activity");

        ClientFeedback feedback = new ClientFeedback();
        feedback.setActivityLog(activityLog);
        feedback.setClient(activityLog.getClient());
        feedback.setSalesExecutive(activityLog.getSalesExecutive());
        feedback.setRating(request.getRating());
        feedback.setStrength(request.getStrengthId() == null ? null : em.find(FeedbackStrength.class, request.getStrengthId()));
        feedback.setImprovement(request.getImprovementId() == null ? null : em.find(FeedbackImprovement.class, request.getImprovementId()));
        feedback.setComments(request.getComments());
        em.persist(feedback);
        em.flush();
        return Response.status(Response.Status.CREATED).entity(Map.of("id", feedback.getId())).build();
    }

    private Response badRequest(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("message", message)).build();
    }
}
