/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lk.spas.manager.model;

/**
 *
 * @author Narada
 */
public class LoginResponse {
    private String token;
    private String fullName;
    private String role;

    // getters/setters — Jackson needs these to deserialize
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
