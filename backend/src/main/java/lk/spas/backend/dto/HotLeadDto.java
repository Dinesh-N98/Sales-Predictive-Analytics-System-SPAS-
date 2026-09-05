package lk.spas.backend.dto;

public class HotLeadDto {

    private Integer activityLogId;
    private Integer seId;
    private String seName;
    private Integer clientId;
    private String activityName;
    private String prediction;
    private Double probabilitySold;

    public HotLeadDto() {
    }

    public HotLeadDto(Integer activityLogId, Integer seId, String seName, Integer clientId,
            String activityName, String prediction, Double probabilitySold) {
        this.activityLogId = activityLogId;
        this.seId = seId;
        this.seName = seName;
        this.clientId = clientId;
        this.activityName = activityName;
        this.prediction = prediction;
        this.probabilitySold = probabilitySold;
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

    public Double getProbabilitySold() {
        return probabilitySold;
    }

    public void setProbabilitySold(Double probabilitySold) {
        this.probabilitySold = probabilitySold;
    }
}
