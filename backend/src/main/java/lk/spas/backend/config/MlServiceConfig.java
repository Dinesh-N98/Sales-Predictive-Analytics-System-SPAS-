package lk.spas.backend.config;

public final class MlServiceConfig {

    public static final String ML_SERVICE_URL =
            System.getenv().getOrDefault("SPAS_ML_SERVICE_URL", "http://127.0.0.1:8000");

    private MlServiceConfig() {
    }
}
