package com.financial.sipanalyzer.util;

import java.util.List;

/** Risk statistics on periodic returns expressed as decimals (0.015 = 1.5%). */
public final class RiskMath {
    private RiskMath() { }

    public static double[] toDecimals(List<Double> percentReturns) {
        double[] out = new double[percentReturns.size()];
        for (int i = 0; i < out.length; i++) {
            double d = percentReturns.get(i) / 100.0;
            if (d <= -1.0) {
                throw new IllegalArgumentException("Periodic returns must be greater than -100%");
            }
            out[i] = d;
        }
        return out;
    }

    public static double mean(double[] x) {
        double s = 0;
        for (double v : x) {
            s += v;
        }
        return s / x.length;
    }

    /** Sample covariance (n-1). */
    public static double covariance(double[] a, double[] b) {
        if (a.length != b.length || a.length < 2) {
            throw new IllegalArgumentException("Series must have equal length of at least 2");
        }
        double ma = mean(a);
        double mb = mean(b);
        double s = 0;
        for (int i = 0; i < a.length; i++) {
            s += (a[i] - ma) * (b[i] - mb);
        }
        return s / (a.length - 1);
    }

    public static double variance(double[] x) {
        return covariance(x, x);
    }

    /** Annualised volatility: sample std-dev of periodic returns * sqrt(periodsPerYear). */
    public static double annualizedVolatility(double[] periodic, int periodsPerYear) {
        return Math.sqrt(variance(periodic)) * Math.sqrt(periodsPerYear);
    }

    /** Geometric annualised return (CAGR) from periodic returns. */
    public static double annualizedReturn(double[] periodic, int periodsPerYear) {
        double sumLog = 0;
        for (double r : periodic) {
            sumLog += Math.log1p(r);
        }
        return Math.expm1(sumLog * periodsPerYear / periodic.length);
    }

    /** Beta = Cov(fund, benchmark) / Var(benchmark). */
    public static double beta(double[] fund, double[] benchmark) {
        double varB = variance(benchmark);
        if (varB == 0.0) {
            throw new IllegalArgumentException("Benchmark has zero variance; beta is undefined");
        }
        return covariance(fund, benchmark) / varB;
    }

    /** Sharpe = (Rp - Rf) / sigma_p, all annualised decimals. */
    public static double sharpe(double portfolioReturn, double riskFree, double volatility) {
        if (volatility == 0.0) {
            throw new IllegalArgumentException("Fund has zero volatility; Sharpe ratio is undefined");
        }
        return (portfolioReturn - riskFree) / volatility;
    }

    /** Jensen's alpha = Rp - [Rf + beta * (Rb - Rf)]. */
    public static double jensensAlpha(double rp, double rf, double beta, double rb) {
        return rp - (rf + beta * (rb - rf));
    }
}
