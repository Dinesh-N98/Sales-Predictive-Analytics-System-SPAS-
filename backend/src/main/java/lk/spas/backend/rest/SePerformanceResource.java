package lk.spas.backend.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import lk.spas.backend.dto.SePerformanceDto;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.SePerformanceService;

@Path("dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class SePerformanceResource {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Inject
    private SePerformanceService service;

    @GET
    @Path("se-performance")
    @Secured({"MANAGER"})
    public Response getSePerformance(@QueryParam("month") String month) {
        final YearMonth requestedMonth;
        try {
            requestedMonth = month == null || month.isBlank() ? YearMonth.now() : YearMonth.parse(month, MONTH_FORMAT);
        } catch (DateTimeParseException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(400, "Bad Request", "month must use yyyy-MM format"))
                    .build();
        }

        List<SePerformanceDto> performance = service.getPerformance(requestedMonth);
        return Response.ok(performance).build();
    }
}
