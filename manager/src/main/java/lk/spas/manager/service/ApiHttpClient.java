package lk.spas.manager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.spas.manager.util.ApiConfig;
import lk.spas.manager.util.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiHttpClient {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public String get(String path, int expectedStatus) throws Exception {
        return send("GET", path, null, false, expectedStatus);
    }

    public String getAuthenticated(String path, int expectedStatus) throws Exception {
        return send("GET", path, null, true, expectedStatus);
    }

    public String postJson(String path, Object body, int expectedStatus) throws Exception {
        return send("POST", path, mapper.writeValueAsString(body), true, expectedStatus);
    }

    public String postJsonWithoutAuthentication(String path, Object body, int expectedStatus) throws Exception {
        return send("POST", path, mapper.writeValueAsString(body), false, expectedStatus);
    }

    public String putJson(String path, Object body, int expectedStatus) throws Exception {
        return send("PUT", path, mapper.writeValueAsString(body), true, expectedStatus);
    }

    public String delete(String path, int expectedStatus) throws Exception {
        return send("DELETE", path, null, true, expectedStatus);
    }

    private String send(String method, String path, String body, boolean authenticated, int expectedStatus)
            throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.getBaseUrl() + path))
                .timeout(REQUEST_TIMEOUT)
                .header("Accept", "application/json");

        if (authenticated) {
            String token = SessionManager.getInstance().getToken();
            if (token == null || token.isBlank()) {
                throw new IllegalStateException("Authentication session is missing");
            }
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        if (body != null) {
            requestBuilder.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body));
        } else {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpResponse<String> response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != expectedStatus) {
            if (response.statusCode() == 401) {
                SessionManager.getInstance().clear();
            }
            throw new RuntimeException(errorMessage(response));
        }
        return response.body();
    }

    private String errorMessage(HttpResponse<String> response) {
        try {
            JsonNode error = mapper.readTree(response.body());
            JsonNode message = error.get("message");
            if (message != null && !message.asText().isBlank()) {
                return message.asText();
            }
        } catch (Exception ignored) {
            // Fall back to a status-only message for non-JSON responses.
        }
        return "Request failed with HTTP status " + response.statusCode();
    }
}
