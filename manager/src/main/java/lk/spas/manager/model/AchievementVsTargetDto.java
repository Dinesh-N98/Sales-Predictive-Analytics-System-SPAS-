package lk.spas.manager.model;

import java.math.BigDecimal;
import java.time.YearMonth;

public class AchievementVsTargetDto {
    private Integer seId;
    private String seName;
    private String seLevelName;
    private YearMonth month;
    private BigDecimal targetAmount;
    private BigDecimal achievedAmount;
    private Double achievementPercentage;

    public AchievementVsTargetDto() {
    }

    public AchievementVsTargetDto(Integer seId, String seName, String seLevelName, YearMonth month,
            BigDecimal targetAmount, BigDecimal achievedAmount, Double achievementPercentage) {
        this.seId = seId;
        this.seName = seName;
        this.seLevelName = seLevelName;
        this.month = month;
        this.targetAmount = targetAmount;
        this.achievedAmount = achievedAmount;
        this.achievementPercentage = achievementPercentage;
    }

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

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
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

    public Double getAchievementPercentage() {
        return achievementPercentage;
    }

    public void setAchievementPercentage(Double achievementPercentage) {
        this.achievementPercentage = achievementPercentage;
    }
}
