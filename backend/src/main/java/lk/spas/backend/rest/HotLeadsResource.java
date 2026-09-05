package lk.spas.backend.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lk.spas.backend.dto.HotLeadDto;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.HotLeadsService;

@Path("dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class HotLeadsResource {

    @Inject
    private HotLeadsService hotLeadsService;

    @GET
    @Path("hot-leads")
    @Secured({"MANAGER"})
    public Response getHotLeads(@QueryParam("limit") Integer limit) {
        int normalizedLimit = limit == null ? 10 : limit;
        if (normalizedLimit < 1) {
            normalizedLimit = 10;
        }
        if (normalizedLimit > 50) {
            normalizedLimit = 50;
        }

        try {
            List<HotLeadDto> hotLeads = hotLeadsService.getHotLeads(normalizedLimit);
            return Response.ok(hotLeads).build();
        } catch (RuntimeException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ApiError(503, "Service Unavailable",
                            "ML service is unavailable: " + ex.getMessage()))
                    .build();
        }
    }
}
