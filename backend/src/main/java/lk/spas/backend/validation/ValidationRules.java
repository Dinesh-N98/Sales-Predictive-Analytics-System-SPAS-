package lk.spas.backend.validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

public final class ValidationRules {

    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+()\\-\\s]{7,20}$");

    private ValidationRules() {
    }

    public static String required(String value, String field) {
        return value == null || value.isBlank() ? field + " is required" : null;
    }

    public static String required(Integer value, String field) {
        return value == null ? field + " is required" : null;
    }

    public static String maxLength(String value, String field, int max) {
        return value != null && value.length() > max ? field + " must not exceed " + max + " characters" : null;
    }

    public static String email(String value, String field) {
        return value != null && !EMAIL.matcher(value).matches() ? field + " must be a valid email address" : null;
    }

    public static String phone(String value, String field) {
        return value != null && !PHONE.matcher(value).matches() ? field + " must contain 7 to 20 valid phone characters" : null;
    }

    public static String amount(BigDecimal value, String field) {
        if (value == null) return null;
        if (value.signum() < 0) return field + " must not be negative";
        if (value.scale() > 2 || value.precision() - value.scale() > 10) {
            return field + " must contain at most 10 integer digits and 2 decimal places";
        }
        return null;
    }

    public static String dateOrder(LocalDate earlier, LocalDate later, String laterField, String earlierField) {
        return earlier != null && later != null && later.isBefore(earlier)
                ? laterField + " must not be before " + earlierField : null;
    }

    public static boolean allowedLeadStatusTransition(int currentStatusId, int newStatusId) {
        return currentStatusId == newStatusId
                || (currentStatusId == 1 && (newStatusId == 2 || newStatusId == 3));
    }

    public static boolean allowedPolicyStatusTransition(int currentStatusId, int newStatusId) {
        if (currentStatusId == newStatusId) return true;
        switch (currentStatusId) {
            case 1:
                return newStatusId == 2 || newStatusId == 3 || newStatusId == 4;
            case 2:
                return newStatusId == 4;
            case 5:
                return newStatusId == 1 || newStatusId == 3;
            default:
                return false;
        }
    }
}
