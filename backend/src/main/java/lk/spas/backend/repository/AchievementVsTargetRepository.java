package lk.spas.backend.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lk.spas.backend.dto.AchievementVsTargetCandidate;

@ApplicationScoped
public class AchievementVsTargetRepository {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<AchievementVsTargetCandidate> findAchievementVsTarget(Integer seId,
            LocalDate startMonth, LocalDate endMonth) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.se_id, se.full_name, sl.level_name, a.month_year, a.target_amount, a.achieved_amount")
                .append(" FROM achievements a")
                .append(" JOIN sales_executives se ON se.id = a.se_id")
                .append(" JOIN se_levels sl ON sl.id = se.se_level_id")
                .append(" WHERE 1 = 1");

        int parameterIndex = 1;
        if (seId != null) {
            sql.append(" AND a.se_id = ?").append(parameterIndex++);
        }
        if (startMonth != null) {
            sql.append(" AND a.month_year >= ?").append(parameterIndex++);
        }
        if (endMonth != null) {
            sql.append(" AND a.month_year <= ?").append(parameterIndex++);
        }

        sql.append(" ORDER BY a.month_year ASC, a.se_id ASC");

        Query query = em.createNativeQuery(sql.toString());
        int index = 1;
        if (seId != null) {
            query.setParameter(index++, seId);
        }
        if (startMonth != null) {
            query.setParameter(index++, Date.valueOf(startMonth));
        }
        if (endMonth != null) {
            query.setParameter(index++, Date.valueOf(endMonth));
        }

        List<Object[]> rows = query.getResultList();
        List<AchievementVsTargetCandidate> candidates = new ArrayList<>();
        for (Object[] row : rows) {
            AchievementVsTargetCandidate candidate = new AchievementVsTargetCandidate();
            candidate.setSeId(toInteger(row[0]));
            candidate.setSeName(toString(row[1]));
            candidate.setSeLevelName(toString(row[2]));
            candidate.setMonth(toLocalDate(row[3]));
            candidate.setTargetAmount(toBigDecimal(row[4]));
            candidate.setAchievedAmount(toBigDecimal(row[5]));
            candidates.add(candidate);
        }
        return candidates;
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.valueOf(value.toString());
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        return new BigDecimal(value.toString());
    }

    private String toString(Object value) {
        return value == null ? null : value.toString();
    }

    private LocalDate toLocalDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        if (value instanceof Date date) {
            return date.toLocalDate();
        }
        return LocalDate.parse(value.toString());
    }
}
