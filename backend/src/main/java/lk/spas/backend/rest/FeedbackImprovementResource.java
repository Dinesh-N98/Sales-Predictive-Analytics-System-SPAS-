package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import lk.spas.backend.dto.FeedbackImprovementDto;
import lk.spas.backend.entity.FeedbackImprovement;

@Path("feedback-improvements")
public class FeedbackImprovementResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<FeedbackImprovementDto> getAll() {
        List<FeedbackImprovement> feedbackImprovements = em.createQuery("SELECT f FROM FeedbackImprovement f", FeedbackImprovement.class)
                .getResultList();
        return feedbackImprovements.stream()
                .map(f -> new FeedbackImprovementDto(f.getId(), f.getImprovementName()))
                .collect(Collectors.toList());
    }
}
