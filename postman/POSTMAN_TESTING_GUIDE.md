# SIP Analyzer – Postman Testing Guide

Files: `SIP-Analyzer.postman_collection.json` (31 requests, ~150 assertions) and `SIP-Analyzer.postman_environment.json`.

## A. One-time setup
1. **Start the service**: unzip the project, then `mvn spring-boot:run`. Wait for `Started SipAnalyzerApplication` (port 8080).
2. **Open Postman → Import** → select both JSON files.
3. Top-right environment dropdown → choose **SIP Analyzer - Local** (`baseUrl = http://localhost:8080`).
4. Run **0.1 OpenAPI docs reachable**. Green = service is up. If it fails, the service isn't running or the port differs (edit `baseUrl`).

## B. Run everything at once
1. Right-click the collection → **Run collection** (Runner).
2. Keep the default order, leave "Run manually", click **Run SIP Analyzer Service**.
3. Expect **0 failed**. Any red test shows the expected vs actual value.
4. CLI alternative: `newman run SIP-Analyzer.postman_collection.json -e SIP-Analyzer.postman_environment.json`

## C. Run one request manually (how to read results)
1. Click the request → read its **Description** (what is checked, expected numbers).
2. For folder 3 only: open **Scripts → Pre-request** to see how NAV data is generated (nothing to paste).
3. **Send** → check **Body** (JSON) and **Test Results** tab (green/red).
4. Tweak the body (e.g. change `annualReturnPercent`) and resend to explore.

## D. What each folder verifies

| # | Request | Key expectation |
|---|---|---|
| 0.1 | OpenAPI docs | 200, 6 endpoints listed |
| 1.1 | 10k, 12%, 10y | 10 rows; 10y corpus ≈ 2,323,391; invested 1,200,000; milestones 5/10/15/20 |
| 1.2 | Custom milestones | sorted, de-duplicated 7/12/25 |
| 1.3 | 0% return | corpus = 120,000, gain 0 |
| 1.4 | years = 0 | 400, `errors.years` |
| 2.1 | Step-up 10%, 20y | step-up corpus > standard; SIP 10,000→11,000→12,100; delta = difference |
| 2.2 | Step-up 0% | equals standard SIP |
| 2.3 | Missing step-up | 400, `errors.stepUpPercent` |
| 3.1 | Rolling 3y/5y + benchmark | 37 / 13 observations; avg ≈ 12.68%; 100% outperformance; MDD 0 |
| 3.2 | 40% crash | MDD −40%, peak 2021‑01‑01, trough 2021‑02‑01, recovery null |
| 3.3 | 10y window, short data | 0 observations, null stats |
| 3.4 | One NAV point | 400, `errors.fundNavs` |
| 4.1 | 14.5% vs 14.0% | net 13.8 vs 13.8, TIE, breakeven alpha 0.5 |
| 4.2 | Equal gross | PASSIVE wins, gap widens with time |
| 4.3 | 16% vs 14% | net alpha 1.5, ACTIVE wins |
| 4.4 | Custom TER/horizons | horizons sorted 8,12; breakeven 0.9 |
| 5.1 | Fund = 2×benchmark | beta 2.0, Rf 6.5, vol 2×, Sharpe consistent |
| 5.2 | Fund = benchmark, Rf 7 | beta 1, alpha 0, excess 0 |
| 5.3–5.5 | Length mismatch / zero variance / ≤ −100% | 400 *Invalid input* |
| 6.1 | 15y goal, 3y glide, 30% crash | equity 100%→0%; glide wins after crash, full equity wins without; crash benefit > 0 |
| 6.2 | glideYears 0 | identical to full equity |
| 6.3 | Defaults | glide 3y, crash 30%, 12 months |
| 6.4 | SIP stops in glide | glide invested 1,440,000 |
| 6.5–6.6 | Invalid glide / crash timing | 400 |
| 7.1–7.4 | Malformed JSON, wrong type, bad date, negative NAV | 400 |

## E. Troubleshooting
- **Could not get response** – service down / wrong `baseUrl`.
- **3.x fail with "Unexpected token"** – the pre-request script didn't run; run via the request itself (not a copy without scripts).
- **Value mismatch by a few paise** – tolerances are built in; larger gaps mean the formula changed.
- **Response time test fails on first run** – JVM warm-up; rerun.
