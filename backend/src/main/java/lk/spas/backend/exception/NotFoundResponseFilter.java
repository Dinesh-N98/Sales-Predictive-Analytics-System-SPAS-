package lk.spas.backend.exception;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        if (responseContext.getStatus() == 404 && responseContext.getEntity() == null) {
                responseContext.setEntity(new ApiError(404, "Not Found", "The requested resource does not exist"));
            responseContext.getHeaders().putSingle("Content-Type", MediaType.APPLICATION_JSON);
        }
    }
}
