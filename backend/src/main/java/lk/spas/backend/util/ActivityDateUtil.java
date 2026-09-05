package lk.spas.backend.util;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public final class ActivityDateUtil {

    private ActivityDateUtil() {
    }

    public static String formatDayOfWeek(LocalDate activityDate) {
        if (activityDate == null) {
            return "Unknown";
        }
        String fullName = activityDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return Character.toUpperCase(fullName.charAt(0)) + fullName.substring(1).toLowerCase();
    }
}