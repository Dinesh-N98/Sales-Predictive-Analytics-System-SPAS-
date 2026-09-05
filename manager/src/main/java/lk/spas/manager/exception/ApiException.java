package lk.spas.manager.exception;

public class ApiException extends Exception {
    private final int statusCode;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isServiceUnavailable() {
        return statusCode == 503;
    }

    public boolean isBadRequest() {
        return statusCode == 400;
    }

    public boolean isNotFound() {
        return statusCode == 404;
    }

    public boolean isAuthError() {
        return statusCode == 401 || statusCode == 403;
    }
}
