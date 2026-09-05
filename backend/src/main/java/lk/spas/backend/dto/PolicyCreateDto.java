package lk.spas.backend.dto;

public class PolicyCreateDto {

    private Integer policyCategoryId;
    private String policyName;
    private String policyDetails;

    public PolicyCreateDto() {
    }

    public Integer getPolicyCategoryId() {
        return policyCategoryId;
    }

    public void setPolicyCategoryId(Integer policyCategoryId) {
        this.policyCategoryId = policyCategoryId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getPolicyDetails() {
        return policyDetails;
    }

    public void setPolicyDetails(String policyDetails) {
        this.policyDetails = policyDetails;
    }
}