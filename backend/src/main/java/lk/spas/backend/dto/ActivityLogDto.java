package lk.spas.backend.dto;

import java.sql.Timestamp;
import java.time.LocalDate;

public class ActivityLogDto {

    private Integer id;
    private Integer seId;
    private Integer clientId;
    private int followupCount;
    private String clientName;
    private Integer activityTypeId;
    private String activityTypeName;
    private Integer statusId;
    private String statusName;
    private LocalDate activityDate;
    private Integer clientPolicyId;
    private String clientPolicyName;
    private LocalDate nextFollowUpDate;
    private String remarks;
    private Integer durationMinutes;
    private String feedbackToken;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public ActivityLogDto() {
    }

    public ActivityLogDto(Integer id, Integer seId, Integer clientId, int followupCount, String clientName,
            Integer activityTypeId, String activityTypeName, Integer statusId, String statusName,
            LocalDate activityDate, Integer clientPolicyId, String clientPolicyName,
            LocalDate nextFollowUpDate, String remarks, Integer durationMinutes,
            String feedbackToken, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.seId = seId;
        this.clientId = clientId;
        this.followupCount = followupCount;
        this.clientName = clientName;
        this.activityTypeId = activityTypeId;
        this.activityTypeName = activityTypeName;
        this.statusId = statusId;
        this.statusName = statusName;
        this.activityDate = activityDate;
        this.clientPolicyId = clientPolicyId;
        this.clientPolicyName = clientPolicyName;
        this.nextFollowUpDate = nextFollowUpDate;
        this.remarks = remarks;
        this.durationMinutes = durationMinutes;
        this.feedbackToken = feedbackToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getSeId() { return seId; }
    public void setSeId(Integer seId) { this.seId = seId; }
    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }
    public int getFollowupCount() { return followupCount; }
    public void setFollowupCount(int followupCount) { this.followupCount = followupCount; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public Integer getActivityTypeId() { return activityTypeId; }
    public void setActivityTypeId(Integer activityTypeId) { this.activityTypeId = activityTypeId; }
    public String getActivityTypeName() { return activityTypeName; }
    public void setActivityTypeName(String activityTypeName) { this.activityTypeName = activityTypeName; }
    public Integer getStatusId() { return statusId; }
    public void setStatusId(Integer statusId) { this.statusId = statusId; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public LocalDate getActivityDate() { return activityDate; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }
    public Integer getClientPolicyId() { return clientPolicyId; }
    public void setClientPolicyId(Integer clientPolicyId) { this.clientPolicyId = clientPolicyId; }
    public String getClientPolicyName() { return clientPolicyName; }
    public void setClientPolicyName(String clientPolicyName) { this.clientPolicyName = clientPolicyName; }
    public LocalDate getNextFollowUpDate() { return nextFollowUpDate; }
    public void setNextFollowUpDate(LocalDate nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getFeedbackToken() { return feedbackToken; }
    public void setFeedbackToken(String feedbackToken) { this.feedbackToken = feedbackToken; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}