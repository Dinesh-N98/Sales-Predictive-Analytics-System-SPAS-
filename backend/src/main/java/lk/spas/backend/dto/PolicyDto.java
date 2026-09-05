package lk.spas.backend.dto;

public class PolicyDto {

    private Integer id;
    private Integer policyCategoryId;
    private String policyCategoryName;
    private String policyName;
    private String policyDetails;

    public PolicyDto() {
    }

    public PolicyDto(Integer id, Integer policyCategoryId, String policyCategoryName,
            String policyName, String policyDetails) {
        this.id = id;
        this.policyCategoryId = policyCategoryId;
        this.policyCategoryName = policyCategoryName;
        this.policyName = policyName;
        this.policyDetails = policyDetails;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPolicyCategoryId() {
        return policyCategoryId;
    }

    public void setPolicyCategoryId(Integer policyCategoryId) {
        this.policyCategoryId = policyCategoryId;
    }

    public String getPolicyCategoryName() {
        return policyCategoryName;
    }

    public void setPolicyCategoryName(String policyCategoryName) {
        this.policyCategoryName = policyCategoryName;
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