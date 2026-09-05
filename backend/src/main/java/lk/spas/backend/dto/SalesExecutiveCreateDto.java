package lk.spas.backend.dto;

public class SalesExecutiveCreateDto {

    private String fullName;
    private boolean active;
    private Integer seLevelId;
    private String email;
    private String phoneNumber;
    private String password;

    public SalesExecutiveCreateDto() {
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

    public Integer getSeLevelId() {
        return seLevelId;
    }

    public void setSeLevelId(Integer seLevelId) {
        this.seLevelId = seLevelId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
