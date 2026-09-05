package lk.spas.backend.dto;

import java.sql.Timestamp;

public class ClientDto {

    private Integer id;
    private Integer seId;
    private String fullName;
    private String address;
    private String contactNumber;
    private Integer clientTypeId;
    private String clientTypeName;
    private Integer financialLevelId;
    private String financialLevelName;
    private Integer rejectionReasonId;
    private String rejectionReasonName;
    private Integer leadSourceId;
    private String leadSourceName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public ClientDto() {
    }

        public ClientDto(Integer id, Integer seId, String fullName, String address, String contactNumber,
            Integer clientTypeId, String clientTypeName, Integer financialLevelId,
            String financialLevelName, Integer rejectionReasonId, String rejectionReasonName, Integer leadSourceId,
            String leadSourceName, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.seId = seId;
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.clientTypeId = clientTypeId;
        this.clientTypeName = clientTypeName;
        this.financialLevelId = financialLevelId;
        this.financialLevelName = financialLevelName;
        this.rejectionReasonId = rejectionReasonId;
        this.rejectionReasonName = rejectionReasonName;
        this.leadSourceId = leadSourceId;
        this.leadSourceName = leadSourceName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getSeId() { return seId; }
    public void setSeId(Integer seId) { this.seId = seId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public Integer getClientTypeId() { return clientTypeId; }
    public void setClientTypeId(Integer clientTypeId) { this.clientTypeId = clientTypeId; }
    public String getClientTypeName() { return clientTypeName; }
    public void setClientTypeName(String clientTypeName) { this.clientTypeName = clientTypeName; }
    public Integer getFinancialLevelId() { return financialLevelId; }
    public void setFinancialLevelId(Integer financialLevelId) { this.financialLevelId = financialLevelId; }
    public String getFinancialLevelName() { return financialLevelName; }
    public void setFinancialLevelName(String financialLevelName) { this.financialLevelName = financialLevelName; }
    public Integer getRejectionReasonId() { return rejectionReasonId; }
    public void setRejectionReasonId(Integer rejectionReasonId) { this.rejectionReasonId = rejectionReasonId; }
    public String getRejectionReasonName() { return rejectionReasonName; }
    public void setRejectionReasonName(String rejectionReasonName) { this.rejectionReasonName = rejectionReasonName; }
    public Integer getLeadSourceId() { return leadSourceId; }
    public void setLeadSourceId(Integer leadSourceId) { this.leadSourceId = leadSourceId; }
    public String getLeadSourceName() { return leadSourceName; }
    public void setLeadSourceName(String leadSourceName) { this.leadSourceName = leadSourceName; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}