package com.financial.sipanalyzer.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Rounding {
    private Rounding() { }

    public static double round2(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double round4(double v) {
        return BigDecimal.valueOf(v).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }
}
