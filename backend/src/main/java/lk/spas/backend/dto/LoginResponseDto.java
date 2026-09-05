package lk.spas.backend.dto;

public class LoginResponseDto {

    private String token;
    private String role;
    private String fullName;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, String role, String fullName) {
        this.token = token;
        this.role = role;
        this.fullName = fullName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
