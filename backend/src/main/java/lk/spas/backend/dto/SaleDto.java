package lk.spas.backend.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

public class SaleDto {

    private Integer id;
    private Integer clientId;
    private String clientName;
    private Integer policyId;
    private String policyName;
    private Integer seId;
    private String seName;
    private Integer activityLogId;
    private LocalDate issueDate;
    private LocalDate renewalDate;
    private BigDecimal premiumAmount;
    private Integer statusId;
    private String statusName;
    private boolean hasClaimed;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public SaleDto() {
    }

    public SaleDto(Integer id, Integer clientId, String clientName, Integer policyId, String policyName,
            Integer seId, String seName, Integer activityLogId, LocalDate issueDate, LocalDate renewalDate, BigDecimal premiumAmount,
            Integer statusId, String statusName, boolean hasClaimed, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.clientId = clientId;
        this.clientName = clientName;
        this.policyId = policyId;
        this.policyName = policyName;
        this.seId = seId;
        this.seName = seName;
        this.activityLogId = activityLogId;
        this.issueDate = issueDate;
        this.renewalDate = renewalDate;
        this.premiumAmount = premiumAmount;
        this.statusId = statusId;
        this.statusName = statusName;
        this.hasClaimed = hasClaimed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public Integer getPolicyId() { return policyId; }
    public void setPolicyId(Integer policyId) { this.policyId = policyId; }
    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }
    public Integer getSeId() { return seId; }
    public void setSeId(Integer seId) { this.seId = seId; }
    public String getSeName() { return seName; }
    public void setSeName(String seName) { this.seName = seName; }
    public Integer getActivityLogId() { return activityLogId; }
    public void setActivityLogId(Integer activityLogId) { this.activityLogId = activityLogId; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getRenewalDate() { return renewalDate; }
    public void setRenewalDate(LocalDate renewalDate) { this.renewalDate = renewalDate; }
    public BigDecimal getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(BigDecimal premiumAmount) { this.premiumAmount = premiumAmount; }
    public Integer getStatusId() { return statusId; }
    public void setStatusId(Integer statusId) { this.statusId = statusId; }
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    public boolean isHasClaimed() { return hasClaimed; }
    public void setHasClaimed(boolean hasClaimed) { this.hasClaimed = hasClaimed; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}