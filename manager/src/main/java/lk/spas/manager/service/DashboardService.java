package lk.spas.manager.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lk.spas.manager.exception.ApiException;
import lk.spas.manager.model.AchievementVsTargetDto;
import lk.spas.manager.model.AtRiskActivityDto;
import lk.spas.manager.model.ActivityOutcomeDetailDto;
import lk.spas.manager.model.HotLeadDto;
import lk.spas.manager.model.SalesTrendDto;
import lk.spas.manager.model.SePaceForecastDto;
import lk.spas.manager.model.SePerformanceDto;
import lk.spas.manager.model.SeTargetForecastDetailDto;
import lk.spas.manager.util.ApiConfig;
import lk.spas.manager.util.SessionManager;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DashboardService {
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);
    private static final String BASE_URL = ApiConfig.getBaseUrl();

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(REQUEST_TIMEOUT)
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public DashboardService() {
        mapper.registerModule(new JavaTimeModule());
    }

    public List<HotLeadDto> getHotLeads(Integer limit) throws Exception {
        return getList("/dashboard/hot-leads", limit, new TypeReference<List<HotLeadDto>>() {});
    }

    public List<AtRiskActivityDto> getAtRiskActivities(Integer limit) throws Exception {
        return getList("/dashboard/at-risk-activities", limit, new TypeReference<List<AtRiskActivityDto>>() {});
    }

    public ActivityOutcomeDetailDto getActivityOutcomeDetail(Integer activityLogId) throws Exception {
        return executeObject("/decision-aid/activity-outcome/" + activityLogId, List.of(), ActivityOutcomeDetailDto.class);
    }

    public SeTargetForecastDetailDto getSeTargetForecastDetail(Integer seId, String month) throws Exception {
        List<String> params = month == null || month.isBlank() ? List.of() : List.of("month=" + encode(month));
        return executeObject("/decision-aid/se-target-forecast/" + seId, params, SeTargetForecastDetailDto.class);
    }

    public List<SePaceForecastDto> getSePaceForecast(String month) throws Exception {
        return getList("/dashboard/se-pace-forecast", month, new TypeReference<List<SePaceForecastDto>>() {});
    }

    public List<SePerformanceDto> getSePerformance(String month) throws Exception {
        return getList("/dashboard/se-performance", month, new TypeReference<List<SePerformanceDto>>() {});
    }

    public List<SalesTrendDto> getSalesTrends(Integer seId, Integer seLevelId, String startDate, String endDate)
            throws Exception {
        return getList(
                "/dashboard/sales-trends",
                seId,
                seLevelId,
                startDate,
                endDate,
                new TypeReference<List<SalesTrendDto>>() {}
        );
    }

    public List<AchievementVsTargetDto> getAchievementVsTarget(Integer seId, String startMonth, String endMonth)
            throws Exception {
        return getList(
                "/dashboard/achievement-vs-target",
                seId,
                startMonth,
                endMonth,
                new TypeReference<List<AchievementVsTargetDto>>() {}
        );
    }

    private <T> List<T> getList(String path, Integer limit, TypeReference<List<T>> typeReference) throws Exception {
        List<String> params = new ArrayList<>();
        if (limit != null) {
            params.add("limit=" + limit);
        }
        return execute(path, params, typeReference);
    }

    private <T> List<T> getList(String path, String month, TypeReference<List<T>> typeReference) throws Exception {
        List<String> params = new ArrayList<>();
        if (month != null && !month.isBlank()) {
            params.add("month=" + encode(month));
        }
        return execute(path, params, typeReference);
    }

    private <T> List<T> getList(String path, Integer seId, Integer seLevelId, String startDate, String endDate,
            TypeReference<List<T>> typeReference) throws Exception {
        List<String> params = new ArrayList<>();
        if (seId != null) {
            params.add("seId=" + seId);
        }
        if (seLevelId != null) {
            params.add("seLevelId=" + seLevelId);
        }
        if (startDate != null && !startDate.isBlank()) {
            params.add("startDate=" + encode(startDate));
        }
        if (endDate != null && !endDate.isBlank()) {
            params.add("endDate=" + encode(endDate));
        }
        return execute(path, params, typeReference);
    }

    private <T> List<T> getList(String path, Integer seId, String startMonth, String endMonth,
            TypeReference<List<T>> typeReference) throws Exception {
        List<String> params = new ArrayList<>();
        if (seId != null) {
            params.add("seId=" + seId);
        }
        if (startMonth != null && !startMonth.isBlank()) {
            params.add("startMonth=" + encode(startMonth));
        }
        if (endMonth != null && !endMonth.isBlank()) {
            params.add("endMonth=" + encode(endMonth));
        }
        return execute(path, params, typeReference);
    }

    private <T> List<T> execute(String path, List<String> params, TypeReference<List<T>> typeReference)
            throws Exception {
        String url = BASE_URL + path;
        if (!params.isEmpty()) {
            url += "?" + String.join("&", params);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(REQUEST_TIMEOUT)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + SessionManager.getInstance().getToken())
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if (status < 200 || status >= 300) {
                throw new ApiException(status, extractMessage(response));
            }
            if (response.body() == null || response.body().isBlank()) {
                return new ArrayList<>();
            }
            return mapper.readValue(response.body(), typeReference);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(503, "Could not reach the backend: " + e.getMessage());
        }
    }

    private <T> T executeObject(String path, List<String> params, Class<T> type) throws Exception {
        String url = BASE_URL + path;
        if (!params.isEmpty()) {
            url += "?" + String.join("&", params);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)).timeout(REQUEST_TIMEOUT).header("Accept", "application/json")
                .header("Authorization", "Bearer " + SessionManager.getInstance().getToken()).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ApiException(response.statusCode(), extractMessage(response));
            }
            return mapper.readValue(response.body(), type);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException(503, "Could not reach the backend: " + e.getMessage());
        }
    }

    private String extractMessage(HttpResponse<String> response) {
        String rawBody = response.body() == null ? "" : response.body();
        try {
            var node = mapper.readTree(rawBody);
            if (node != null && node.has("message") && !node.get("message").asText().isBlank()) {
                return node.get("message").asText();
            }
        } catch (Exception ignored) {
            // fall back to raw body
        }
        return rawBody.isBlank() ? "Request failed with HTTP status " + response.statusCode() : rawBody;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
