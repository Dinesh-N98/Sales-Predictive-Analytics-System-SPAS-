package lk.spas.backend.dto;

public class SalesExecutiveDto {

    private Integer id;
    private String fullName;
    private boolean active;
    private String email;
    private String phoneNumber;
    private Integer seLevelId;
    private String seLevelName;

    public SalesExecutiveDto() {
    }

    public SalesExecutiveDto(Integer id, String fullName, boolean active, String email, String phoneNumber,
            Integer seLevelId, String seLevelName) {
        this.id = id;
        this.fullName = fullName;
        this.active = active;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.seLevelId = seLevelId;
        this.seLevelName = seLevelName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getSeLevelId() {
        return seLevelId;
    }

    public void setSeLevelId(Integer seLevelId) {
        this.seLevelId = seLevelId;
    }

    public String getSeLevelName() {
        return seLevelName;
    }

    public void setSeLevelName(String seLevelName) {
        this.seLevelName = seLevelName;
    }
}
