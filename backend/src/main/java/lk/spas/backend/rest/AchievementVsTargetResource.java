package lk.spas.backend.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lk.spas.backend.dto.AchievementVsTargetDto;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.AchievementVsTargetService;

@Path("dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class AchievementVsTargetResource {

    @Inject
    private AchievementVsTargetService service;

    @GET
    @Path("achievement-vs-target")
    @Secured({"MANAGER"})
    public Response getAchievementVsTarget(@QueryParam("seId") Integer seId,
            @QueryParam("startMonth") String startMonth,
            @QueryParam("endMonth") String endMonth) {
        try {
            List<AchievementVsTargetDto> rows = service.getAchievementVsTarget(seId, startMonth, endMonth);
            return Response.ok(rows).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(400, "Bad Request", ex.getMessage()))
                    .build();
        }
    }
}
