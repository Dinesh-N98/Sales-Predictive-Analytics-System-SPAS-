package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import lk.spas.backend.dto.ActivityTypeDto;
import lk.spas.backend.entity.ActivityType;

@Path("activity-types")
public class ActivityTypeResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ActivityTypeDto> getAll() {
        List<ActivityType> activityTypes = em.createQuery("SELECT a FROM ActivityType a", ActivityType.class)
                .getResultList();
        return activityTypes.stream()
                .map(a -> new ActivityTypeDto(a.getId(), a.getActivityName()))
                .collect(Collectors.toList());
    }
}
