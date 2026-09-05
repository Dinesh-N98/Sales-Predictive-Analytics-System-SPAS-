package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import lk.spas.backend.dto.PolicyCategoryDto;
import lk.spas.backend.entity.PolicyCategory;

@Path("policy-categories")
public class PolicyCategoryResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PolicyCategoryDto> getAll() {
        List<PolicyCategory> policyCategories = em.createQuery("SELECT p FROM PolicyCategory p", PolicyCategory.class)
                .getResultList();
        return policyCategories.stream()
                .map(p -> new PolicyCategoryDto(p.getId(), p.getCategoryName()))
                .collect(Collectors.toList());
    }
}
