package lk.spas.manager.model;

public class ActivityOutcomeDetailDto {
    private Integer activityLogId;
    private Integer seId;
    private String seName;
    private Integer clientId;
    private String activityName;
    private String prediction;
    private Double probabilitySold;
    private Double probabilityPending;
    private Double probabilityRejected;

    public Integer getActivityLogId() { return activityLogId; }
    public void setActivityLogId(Integer value) { activityLogId = value; }
    public Integer getSeId() { return seId; }
    public void setSeId(Integer value) { seId = value; }
    public String getSeName() { return seName; }
    public void setSeName(String value) { seName = value; }
    public Integer getClientId() { return clientId; }
    public void setClientId(Integer value) { clientId = value; }
    public String getActivityName() { return activityName; }
    public void setActivityName(String value) { activityName = value; }
    public String getPrediction() { return prediction; }
    public void setPrediction(String value) { prediction = value; }
    public Double getProbabilitySold() { return probabilitySold; }
    public void setProbabilitySold(Double value) { probabilitySold = value; }
    public Double getProbabilityPending() { return probabilityPending; }
    public void setProbabilityPending(Double value) { probabilityPending = value; }
    public Double getProbabilityRejected() { return probabilityRejected; }
    public void setProbabilityRejected(Double value) { probabilityRejected = value; }
}