package lk.spas.backend.dto;

public class ClientFeedbackCreateDto {

    private Integer activityLogId;
    private Integer clientId;
    private Integer rating;
    private Integer strengthId;
    private Integer improvementId;
    private String comments;

    public ClientFeedbackCreateDto() {
    }

    public Integer getActivityLogId() { return activityLogId; }
    public void setActivityLogId(Integer activityLogId) { this.activityLogId = activityLogId; }
    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public Integer getStrengthId() { return strengthId; }
    public void setStrengthId(Integer strengthId) { this.strengthId = strengthId; }
    public Integer getImprovementId() { return improvementId; }
    public void setImprovementId(Integer improvementId) { this.improvementId = improvementId; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}