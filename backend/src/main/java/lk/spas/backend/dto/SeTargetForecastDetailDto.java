package lk.spas.backend.dto;

import java.math.BigDecimal;

public class SeTargetForecastDetailDto {
    private Integer seId;
    private String seName;
    private String seLevelName;
    private BigDecimal targetAmount;
    private BigDecimal achievedAmount;
    private Integer prediction;
    private Double probabilityHitTarget;

    public SeTargetForecastDetailDto() {
    }

    public SeTargetForecastDetailDto(Integer seId, String seName, String seLevelName, BigDecimal targetAmount,
            BigDecimal achievedAmount, Integer prediction, Double probabilityHitTarget) {
        this.seId = seId;
        this.seName = seName;
        this.seLevelName = seLevelName;
        this.targetAmount = targetAmount;
        this.achievedAmount = achievedAmount;
        this.prediction = prediction;
        this.probabilityHitTarget = probabilityHitTarget;
    }

    public Integer getSeId() { return seId; }
    public void setSeId(Integer value) { seId = value; }
    public String getSeName() { return seName; }
    public void setSeName(String value) { seName = value; }
    public String getSeLevelName() { return seLevelName; }
    public void setSeLevelName(String value) { seLevelName = value; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal value) { targetAmount = value; }
    public BigDecimal getAchievedAmount() { return achievedAmount; }
    public void setAchievedAmount(BigDecimal value) { achievedAmount = value; }
    public Integer getPrediction() { return prediction; }
    public void setPrediction(Integer value) { prediction = value; }
    public Double getProbabilityHitTarget() { return probabilityHitTarget; }
    public void setProbabilityHitTarget(Double value) { probabilityHitTarget = value; }
}