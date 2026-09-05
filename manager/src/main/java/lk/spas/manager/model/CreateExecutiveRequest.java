package lk.spas.manager.model;

public class CreateExecutiveRequest {
    private String fullName;
    private String phoneNumber;
    private String email;
    private int seLevelId;
    private String password;
    private boolean active;

    public CreateExecutiveRequest() {}

    public CreateExecutiveRequest(String fullName, String phoneNumber, String email,
                                   int seLevelId, String password, boolean active) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.seLevelId = seLevelId;
        this.password = password;
        this.active = active;
    }

    // getters + setters for all fields
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getSeLevelId() { return seLevelId; }
    public void setSeLevelId(int seLevelId) { this.seLevelId = seLevelId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
