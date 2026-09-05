package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import lk.spas.backend.dto.LeadStatusDto;
import lk.spas.backend.entity.LeadStatus;

@Path("lead-statuses")
public class LeadStatusResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<LeadStatusDto> getAll() {
        List<LeadStatus> leadStatuses = em.createQuery("SELECT l FROM LeadStatus l", LeadStatus.class)
                .getResultList();
        return leadStatuses.stream()
                .map(l -> new LeadStatusDto(l.getId(), l.getStatusName()))
                .collect(Collectors.toList());
    }
}
