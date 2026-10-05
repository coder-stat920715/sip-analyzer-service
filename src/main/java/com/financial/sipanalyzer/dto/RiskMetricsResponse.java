package com.financial.sipanalyzer.dto;

/** Returns/volatility in percent (annualised); beta and Sharpe are plain ratios. */
public record RiskMetricsResponse(int observations, int periodsPerYear, double riskFreeRatePercent,
                                  double annualizedFundReturnPercent, double annualizedBenchmarkReturnPercent,
                                  double excessReturnPercent, double jensensAlphaPercent, double beta,
                                  double annualizedVolatilityPercent, double benchmarkVolatilityPercent,
                                  double sharpeRatio) {
}
