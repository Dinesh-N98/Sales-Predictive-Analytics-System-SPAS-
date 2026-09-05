package lk.spas.backend.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lk.spas.backend.dto.HotLeadCandidate;

@ApplicationScoped
public class HotLeadsRepository {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    private static final String HOT_LEADS_SQL = "WITH sale_amounts AS ("
            + "    SELECT"
            + "        al.id AS activity_log_id,"
            + "        al.se_id,"
            + "        DATE_FORMAT(al.activity_date, '%Y-%m-01') AS month_period,"
            + "        al.activity_date,"
            + "        COALESCE(s.premium_amount, 0) AS sale_amount"
            + "    FROM activity_logs al"
            + "    LEFT JOIN sales s ON s.activity_log_id = al.id"
            + "), running_totals AS ("
            + "    SELECT"
            + "        activity_log_id,"
            + "        se_id,"
            + "        month_period,"
            + "        COALESCE(SUM(sale_amount) OVER ("
            + "            PARTITION BY se_id, month_period"
            + "            ORDER BY activity_date, activity_log_id"
            + "            ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING"
            + "        ), 0) AS running_achieved"
            + "    FROM sale_amounts"
            + ")"
            + "SELECT"
            + "    al.id AS id,"
            + "    al.se_id AS se_id,"
            + "    se.full_name AS se_name,"
            + "    al.client_id AS client_id,"
            + "    sl.level_name AS se_level_name,"
            + "    at.activity_name AS activity_name,"
            + "    al.duration_minutes AS duration_minutes,"
            + "    al.followup_count AS followup_count,"
            + "    ct.type_name AS client_type_name,"
            + "    fl.level_name AS financial_level_name,"
            + "    lsrc.source_name AS lead_source_name,"
            + "    p.policy_name AS policy_name,"
            + "    CASE WHEN cf.activity_log_id IS NOT NULL THEN 1 ELSE 0 END AS has_feedback,"
            + "    cf.rating AS rating,"
            + "    fs.strength_name AS strength_name,"
            + "    fi.improvement_name AS improvement_name,"
            + "    rt.running_achieved AS running_achieved,"
            + "    CASE"
            + "        WHEN ach.target_amount IS NOT NULL"
            + "             AND rt.running_achieved >= ach.target_amount"
            + "        THEN 1 ELSE 0"
            + "    END AS has_achieved_target,"
            + "    al.activity_date AS activity_date"
            + " FROM activity_logs al"
            + " JOIN sales_executives se ON se.id = al.se_id"
            + " JOIN se_levels sl ON sl.id = se.se_level_id"
            + " JOIN activity_types at ON at.id = al.activity_type_id"
            + " JOIN lead_statuses ls ON ls.id = al.status_id"
            + " JOIN clients c ON c.id = al.client_id"
            + " JOIN client_types ct ON ct.id = c.client_type_id"
            + " LEFT JOIN financial_levels fl ON fl.id = c.financial_level_id"
            + " JOIN lead_sources lsrc ON lsrc.id = c.lead_source_id"
            + " LEFT JOIN policies p ON p.id = al.client_policy_id"
            + " LEFT JOIN client_feedbacks cf ON cf.activity_log_id = al.id"
            + " LEFT JOIN feedback_strengths fs ON fs.id = cf.strength_id"
            + " LEFT JOIN feedback_improvements fi ON fi.id = cf.improvement_id"
            + " LEFT JOIN running_totals rt ON rt.activity_log_id = al.id"
            + " LEFT JOIN achievements ach ON ach.se_id = al.se_id"
            + "                             AND ach.month_year = DATE_FORMAT(al.activity_date, '%Y-%m-01')"
            + " WHERE ls.status_name = 'Pending'"
            + " ORDER BY al.se_id, al.activity_date, al.id";

    @SuppressWarnings("unchecked")
    public List<HotLeadCandidate> findPendingHotLeads() {
        List<Object[]> rows = em.createNativeQuery(HOT_LEADS_SQL).getResultList();
        List<HotLeadCandidate> candidates = new ArrayList<>();
        for (Object[] row : rows) {
            HotLeadCandidate candidate = new HotLeadCandidate();
            candidate.setId(toInteger(row[0]));
            candidate.setSeId(toInteger(row[1]));
            candidate.setSeName(toString(row[2]));
            candidate.setClientId(toInteger(row[3]));
            candidate.setSeLevelName(toString(row[4]));
            candidate.setActivityName(toString(row[5]));
            candidate.setDurationMinutes(toInteger(row[6]));
            candidate.setFollowupCount(toInteger(row[7]));
            candidate.setClientTypeName(toString(row[8]));
            candidate.setFinancialLevelName(toString(row[9]));
            candidate.setLeadSourceName(toString(row[10]));
            candidate.setPolicyName(toString(row[11]));
            candidate.setHasFeedback(toInteger(row[12]));
            candidate.setRating(toInteger(row[13]));
            candidate.setStrengthName(toString(row[14]));
            candidate.setImprovementName(toString(row[15]));
            candidate.setRunningAchieved(toDouble(row[16]));
            candidate.setHasAchievedTarget(toInteger(row[17]));
            candidate.setActivityDate(toLocalDate(row[18]));
            candidates.add(candidate);
        }
        return candidates;
    }

    public HotLeadCandidate findPendingHotLead(Integer activityLogId) {
        return findPendingHotLeads().stream()
                .filter(candidate -> activityLogId.equals(candidate.getId()))
                .findFirst()
                .orElse(null);
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text) {
            return Integer.valueOf(text);
        }
        return null;
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text) {
            return Double.valueOf(text);
        }
        return null;
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
        return null;
    }
}
