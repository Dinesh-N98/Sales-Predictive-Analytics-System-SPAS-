package lk.spas.backend.dto;

import java.time.LocalDate;

public class HotLeadCandidate {

    private Integer id;
    private Integer seId;
    private String seName;
    private Integer clientId;
    private String seLevelName;
    private String activityName;
    private Integer durationMinutes;
    private Integer followupCount;
    private String clientTypeName;
    private String financialLevelName;
    private String leadSourceName;
    private String policyName;
    private Integer hasFeedback;
    private Integer rating;
    private String strengthName;
    private String improvementName;
    private Double runningAchieved;
    private Integer hasAchievedTarget;
    private LocalDate activityDate;

    public HotLeadCandidate() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getSeLevelName() {
        return seLevelName;
    }

    public void setSeLevelName(String seLevelName) {
        this.seLevelName = seLevelName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getFollowupCount() {
        return followupCount;
    }

    public void setFollowupCount(Integer followupCount) {
        this.followupCount = followupCount;
    }

    public String getClientTypeName() {
        return clientTypeName;
    }

    public void setClientTypeName(String clientTypeName) {
        this.clientTypeName = clientTypeName;
    }

    public String getFinancialLevelName() {
        return financialLevelName;
    }

    public void setFinancialLevelName(String financialLevelName) {
        this.financialLevelName = financialLevelName;
    }

    public String getLeadSourceName() {
        return leadSourceName;
    }

    public void setLeadSourceName(String leadSourceName) {
        this.leadSourceName = leadSourceName;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public Integer getHasFeedback() {
        return hasFeedback;
    }

    public void setHasFeedback(Integer hasFeedback) {
        this.hasFeedback = hasFeedback;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getStrengthName() {
        return strengthName;
    }

    public void setStrengthName(String strengthName) {
        this.strengthName = strengthName;
    }

    public String getImprovementName() {
        return improvementName;
    }

    public void setImprovementName(String improvementName) {
        this.improvementName = improvementName;
    }

    public Double getRunningAchieved() {
        return runningAchieved;
    }

    public void setRunningAchieved(Double runningAchieved) {
        this.runningAchieved = runningAchieved;
    }

    public Integer getHasAchievedTarget() {
        return hasAchievedTarget;
    }

    public void setHasAchievedTarget(Integer hasAchievedTarget) {
        this.hasAchievedTarget = hasAchievedTarget;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }
}
