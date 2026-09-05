package lk.spas.backend.dto;

import java.math.BigDecimal;

public class SePaceCandidate {

    private Integer seId;
    private String seName;
    private String seLevelName;
    private int activityCount;
    private double avgFollowupCount;
    private double avgDurationMinutes;
    private double soldRate;
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

    public int getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(int activityCount) {
        this.activityCount = activityCount;
    }

    public double getAvgFollowupCount() {
        return avgFollowupCount;
    }

    public void setAvgFollowupCount(double avgFollowupCount) {
        this.avgFollowupCount = avgFollowupCount;
    }

    public double getAvgDurationMinutes() {
        return avgDurationMinutes;
    }

    public void setAvgDurationMinutes(double avgDurationMinutes) {
        this.avgDurationMinutes = avgDurationMinutes;
    }

    public double getSoldRate() {
        return soldRate;
    }

    public void setSoldRate(double soldRate) {
        this.soldRate = soldRate;
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