package lk.spas.backend.ml;

import jakarta.json.bind.annotation.JsonbProperty;

public class ActivityOutcomeRequestItem {

    private int id;

    @JsonbProperty("se_level_name")
    private String seLevelName;

    @JsonbProperty("activity_name")
    private String activityName;

    @JsonbProperty("duration_minutes")
    private double durationMinutes;

    @JsonbProperty("followup_count")
    private int followupCount;

    @JsonbProperty("client_type_name")
    private String clientTypeName;

    @JsonbProperty("financial_level_name")
    private String financialLevelName;

    @JsonbProperty("lead_source_name")
    private String leadSourceName;

    @JsonbProperty("policy_name")
    private String policyName;

    @JsonbProperty("has_feedback")
    private int hasFeedback;

    private Double rating;

    @JsonbProperty("strength_name")
    private String strengthName;

    @JsonbProperty("improvement_name")
    private String improvementName;

    @JsonbProperty("running_achieved")
    private double runningAchieved;

    @JsonbProperty("has_achieved_target")
    private int hasAchievedTarget;

    @JsonbProperty("day_of_week")
    private String dayOfWeek;

    public ActivityOutcomeRequestItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getFollowupCount() {
        return followupCount;
    }

    public void setFollowupCount(int followupCount) {
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

    public int getHasFeedback() {
        return hasFeedback;
    }

    public void setHasFeedback(int hasFeedback) {
        this.hasFeedback = hasFeedback;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
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

    public double getRunningAchieved() {
        return runningAchieved;
    }

    public void setRunningAchieved(double runningAchieved) {
        this.runningAchieved = runningAchieved;
    }

    public int getHasAchievedTarget() {
        return hasAchievedTarget;
    }

    public void setHasAchievedTarget(int hasAchievedTarget) {
        this.hasAchievedTarget = hasAchievedTarget;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
