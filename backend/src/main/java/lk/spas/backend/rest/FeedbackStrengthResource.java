package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import lk.spas.backend.dto.FeedbackStrengthDto;
import lk.spas.backend.entity.FeedbackStrength;

@Path("feedback-strengths")
public class FeedbackStrengthResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<FeedbackStrengthDto> getAll() {
        List<FeedbackStrength> feedbackStrengths = em.createQuery("SELECT f FROM FeedbackStrength f", FeedbackStrength.class)
                .getResultList();
        return feedbackStrengths.stream()
                .map(f -> new FeedbackStrengthDto(f.getId(), f.getStrengthName()))
                .collect(Collectors.toList());
    }
}
