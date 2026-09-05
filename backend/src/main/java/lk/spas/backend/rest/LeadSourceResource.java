package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import lk.spas.backend.dto.LeadSourceDto;
import lk.spas.backend.entity.LeadSource;

@Path("lead-sources")
public class LeadSourceResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<LeadSourceDto> getAll() {
        List<LeadSource> leadSources = em.createQuery("SELECT l FROM LeadSource l", LeadSource.class)
                .getResultList();
        return leadSources.stream()
                .map(l -> new LeadSourceDto(l.getId(), l.getSourceName()))
                .collect(Collectors.toList());
    }
}
