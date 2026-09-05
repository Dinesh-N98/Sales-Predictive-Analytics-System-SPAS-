package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import lk.spas.backend.dto.SeLevelDto;
import lk.spas.backend.entity.SeLevel;

@Path("se-levels")
public class SeLevelResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SeLevelDto> getAll() {
        List<SeLevel> levels = em.createQuery("SELECT s FROM SeLevel s", SeLevel.class)
                .getResultList();
        return levels.stream()
                .map(s -> new SeLevelDto(s.getId(), s.getLevelName()))
                .collect(Collectors.toList());
    }
}