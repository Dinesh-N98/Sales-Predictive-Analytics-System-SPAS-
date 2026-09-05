package lk.spas.backend.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import lk.spas.backend.dto.SalesTrendDto;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.SalesTrendsService;

@Path("dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class SalesTrendsResource {

    @Inject
    private SalesTrendsService service;

    @GET
    @Path("sales-trends")
    @Secured({"MANAGER"})
    public Response getSalesTrends(@QueryParam("seId") Integer seId,
            @QueryParam("seLevelId") Integer seLevelId,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate) {
        try {
            List<SalesTrendDto> trends = service.getSalesTrends(seId, seLevelId, startDate, endDate);
            return Response.ok(trends).build();
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(400, "Bad Request", ex.getMessage()))
                    .build();
        }
    }
}
