package lk.spas.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Timestamp updatedAt;

    // Immutable historical snapshot; unlike clients.se_id, this is not mutable current ownership.
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "se_id", nullable = false)
    private SalesExecutive salesExecutive;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "followup_count", nullable = false)
    private int followupCount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_type_id", nullable = false)
    private ActivityType activityType;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private LeadStatus status;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_policy_id")
    private Policy clientPolicy;

    @Column(name = "next_follow_up_date")
    private LocalDate nextFollowUpDate;

    @Lob
    @Column(name = "remarks")
    private String remarks;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "feedback_token", length = 36, unique = true)
    private String feedbackToken;

    public ActivityLog() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public SalesExecutive getSalesExecutive() { return salesExecutive; }
    public void setSalesExecutive(SalesExecutive salesExecutive) { this.salesExecutive = salesExecutive; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public int getFollowupCount() { return followupCount; }
    public void setFollowupCount(int followupCount) { this.followupCount = followupCount; }
    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }
    public LeadStatus getStatus() { return status; }
    public void setStatus(LeadStatus status) { this.status = status; }
    public LocalDate getActivityDate() { return activityDate; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }
    public Policy getClientPolicy() { return clientPolicy; }
    public void setClientPolicy(Policy clientPolicy) { this.clientPolicy = clientPolicy; }
    public LocalDate getNextFollowUpDate() { return nextFollowUpDate; }
    public void setNextFollowUpDate(LocalDate nextFollowUpDate) { this.nextFollowUpDate = nextFollowUpDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getFeedbackToken() { return feedbackToken; }
    public void setFeedbackToken(String feedbackToken) { this.feedbackToken = feedbackToken; }
}