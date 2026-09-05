package lk.spas.manager.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public final class CurrencyFormatter {
    private CurrencyFormatter() {
    }

    public static String format(BigDecimal amount) {
        if (amount == null) {
            return "-";
        }
        DecimalFormat pattern = new DecimalFormat("#,##0.00");
        return "Rs. " + pattern.format(amount);
    }
}
