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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.sql.Timestamp;

@Entity
@Table(name = "client_feedbacks", uniqueConstraints = @UniqueConstraint(columnNames = "activity_log_id"))
public class ClientFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_log_id", nullable = false, unique = true)
    private ActivityLog activityLog;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Immutable historical snapshot; unlike clients.se_id, this is not mutable current ownership.
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "se_id", nullable = false)
    private SalesExecutive salesExecutive;

    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "strength_id")
    private FeedbackStrength strength;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "improvement_id")
    private FeedbackImprovement improvement;

    @Lob
    @Column(name = "comments")
    private String comments;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Timestamp createdAt;

    public ClientFeedback() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public ActivityLog getActivityLog() { return activityLog; }
    public void setActivityLog(ActivityLog activityLog) { this.activityLog = activityLog; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public SalesExecutive getSalesExecutive() { return salesExecutive; }
    public void setSalesExecutive(SalesExecutive salesExecutive) { this.salesExecutive = salesExecutive; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public FeedbackStrength getStrength() { return strength; }
    public void setStrength(FeedbackStrength strength) { this.strength = strength; }
    public FeedbackImprovement getImprovement() { return improvement; }
    public void setImprovement(FeedbackImprovement improvement) { this.improvement = improvement; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}