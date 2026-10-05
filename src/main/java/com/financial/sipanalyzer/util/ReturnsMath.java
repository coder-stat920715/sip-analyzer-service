package com.financial.sipanalyzer.util;

import com.financial.sipanalyzer.model.DrawdownResult;
import com.financial.sipanalyzer.model.NavPoint;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Trailing / rolling returns and drawdown over date-indexed NAV series. */
public final class ReturnsMath {
    /** Max calendar-day gap tolerated when locating the NAV at the start of a window (holidays/weekends). */
    public static final int MAX_GAP_DAYS = 7;

    private ReturnsMath() { }

    public static TreeMap<LocalDate, Double> toSeries(List<NavPoint> points) {
        TreeMap<LocalDate, Double> series = new TreeMap<>();
        for (NavPoint p : points) {
            series.put(p.date(), p.nav()); // duplicate dates: last one wins
        }
        return series;
    }

    public static double cagrPercent(double start, double end, double years) {
        return (Math.pow(end / start, 1.0 / years) - 1.0) * 100.0;
    }

    /** Value on {@code target} or the closest earlier date, if within maxGapDays; otherwise null. */
    public static Double valueOnOrBefore(TreeMap<LocalDate, Double> series, LocalDate target, int maxGapDays) {
        Map.Entry<LocalDate, Double> e = series.floorEntry(target);
        if (e == null || ChronoUnit.DAYS.between(e.getKey(), target) > maxGapDays) {
            return null;
        }
        return e.getValue();
    }

    /** N-year rolling CAGR (%) keyed by window end date. */
    public static TreeMap<LocalDate, Double> rollingReturns(TreeMap<LocalDate, Double> series, int years) {
        TreeMap<LocalDate, Double> out = new TreeMap<>();
        for (Map.Entry<LocalDate, Double> e : series.entrySet()) {
            Double start = valueOnOrBefore(series, e.getKey().minusYears(years), MAX_GAP_DAYS);
            if (start != null) {
                out.put(e.getKey(), cagrPercent(start, e.getValue(), years));
            }
        }
        return out;
    }

    /** Annualised return (%) over the last N years ending at the latest date; null if history too short. */
    public static Double trailingReturnPercent(TreeMap<LocalDate, Double> series, int years) {
        Map.Entry<LocalDate, Double> last = series.lastEntry();
        Double start = valueOnOrBefore(series, last.getKey().minusYears(years), MAX_GAP_DAYS);
        return start == null ? null : cagrPercent(start, last.getValue(), years);
    }

    public static DrawdownResult maxDrawdown(TreeMap<LocalDate, Double> series) {
        double peak = Double.NEGATIVE_INFINITY;
        LocalDate peakDate = null;
        double maxDd = 0.0;
        double worstPeakNav = 0.0;
        LocalDate worstPeakDate = null;
        LocalDate troughDate = null;
        for (Map.Entry<LocalDate, Double> e : series.entrySet()) {
            double nav = e.getValue();
            if (nav > peak) {
                peak = nav;
                peakDate = e.getKey();
            }
            double dd = (nav - peak) / peak;
            if (dd < maxDd) {
                maxDd = dd;
                worstPeakNav = peak;
                worstPeakDate = peakDate;
                troughDate = e.getKey();
            }
        }
        LocalDate recovery = null;
        if (troughDate != null) {
            for (Map.Entry<LocalDate, Double> e : series.tailMap(troughDate, false).entrySet()) {
                if (e.getValue() >= worstPeakNav) {
                    recovery = e.getKey();
                    break;
                }
            }
        }
        return new DrawdownResult(Rounding.round2(maxDd * 100.0), worstPeakDate, troughDate, recovery);
    }
}
