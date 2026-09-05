package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import lk.spas.backend.dto.FinancialLevelDto;
import lk.spas.backend.entity.FinancialLevel;

@Path("financial-levels")
public class FinancialLevelResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<FinancialLevelDto> getAll() {
        List<FinancialLevel> financialLevels = em.createQuery("SELECT f FROM FinancialLevel f", FinancialLevel.class)
                .getResultList();
        return financialLevels.stream()
                .map(f -> new FinancialLevelDto(f.getId(), f.getLevelName()))
                .collect(Collectors.toList());
    }
}
