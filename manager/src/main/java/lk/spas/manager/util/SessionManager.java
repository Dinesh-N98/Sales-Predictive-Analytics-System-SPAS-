/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lk.spas.manager.util;

/**
 *
 * @author Narada
 */
public class SessionManager {
    private static final SessionManager instance = new SessionManager();
    private String token;
    private String managerName;

    private SessionManager() {}
    public static SessionManager getInstance() { return instance; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String n) { this.managerName = n; }

    public void clear() {
        token = null;
        managerName = null;
    }

    public boolean hasSession() {
        return token != null && !token.isBlank();
    }
}
