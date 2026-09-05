package lk.spas.backend.dto;

import java.sql.Timestamp;
import java.time.LocalDate;

public class LastPolicyDto {

    private Integer policyId;
    private String policyName;
    private LocalDate activityDate;
    private Timestamp createdAt;

    public LastPolicyDto() {
    }

    public LastPolicyDto(Integer policyId, String policyName, LocalDate activityDate, Timestamp createdAt) {
        this.policyId = policyId;
        this.policyName = policyName;
        this.activityDate = activityDate;
        this.createdAt = createdAt;
    }

    public Integer getPolicyId() { return policyId; }
    public void setPolicyId(Integer policyId) { this.policyId = policyId; }
    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }
    public LocalDate getActivityDate() { return activityDate; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
