package lk.spas.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AchievementDto {

    private Integer id;
    private Integer seId;
    private String seName;
    private BigDecimal targetAmount;
    private BigDecimal achievedAmount;
    private LocalDate monthYear;

    public AchievementDto() {
    }

    public AchievementDto(Integer id, Integer seId, String seName, BigDecimal targetAmount,
            BigDecimal achievedAmount, LocalDate monthYear) {
        this.id = id;
        this.seId = seId;
        this.seName = seName;
        this.targetAmount = targetAmount;
        this.achievedAmount = achievedAmount;
        this.monthYear = monthYear;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getSeId() { return seId; }
    public void setSeId(Integer seId) { this.seId = seId; }
    public String getSeName() { return seName; }
    public void setSeName(String seName) { this.seName = seName; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    public BigDecimal getAchievedAmount() { return achievedAmount; }
    public void setAchievedAmount(BigDecimal achievedAmount) { this.achievedAmount = achievedAmount; }
    public LocalDate getMonthYear() { return monthYear; }
    public void setMonthYear(LocalDate monthYear) { this.monthYear = monthYear; }
}