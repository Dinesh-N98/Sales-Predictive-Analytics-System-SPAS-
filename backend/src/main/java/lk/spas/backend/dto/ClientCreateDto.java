package lk.spas.backend.dto;

public class ClientCreateDto {

    private Integer seId;
    private String fullName;
    private String address;
    private String contactNumber;
    private Integer clientTypeId;
    private Integer financialLevelId;
    private Integer rejectionReasonId;
    private Integer leadSourceId;

    public ClientCreateDto() {
    }

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
    public Integer getFinancialLevelId() { return financialLevelId; }
    public void setFinancialLevelId(Integer financialLevelId) { this.financialLevelId = financialLevelId; }
    public Integer getRejectionReasonId() { return rejectionReasonId; }
    public void setRejectionReasonId(Integer rejectionReasonId) { this.rejectionReasonId = rejectionReasonId; }
    public Integer getLeadSourceId() { return leadSourceId; }
    public void setLeadSourceId(Integer leadSourceId) { this.leadSourceId = leadSourceId; }
}