package lk.spas.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SaleCreateDto {

    private Integer clientId;
    private Integer policyId;
    private Integer seId;
    private LocalDate issueDate;
    private LocalDate renewalDate;
    private BigDecimal premiumAmount;
    private Integer statusId;
    private boolean hasClaimed;

    public SaleCreateDto() {
    }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }
    public Integer getPolicyId() { return policyId; }
    public void setPolicyId(Integer policyId) { this.policyId = policyId; }
    public Integer getSeId() { return seId; }
    public void setSeId(Integer seId) { this.seId = seId; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getRenewalDate() { return renewalDate; }
    public void setRenewalDate(LocalDate renewalDate) { this.renewalDate = renewalDate; }
    public BigDecimal getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(BigDecimal premiumAmount) { this.premiumAmount = premiumAmount; }
    public Integer getStatusId() { return statusId; }
    public void setStatusId(Integer statusId) { this.statusId = statusId; }
    public boolean isHasClaimed() { return hasClaimed; }
    public void setHasClaimed(boolean hasClaimed) { this.hasClaimed = hasClaimed; }
}