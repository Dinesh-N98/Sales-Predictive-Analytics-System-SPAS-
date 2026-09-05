package lk.spas.manager.util;

public final class ApiConfig {

    // Local development fallback; deployments must override this with a property or environment variable.
    private static final String DEVELOPMENT_DEFAULT_BASE_URL = "http://localhost:24742/backend/api";
    private static final String ENVIRONMENT_VARIABLE = "SPAS_API_BASE_URL";
    private static final String SYSTEM_PROPERTY = "spas.api.base-url";

    private ApiConfig() {
    }

    public static String getBaseUrl() {
        String configuredUrl = System.getProperty(SYSTEM_PROPERTY);
        if (configuredUrl == null || configuredUrl.isBlank()) {
            configuredUrl = System.getenv(ENVIRONMENT_VARIABLE);
        }
        if (configuredUrl == null || configuredUrl.isBlank()) {
            return DEVELOPMENT_DEFAULT_BASE_URL;
        }
        return configuredUrl.replaceAll("/+$", "");
    }
}
