package lk.spas.backend.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.SecurityContext;
import lk.spas.backend.entity.Manager;
import lk.spas.backend.entity.SalesExecutive;

@ApplicationScoped
public class CurrentUser {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    public SalesExecutive getExecutive(SecurityContext securityContext) {
        if (securityContext == null || securityContext.getUserPrincipal() == null) return null;
        try {
            Integer executiveId = Integer.valueOf(securityContext.getUserPrincipal().getName());
            return em.find(SalesExecutive.class, executiveId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public boolean isActive(SecurityContext securityContext, String role) {
        if (securityContext == null || securityContext.getUserPrincipal() == null || role == null) return false;
        try {
            Integer userId = Integer.valueOf(securityContext.getUserPrincipal().getName());
            if ("EXECUTIVE".equals(role)) {
                SalesExecutive executive = em.find(SalesExecutive.class, userId);
                return executive != null && executive.isActive();
            }
            if ("MANAGER".equals(role)) {
                Manager manager = em.find(Manager.class, userId);
                return manager != null && manager.isActive();
            }
        } catch (NumberFormatException ex) {
            return false;
        }
        return false;
    }
}