package lk.spas.backend.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lk.spas.backend.dto.AtRiskActivityDto;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.AtRiskActivitiesService;

@Path("dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class AtRiskActivitiesResource {

    @Inject
    private AtRiskActivitiesService atRiskActivitiesService;

    @GET
    @Path("at-risk-activities")
    @Secured({"MANAGER"})
    public Response getAtRiskActivities(@QueryParam("limit") Integer limit) {
        int normalizedLimit = limit == null ? 10 : limit;
        if (normalizedLimit < 1) {
            normalizedLimit = 10;
        }
        if (normalizedLimit > 50) {
            normalizedLimit = 50;
        }

        try {
            List<AtRiskActivityDto> activities = atRiskActivitiesService.getAtRiskActivities(normalizedLimit);
            return Response.ok(activities).build();
        } catch (RuntimeException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ApiError(503, "Service Unavailable",
                            "ML service is unavailable: " + ex.getMessage()))
                    .build();
        }
    }
}