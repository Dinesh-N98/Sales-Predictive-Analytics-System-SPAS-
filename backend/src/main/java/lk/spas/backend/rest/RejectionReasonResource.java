package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import lk.spas.backend.dto.RejectionReasonDto;
import lk.spas.backend.entity.RejectionReason;

@Path("rejection-reasons")
public class RejectionReasonResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<RejectionReasonDto> getAll() {
        List<RejectionReason> rejectionReasons = em.createQuery("SELECT r FROM RejectionReason r", RejectionReason.class)
                .getResultList();
        return rejectionReasons.stream()
                .map(r -> new RejectionReasonDto(r.getId(), r.getReasonName()))
                .collect(Collectors.toList());
    }
}
