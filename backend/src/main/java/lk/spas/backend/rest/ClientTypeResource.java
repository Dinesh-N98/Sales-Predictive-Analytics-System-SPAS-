package lk.spas.backend.rest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;
import lk.spas.backend.dto.ClientTypeDto;
import lk.spas.backend.entity.ClientType;

@Path("client-types")
public class ClientTypeResource {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClientTypeDto> getAll() {
        List<ClientType> clientTypes = em.createQuery("SELECT c FROM ClientType c", ClientType.class)
                .getResultList();
        return clientTypes.stream()
                .map(c -> new ClientTypeDto(c.getId(), c.getTypeName()))
                .collect(Collectors.toList());
    }
}
