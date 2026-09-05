package lk.spas.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AchievementVsTargetCandidate {

    private Integer seId;
    private String seName;
    private String seLevelName;
    private LocalDate month;
    private BigDecimal targetAmount;
    private BigDecimal achievedAmount;

    public Integer getSeId() {
        return seId;
    }

    public void setSeId(Integer seId) {
        this.seId = seId;
    }

    public String getSeName() {
        return seName;
    }

    public void setSeName(String seName) {
        this.seName = seName;
    }

    public String getSeLevelName() {
        return seLevelName;
    }

    public void setSeLevelName(String seLevelName) {
        this.seLevelName = seLevelName;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getAchievedAmount() {
        return achievedAmount;
    }

    public void setAchievedAmount(BigDecimal achievedAmount) {
        this.achievedAmount = achievedAmount;
    }
}
