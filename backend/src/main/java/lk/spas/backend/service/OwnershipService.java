package lk.spas.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.SecurityContext;
import lk.spas.backend.entity.SalesExecutive;
import lk.spas.backend.security.CurrentUser;

@ApplicationScoped
public class OwnershipService {

    @Inject
    private CurrentUser currentUser;

    public OwnershipService() {
    }

    public SalesExecutive getCurrentExecutive(SecurityContext securityContext) {
        return currentUser.getExecutive(securityContext);
    }

    public Integer currentExecutiveId(SecurityContext securityContext) {
        SalesExecutive executive = getCurrentExecutive(securityContext);
        return executive == null ? -1 : executive.getId();
    }

    public boolean belongsToCurrentExecutive(SalesExecutive executive, SecurityContext securityContext) {
        SalesExecutive current = getCurrentExecutive(securityContext);
        return current != null && executive != null && current.getId().equals(executive.getId());
    }
}
