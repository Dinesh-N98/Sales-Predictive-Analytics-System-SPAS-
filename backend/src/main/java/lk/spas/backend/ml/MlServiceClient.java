package lk.spas.backend.ml;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import lk.spas.backend.config.MlServiceConfig;

public final class MlServiceClient {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final Jsonb JSONB = JsonbBuilder.create();

    private MlServiceClient() {
    }

    public static List<ActivityOutcomeResult> predictActivityOutcome(List<ActivityOutcomeRequestItem> items) {
        ActivityOutcomeBatchRequest request = new ActivityOutcomeBatchRequest(items);
        String payload = JSONB.toJson(request);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MlServiceConfig.ML_SERVICE_URL + "/predict/activity-outcome"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new IllegalStateException(
                        "ML service request failed with status " + response.statusCode() + ": " + response.body());
            }
            ActivityOutcomeBatchResponse body = JSONB.fromJson(response.body(), ActivityOutcomeBatchResponse.class);
            return body == null ? List.of() : body.getResults();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed to call ML activity outcome service", e);
        }
    }

    public static List<SeTargetForecastResult> predictSeTargetForecast(List<SeTargetForecastRequestItem> items) {
        SeTargetForecastBatchRequest request = new SeTargetForecastBatchRequest(items);
        String payload = JSONB.toJson(request);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(MlServiceConfig.ML_SERVICE_URL + "/predict/se-target-forecast"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new IllegalStateException(
                        "ML service request failed with status " + response.statusCode() + ": " + response.body());
            }
            SeTargetForecastBatchResponse body = JSONB.fromJson(response.body(), SeTargetForecastBatchResponse.class);
            return body == null ? List.of() : body.getResults();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed to call ML se target forecast service", e);
        }
    }
}
