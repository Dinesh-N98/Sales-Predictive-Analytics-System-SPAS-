package lk.spas.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Timestamp updatedAt;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    // Immutable historical snapshot; unlike clients.se_id, this is not mutable current ownership.
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "se_id", nullable = false)
    private SalesExecutive salesExecutive;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_log_id", nullable = true)
    private ActivityLog activityLog;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "renewal_date")
    private LocalDate renewalDate;

    @Column(name = "premium_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumAmount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private PolicyStatus status;

    @Column(name = "has_claimed", nullable = false)
    private boolean hasClaimed;

    public Sale() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public Policy getPolicy() { return policy; }
    public void setPolicy(Policy policy) { this.policy = policy; }
    public SalesExecutive getSalesExecutive() { return salesExecutive; }
    public void setSalesExecutive(SalesExecutive salesExecutive) { this.salesExecutive = salesExecutive; }
    public ActivityLog getActivityLog() { return activityLog; }
    public void setActivityLog(ActivityLog activityLog) { this.activityLog = activityLog; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getRenewalDate() { return renewalDate; }
    public void setRenewalDate(LocalDate renewalDate) { this.renewalDate = renewalDate; }
    public BigDecimal getPremiumAmount() { return premiumAmount; }
    public void setPremiumAmount(BigDecimal premiumAmount) { this.premiumAmount = premiumAmount; }
    public PolicyStatus getStatus() { return status; }
    public void setStatus(PolicyStatus status) { this.status = status; }
    public boolean isHasClaimed() { return hasClaimed; }
    public void setHasClaimed(boolean hasClaimed) { this.hasClaimed = hasClaimed; }
}