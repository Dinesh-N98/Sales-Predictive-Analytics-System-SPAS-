package lk.spas.manager.service;

import lk.spas.manager.model.LoginRequest;
import lk.spas.manager.model.LoginResponse;
import lk.spas.manager.util.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuthService {
    private final ApiHttpClient apiClient = new ApiHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public LoginResponse login(String phoneNumber, String password) throws Exception {
        SessionManager.getInstance().clear();
        LoginRequest req = new LoginRequest(phoneNumber, password);
        String response = apiClient.postJsonWithoutAuthentication("/auth/login", req, 200);
        return mapper.readValue(response, LoginResponse.class);
    }
}