package lk.spas.manager.model;

public class SalesExecutive {
    private int id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private int seLevelId;
    private String seLevelName;
    private boolean active;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getSeLevelId() { return seLevelId; }
    public void setSeLevelId(int seLevelId) { this.seLevelId = seLevelId; }

    public String getSeLevelName() { return seLevelName; }
    public void setSeLevelName(String seLevelName) { this.seLevelName = seLevelName; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}