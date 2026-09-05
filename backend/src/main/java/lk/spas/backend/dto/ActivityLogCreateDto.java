package lk.spas.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ActivityLogCreateDto {

    private Integer clientId;
    private Integer activityTypeId;
    private Integer statusId;
    private BigDecimal premiumAmount;
    private LocalDate activityDate;
    private Integer clientPolicyId;
    private LocalDate nextFollowUpDate;
    private String remarks;
    private Integer durationMinutes;

    public ActivityLogCreateDto() {
    }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }
    public Integer getActivityTypeId() { return activityTypeId; }
    public void setActivityTypeId(Integer activityTypeId) { this.activityTypeId = activityTypeId; }
    public Integer getStatusId() { return statusId; }
    public void setStatusId(Integer statusId) { this.statusId = statusId; }
    public BigDecimal getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(BigDecimal premiumAmount) { this.premiumAmount = premiumAmount; }
    public LocalDate getActivityDate() { return activityDate; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }
    public Integer getClientPolicyId() { return clientPolicyId; }
    public void setClientPolicyId(Integer clientPolicyId) { this.clientPolicyId = clientPolicyId; }
    public LocalDate getNextFollowUpDate() { return nextFollowUpDate; }
    public void setNextFollowUpDate(LocalDate nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}