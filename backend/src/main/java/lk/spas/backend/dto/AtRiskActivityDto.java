package lk.spas.backend.dto;

public class AtRiskActivityDto {

    private Integer activityLogId;
    private Integer seId;
    private String seName;
    private Integer clientId;
    private String activityName;
    private String prediction;
    private Double probabilityRejected;

    public AtRiskActivityDto() {
    }

    public AtRiskActivityDto(Integer activityLogId, Integer seId, String seName, Integer clientId,
            String activityName, String prediction, Double probabilityRejected) {
        this.activityLogId = activityLogId;
        this.seId = seId;
        this.seName = seName;
        this.clientId = clientId;
        this.activityName = activityName;
        this.prediction = prediction;
        this.probabilityRejected = probabilityRejected;
    }

    public Integer getActivityLogId() {
        return activityLogId;
    }

    public void setActivityLogId(Integer activityLogId) {
        this.activityLogId = activityLogId;
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

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public Double getProbabilityRejected() {
        return probabilityRejected;
    }

    public void setProbabilityRejected(Double probabilityRejected) {
        this.probabilityRejected = probabilityRejected;
    }
}