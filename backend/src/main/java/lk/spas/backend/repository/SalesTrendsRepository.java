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
import lk.spas.backend.dto.SalesTrendDto;

@ApplicationScoped
public class SalesTrendsRepository {

    @PersistenceContext(unitName = "spasPU")
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<SalesTrendDto> findSalesTrends(Integer seId, Integer seLevelId,
            LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DATE(s.issue_date) AS sale_date, COUNT(*) AS sales_count, COALESCE(SUM(s.premium_amount), 0) AS total_amount")
                .append(" FROM sales s");

        if (seLevelId != null) {
            sql.append(" JOIN sales_executives se ON se.id = s.se_id")
                    .append(" JOIN se_levels sl ON sl.id = se.se_level_id");
        }

        sql.append(" WHERE 1 = 1");

        int parameterIndex = 1;
        if (seId != null) {
            sql.append(" AND s.se_id = ?").append(parameterIndex++);
        }
        if (seLevelId != null) {
            sql.append(" AND sl.id = ?").append(parameterIndex++);
        }
        if (startDate != null) {
            sql.append(" AND s.issue_date >= ?").append(parameterIndex++);
        }
        if (endDate != null) {
            sql.append(" AND s.issue_date <= ?").append(parameterIndex++);
        }

        sql.append(" GROUP BY DATE(s.issue_date)")
                .append(" ORDER BY DATE(s.issue_date) ASC");

        Query query = em.createNativeQuery(sql.toString());
        int index = 1;
        if (seId != null) {
            query.setParameter(index++, seId);
        }
        if (seLevelId != null) {
            query.setParameter(index++, seLevelId);
        }
        if (startDate != null) {
            query.setParameter(index++, Date.valueOf(startDate));
        }
        if (endDate != null) {
            query.setParameter(index++, Date.valueOf(endDate));
        }

        List<Object[]> rows = query.getResultList();
        List<SalesTrendDto> trends = new ArrayList<>();
        for (Object[] row : rows) {
            SalesTrendDto dto = new SalesTrendDto();
            dto.setDate(toLocalDate(row[0]));
            dto.setSalesCount(toInteger(row[1]));
            dto.setTotalAmount(toBigDecimal(row[2]));
            trends.add(dto);
        }
        return trends;
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
