package lk.spas.backend.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lk.spas.backend.dto.SePaceCandidate;

@ApplicationScoped
public class SePaceForecastRepository {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    private static final String SQL = "SELECT se.id, se.full_name, sl.level_name,"
            + " COALESCE(activity_stats.activity_count, 0),"
            + " COALESCE(activity_stats.avg_followup_count, 0),"
            + " COALESCE(activity_stats.avg_duration_minutes, 0),"
            + " COALESCE(activity_stats.sold_rate, 0),"
            + " COALESCE(ach.target_amount, 0), COALESCE(ach.achieved_amount, 0)"
            + " FROM sales_executives se"
            + " JOIN se_levels sl ON sl.id = se.se_level_id"
            + " LEFT JOIN ("
            + "   SELECT al.se_id, COUNT(*) AS activity_count,"
            + "      AVG(al.followup_count) AS avg_followup_count,"
            + "      AVG(al.duration_minutes) AS avg_duration_minutes,"
            + "      SUM(CASE WHEN ls.status_name = 'Sold' THEN 1 ELSE 0 END) / COUNT(*) AS sold_rate"
            + "   FROM activity_logs al"
            + "   JOIN lead_statuses ls ON ls.id = al.status_id"
            + "   WHERE al.activity_date >= ?1"
            + "     AND al.activity_date < DATE_ADD(?2, INTERVAL 1 MONTH)"
            + "   GROUP BY al.se_id"
            + " ) activity_stats ON activity_stats.se_id = se.id"
            + " LEFT JOIN achievements ach ON ach.se_id = se.id AND ach.month_year = ?3"
            + " WHERE se.is_active = 1"
            + " ORDER BY se.id";

    @SuppressWarnings("unchecked")
    public List<SePaceCandidate> findActiveSePaceCandidates(LocalDate monthStart) {
        List<Object[]> rows = em.createNativeQuery(SQL)
            .setParameter(1, Date.valueOf(monthStart))
            .setParameter(2, Date.valueOf(monthStart))
            .setParameter(3, Date.valueOf(monthStart))
                .getResultList();
        List<SePaceCandidate> candidates = new ArrayList<>();
        for (Object[] row : rows) {
            SePaceCandidate candidate = new SePaceCandidate();
            candidate.setSeId(toInteger(row[0]));
            candidate.setSeName(toString(row[1]));
            candidate.setSeLevelName(toString(row[2]));
            candidate.setActivityCount(toInteger(row[3]));
            candidate.setAvgFollowupCount(toDouble(row[4]));
            candidate.setAvgDurationMinutes(toDouble(row[5]));
            candidate.setSoldRate(toDouble(row[6]));
            candidate.setTargetAmount(toBigDecimal(row[7]));
            candidate.setAchievedAmount(toBigDecimal(row[8]));
            candidates.add(candidate);
        }
        return candidates;
    }

    public SePaceCandidate findActiveSePaceCandidate(Integer seId, LocalDate monthStart) {
        if (!hasActiveTarget(seId, monthStart)) {
            return null;
        }
        return findActiveSePaceCandidates(monthStart).stream()
                .filter(candidate -> seId.equals(candidate.getSeId()))
                .findFirst()
                .orElse(null);
    }

    public boolean hasActiveTarget(Integer seId, LocalDate monthStart) {
        Number count = (Number) em.createNativeQuery(
                "SELECT COUNT(*) FROM achievements WHERE se_id = ?1 AND month_year = ?2 AND target_amount IS NOT NULL")
                .setParameter(1, seId)
                .setParameter(2, Date.valueOf(monthStart))
                .getSingleResult();
        return count.intValue() > 0;
    }

    private Integer toInteger(Object value) {
        return value instanceof Number number ? number.intValue() : Integer.valueOf(value.toString());
    }

    private double toDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : Double.parseDouble(value.toString());
    }

    private BigDecimal toBigDecimal(Object value) {
        return value instanceof BigDecimal decimal ? decimal : new BigDecimal(value.toString());
    }

    private String toString(Object value) {
        return value == null ? null : value.toString();
    }
}