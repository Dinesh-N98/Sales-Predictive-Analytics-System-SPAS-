package lk.spas.backend.dto;

import java.sql.Timestamp;

public class ClientFeedbackDto {

    private Integer id;
    private Integer activityLogId;
    private Integer clientId;
    private String clientName;
    private Integer seId;
    private String seName;
    private Integer rating;
    private Integer strengthId;
    private String strengthName;
    private Integer improvementId;
    private String improvementName;
    private String comments;
    private Timestamp createdAt;

    public ClientFeedbackDto() {
    }

    public ClientFeedbackDto(Integer id, Integer activityLogId, Integer clientId, String clientName,
            Integer seId, String seName, Integer rating, Integer strengthId, String strengthName,
            Integer improvementId, String improvementName, String comments, Timestamp createdAt) {
        this.id = id;
        this.activityLogId = activityLogId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.seId = seId;
        this.seName = seName;
        this.rating = rating;
        this.strengthId = strengthId;
        this.strengthName = strengthName;
        this.improvementId = improvementId;
        this.improvementName = improvementName;
        this.comments = comments;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getActivityLogId() { return activityLogId; }
    public void setActivityLogId(Integer activityLogId) { this.activityLogId = activityLogId; }
    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public Integer getSeId() { return seId; }
    public void setSeId(Integer seId) { this.seId = seId; }
    public String getSeName() { return seName; }
    public void setSeName(String seName) { this.seName = seName; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public Integer getStrengthId() { return strengthId; }
    public void setStrengthId(Integer strengthId) { this.strengthId = strengthId; }
    public String getStrengthName() { return strengthName; }
    public void setStrengthName(String strengthName) { this.strengthName = strengthName; }
    public Integer getImprovementId() { return improvementId; }
    public void setImprovementId(Integer improvementId) { this.improvementId = improvementId; }
    public String getImprovementName() { return improvementName; }
    public void setImprovementName(String improvementName) { this.improvementName = improvementName; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}