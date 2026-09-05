package lk.spas.backend.security;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import lk.spas.backend.exception.ApiError;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    @jakarta.inject.Inject
    private CurrentUser currentUser;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return;
        }

        String path = normalizePath(requestContext.getUriInfo().getPath());
        if (isPublicPath(path)) {
            return;
        }

        String authorization = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            abortUnauthorized(requestContext);
            return;
        }

        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            abortUnauthorized(requestContext);
            return;
        }

        try {
            Claims claims = JwtUtil.validateToken(token);
            String subject = claims.getSubject();
            String role = claims.get("role", String.class);
            JwtUtil.validateClaims(subject, role);
            SecurityContext currentContext = requestContext.getSecurityContext();
            SecurityContext authenticatedContext = new JwtSecurityContext(subject, role, currentContext);
            if (!currentUser.isActive(authenticatedContext, role)) {
                abortUnauthorized(requestContext);
                return;
            }
            requestContext.setSecurityContext(authenticatedContext);
        } catch (Exception ex) {
            abortUnauthorized(requestContext);
        }
    }

    private void abortUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
            .entity(new ApiError(401, "Unauthorized", "Unauthorized"))
                .build());
    }

    private String normalizePath(String path) {
        if (path == null) return "";
        String normalized = path.trim();
        while (normalized.startsWith("/")) normalized = normalized.substring(1);
        if (normalized.startsWith("api/")) normalized = normalized.substring(4);
        return normalized;
    }

    private boolean isPublicPath(String path) {
        return "auth/login".equals(path)
                || (path.startsWith("public/client-feedbacks/")
                && path.length() > "public/client-feedbacks/".length());
    }

    private static class JwtSecurityContext implements SecurityContext {

        private final String subject;
        private final String role;
        private final SecurityContext delegate;

        JwtSecurityContext(String subject, String role, SecurityContext delegate) {
            this.subject = subject;
            this.role = role;
            this.delegate = delegate;
        }

        @Override
        public Principal getUserPrincipal() {
            return () -> subject;
        }

        @Override
        public boolean isUserInRole(String role) {
            return this.role != null && this.role.equals(role);
        }

        @Override
        public boolean isSecure() {
            return delegate != null && delegate.isSecure();
        }

        @Override
        public String getAuthenticationScheme() {
            return "Bearer";
        }
    }
}
