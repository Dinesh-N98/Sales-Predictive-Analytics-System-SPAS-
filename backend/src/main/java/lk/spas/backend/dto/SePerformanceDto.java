package lk.spas.backend.dto;

import java.math.BigDecimal;

public class SePerformanceDto {

    private Integer seId;
    private String seName;
    private String seLevelName;
    private Integer salesCount;
    private BigDecimal totalSalesAmount;
    private BigDecimal achievedAmount;
    private BigDecimal targetAmount;
    private Double achievementPercentage;

    public SePerformanceDto() {
    }

    public SePerformanceDto(Integer seId, String seName, String seLevelName, Integer salesCount,
            BigDecimal totalSalesAmount, BigDecimal achievedAmount, BigDecimal targetAmount,
            Double achievementPercentage) {
        this.seId = seId;
        this.seName = seName;
        this.seLevelName = seLevelName;
        this.salesCount = salesCount;
        this.totalSalesAmount = totalSalesAmount;
        this.achievedAmount = achievedAmount;
        this.targetAmount = targetAmount;
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

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }

    public BigDecimal getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(BigDecimal totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    public BigDecimal getAchievedAmount() {
        return achievedAmount;
    }

    public void setAchievedAmount(BigDecimal achievedAmount) {
        this.achievedAmount = achievedAmount;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public Double getAchievementPercentage() {
        return achievementPercentage;
    }

    public void setAchievementPercentage(Double achievementPercentage) {
        this.achievementPercentage = achievementPercentage;
    }
}
