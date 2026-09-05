/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lk.spas.manager.model;

/**
 *
 * @author Narada
 */
public class LoginRequest {
    private String phoneNumber;
    private String password;

    public LoginRequest(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPassword() { return password; }
}
