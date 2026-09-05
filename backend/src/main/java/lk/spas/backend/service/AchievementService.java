package lk.spas.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import lk.spas.backend.entity.Achievement;
import lk.spas.backend.entity.SalesExecutive;

@ApplicationScoped
public class AchievementService {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @Inject
    public AchievementService() {
    }

    public void applySaleToAchievement(int seId, LocalDate saleDate, BigDecimal premiumAmount) {
        LocalDate monthYear = saleDate.withDayOfMonth(1);
        Achievement achievement = em.createQuery(
                "SELECT a FROM Achievement a WHERE a.salesExecutive.id = :seId AND a.monthYear = :monthYear",
                Achievement.class)
                .setParameter("seId", seId)
                .setParameter("monthYear", monthYear)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (achievement != null) {
            achievement.setAchievedAmount(achievement.getAchievedAmount().add(premiumAmount));
            return;
        }

        Achievement newAchievement = new Achievement();
        newAchievement.setSalesExecutive(em.find(SalesExecutive.class, seId));
        newAchievement.setMonthYear(monthYear);
        newAchievement.setTargetAmount(BigDecimal.ZERO);
        newAchievement.setAchievedAmount(premiumAmount);
        em.persist(newAchievement);
    }
}
