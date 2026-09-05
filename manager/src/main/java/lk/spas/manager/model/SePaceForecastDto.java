package lk.spas.manager.model;

import java.math.BigDecimal;

public class SePaceForecastDto {
    private Integer seId;
    private String seName;
    private String seLevelName;
    private BigDecimal targetAmount;
    private BigDecimal achievedAmount;
    private Integer prediction;
    private Double probabilityHitTarget;

    public SePaceForecastDto() {
    }

    public SePaceForecastDto(Integer seId, String seName, String seLevelName, BigDecimal targetAmount,
            BigDecimal achievedAmount, Integer prediction, Double probabilityHitTarget) {
        this.seId = seId;
        this.seName = seName;
        this.seLevelName = seLevelName;
        this.targetAmount = targetAmount;
        this.achievedAmount = achievedAmount;
        this.prediction = prediction;
        this.probabilityHitTarget = probabilityHitTarget;
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

    public Integer getPrediction() {
        return prediction;
    }

    public void setPrediction(Integer prediction) {
        this.prediction = prediction;
    }

    public Double getProbabilityHitTarget() {
        return probabilityHitTarget;
    }

    public void setProbabilityHitTarget(Double probabilityHitTarget) {
        this.probabilityHitTarget = probabilityHitTarget;
    }
}
