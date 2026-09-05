package lk.spas.backend.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lk.spas.backend.dto.ActivityOutcomeDetailDto;
import lk.spas.backend.dto.SeTargetForecastDetailDto;
import lk.spas.backend.exception.ApiError;
import lk.spas.backend.security.Secured;
import lk.spas.backend.service.DecisionAidService;

@Path("decision-aid")
@Produces(MediaType.APPLICATION_JSON)
public class DecisionAidResource {
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
    @Inject private DecisionAidService service;

    @GET @Path("activity-outcome/{activityLogId}") @Secured({"MANAGER"})
    public Response getActivityOutcome(@PathParam("activityLogId") Integer activityLogId) {
        try {
            ActivityOutcomeDetailDto result = service.getActivityOutcome(activityLogId);
            return result == null ? notFound() : Response.ok(result).build();
        } catch (RuntimeException ex) { return unavailable(ex); }
    }

    @GET @Path("se-target-forecast/{seId}") @Secured({"MANAGER"})
    public Response getSeTargetForecast(@PathParam("seId") Integer seId, @QueryParam("month") String month) {
        final YearMonth requestedMonth;
        try {
            requestedMonth = month == null || month.isBlank() ? YearMonth.now() : YearMonth.parse(month, MONTH_FORMAT);
        } catch (DateTimeParseException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError(400, "Bad Request", "month must use yyyy-MM format")).build();
        }
        try {
            SeTargetForecastDetailDto result = service.getSeTargetForecast(seId, requestedMonth);
            return result == null ? notFound() : Response.ok(result).build();
        } catch (RuntimeException ex) { return unavailable(ex); }
    }

    private Response notFound() {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ApiError(404, "Not Found", "No prediction is available for this selection")).build();
    }

    private Response unavailable(RuntimeException ex) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(new ApiError(503, "Service Unavailable", "ML service is unavailable: " + ex.getMessage())).build();
    }
}