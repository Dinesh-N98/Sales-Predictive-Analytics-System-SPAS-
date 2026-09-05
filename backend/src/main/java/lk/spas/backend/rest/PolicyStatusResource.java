package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import lk.spas.backend.dto.PolicyStatusDto;
import lk.spas.backend.entity.PolicyStatus;

@Path("policy-statuses")
public class PolicyStatusResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PolicyStatusDto> getAll() {
        List<PolicyStatus> policyStatuses = em.createQuery("SELECT p FROM PolicyStatus p", PolicyStatus.class)
                .getResultList();
        return policyStatuses.stream()
                .map(p -> new PolicyStatusDto(p.getId(), p.getStatusName()))
                .collect(Collectors.toList());
    }
}
