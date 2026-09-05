package lk.spas.backend.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lk.spas.backend.dto.SePerformanceCandidate;

@ApplicationScoped
public class SePerformanceRepository {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    private static final String SQL = "SELECT"
            + " se.id AS se_id,"
            + " se.full_name AS se_name,"
            + " sl.level_name AS se_level_name,"
            + " COALESCE(sales_stats.sales_count, 0) AS sales_count,"
            + " COALESCE(sales_stats.total_sales_amount, 0) AS total_sales_amount,"
            + " COALESCE(ach.target_amount, 0) AS target_amount,"
            + " COALESCE(ach.achieved_amount, 0) AS achieved_amount"
            + " FROM sales_executives se"
            + " JOIN se_levels sl ON sl.id = se.se_level_id"
            + " LEFT JOIN ("
            + "     SELECT s.se_id, COUNT(*) AS sales_count, SUM(s.premium_amount) AS total_sales_amount"
            + "     FROM sales s"
            + "     WHERE s.issue_date >= ?1"
            + "       AND s.issue_date < DATE_ADD(?2, INTERVAL 1 MONTH)"
            + "     GROUP BY s.se_id"
            + " ) sales_stats ON sales_stats.se_id = se.id"
            + " LEFT JOIN achievements ach ON ach.se_id = se.id AND ach.month_year = ?3"
            + " WHERE se.is_active = 1"
            + " ORDER BY se.id";

    @SuppressWarnings("unchecked")
    public List<SePerformanceCandidate> findActiveSePerformanceCandidates(LocalDate monthStart) {
        List<Object[]> rows = em.createNativeQuery(SQL)
                .setParameter(1, Date.valueOf(monthStart))
                .setParameter(2, Date.valueOf(monthStart))
                .setParameter(3, Date.valueOf(monthStart))
                .getResultList();

        List<SePerformanceCandidate> candidates = new ArrayList<>();
        for (Object[] row : rows) {
            SePerformanceCandidate candidate = new SePerformanceCandidate();
            candidate.setSeId(toInteger(row[0]));
            candidate.setSeName(toString(row[1]));
            candidate.setSeLevelName(toString(row[2]));
            candidate.setSalesCount(toInteger(row[3]));
            candidate.setTotalSalesAmount(toBigDecimal(row[4]));
            candidate.setTargetAmount(toBigDecimal(row[5]));
            candidate.setAchievedAmount(toBigDecimal(row[6]));
            candidates.add(candidate);
        }
        return candidates;
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return 0;
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
}
