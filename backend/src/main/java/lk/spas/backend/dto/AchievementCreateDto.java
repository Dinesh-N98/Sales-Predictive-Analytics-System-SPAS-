package lk.spas.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AchievementCreateDto {

    private Integer seId;
    private BigDecimal targetAmount;
    private BigDecimal achievedAmount;
    private LocalDate monthYear;

    public AchievementCreateDto() {
    }

    public Integer getSeId() { return seId; }
    public void setSeId(Integer seId) { this.seId = seId; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public BigDecimal getAchievedAmount() { return achievedAmount; }
    public void setAchievedAmount(BigDecimal achievedAmount) { this.achievedAmount = achievedAmount; }
    public LocalDate getMonthYear() { return monthYear; }
    public void setMonthYear(LocalDate monthYear) { this.monthYear = monthYear; }
}