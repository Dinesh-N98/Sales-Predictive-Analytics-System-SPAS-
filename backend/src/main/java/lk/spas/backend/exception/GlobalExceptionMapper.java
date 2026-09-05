package lk.spas.backend.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            WebApplicationException webException = (WebApplicationException) exception;
            int status = webException.getResponse().getStatus();
            return errorResponse(status, messageForStatus(status));
        }

        return errorResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                "An unexpected server error occurred");
    }

    private Response errorResponse(int status, String message) {
        Response.Status responseStatus = Response.Status.fromStatusCode(status);
        String error = responseStatus == null ? "Error" : responseStatus.getReasonPhrase();
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ApiError(status, error, message))
                .build();
    }

    private String messageForStatus(int status) {
        switch (status) {
            case 400:
                return "The request is invalid";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "The requested resource does not exist";
            case 409:
                return "The request conflicts with existing data";
            default:
                return status >= 500 ? "An unexpected server error occurred" : "Request failed";
        }
    }
}
