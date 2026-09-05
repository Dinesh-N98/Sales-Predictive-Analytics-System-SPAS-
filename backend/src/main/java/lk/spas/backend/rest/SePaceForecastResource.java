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
import lk.spas.backend.dto.SePaceForecastDto;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.SePaceForecastService;

@Path("dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class SePaceForecastResource {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    @Inject
    private SePaceForecastService service;

    @GET
    @Path("se-pace-forecast")
    @Secured({"MANAGER"})
    public Response getSePaceForecast(@QueryParam("month") String month) {
        final YearMonth requestedMonth;
        try {
            requestedMonth = month == null || month.isBlank() ? YearMonth.now() : YearMonth.parse(month, MONTH_FORMAT);
        } catch (DateTimeParseException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(400, "Bad Request", "month must use yyyy-MM format"))
                    .build();
        }

        try {
            List<SePaceForecastDto> forecasts = service.getForecast(requestedMonth);
            return Response.ok(forecasts).build();
        } catch (RuntimeException ex) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new ApiError(503, "Service Unavailable",
                            "ML service is unavailable: " + ex.getMessage()))
                    .build();
        }
    }
}