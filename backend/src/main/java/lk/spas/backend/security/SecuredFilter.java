package lk.spas.backend.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import lk.spas.backend.exception.ApiError;

@Secured({})
@Provider
@Priority(Priorities.AUTHORIZATION)
public class SecuredFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method method = resourceInfo.getResourceMethod();
        Secured secured = method.getAnnotation(Secured.class);
        if (secured == null) {
            secured = resourceInfo.getResourceClass().getAnnotation(Secured.class);
        }

        String[] allowedRoles = secured.value();
        SecurityContext securityContext = requestContext.getSecurityContext();

        for (String role : allowedRoles) {
            if (securityContext.isUserInRole(role)) {
                return; // allowed
            }
        }

        requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                    .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                    .entity(new ApiError(403, "Forbidden", "Forbidden: insufficient role"))
                        .build());
    }
}