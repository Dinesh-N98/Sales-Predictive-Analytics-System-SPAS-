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
import java.sql.Timestamp;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // Mutable current ownership; unlike activity_logs.se_id and sales.se_id, this is not a historical snapshot.
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "se_id", nullable = false)
    private SalesExecutive salesExecutive;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Timestamp updatedAt;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_type_id", nullable = false)
    private ClientType clientType;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_level_id", nullable = false)
    private FinancialLevel financialLevel;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "rejection_reason_id")
    private RejectionReason rejectionReason;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_source_id", nullable = false)
    private LeadSource leadSource;

    public Client() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SalesExecutive getSalesExecutive() {
        return salesExecutive;
    }

    public void setSalesExecutive(SalesExecutive salesExecutive) {
        this.salesExecutive = salesExecutive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public FinancialLevel getFinancialLevel() {
        return financialLevel;
    }

    public void setFinancialLevel(FinancialLevel financialLevel) {
        this.financialLevel = financialLevel;
    }

    public RejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(RejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LeadSource getLeadSource() {
        return leadSource;
    }

    public void setLeadSource(LeadSource leadSource) {
        this.leadSource = leadSource;
    }
}