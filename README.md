# sip-analyzer-service

Spring Boot 3.3 / Java 17 quantitative SIP & mutual fund analysis engine.

    mvn clean test
    mvn spring-boot:run          # Swagger UI: http://localhost:8080/swagger-ui.html

| Endpoint | Purpose |
|---|---|
| POST /api/v1/sip/projection | SIP corpus, yearly schedule, 5/10/15/20y milestones |
| POST /api/v1/sip/step-up | Standard vs annual step-up SIP |
| POST /api/v1/returns/rolling | Rolling & trailing returns, benchmark outperformance %, max drawdown |
| POST /api/v1/benchmark/active-vs-passive | Net-of-TER alpha and corpus (10-20y) |
| POST /api/v1/risk/metrics | Alpha, beta, Sharpe, annualised volatility |
| POST /api/v1/goals/glide-path | Equity-to-debt STP glide path with crash stress test |

Example:

    curl -X POST localhost:8080/api/v1/sip/step-up -H 'Content-Type: application/json' \
      -d '{"monthlyInvestment":10000,"annualReturnPercent":12,"years":20,"stepUpPercent":10}'

Defaults (risk-free 6.5%, TER 0.70/0.20, debt 6.5%) live in `application.yml` under `analyzer.*`.
