package lk.spas.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "achievements", uniqueConstraints = @UniqueConstraint(columnNames = {"se_id", "month_year"}))
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "se_id", nullable = false)
    private SalesExecutive salesExecutive;

    @Column(name = "target_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "achieved_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal achievedAmount = BigDecimal.ZERO;

    @Column(name = "month_year", nullable = false)
    private LocalDate monthYear;

    public Achievement() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public SalesExecutive getSalesExecutive() { return salesExecutive; }
    public void setSalesExecutive(SalesExecutive salesExecutive) { this.salesExecutive = salesExecutive; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public BigDecimal getAchievedAmount() { return achievedAmount; }
    public void setAchievedAmount(BigDecimal achievedAmount) { this.achievedAmount = achievedAmount; }
    public LocalDate getMonthYear() { return monthYear; }
    public void setMonthYear(LocalDate monthYear) { this.monthYear = monthYear; }
}