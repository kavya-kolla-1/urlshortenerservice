# Agentic URL Shortener

A production-oriented URL Shortener service built with Spring Boot, demonstrating an
**Agentic SDLC workflow** where AI agents coordinate requirement analysis, design,
implementation, testing, validation, and release under human oversight and approval gates.

> **Assignment context:** This project is a response to the Schwab Staff Engineer I
> take-home exercise. It demonstrates greenfield development, brownfield enhancement,
> and ambiguous requirement handling — all orchestrated through an agentic execution model.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Setup and Run](#setup-and-run)
- [Running the Agent Workflow](#running-the-agent-workflow)
- [API Reference](#api-reference)
- [Testing](#testing)
- [Test Coverage Report](#test-coverage-report)
- [Agentic Design](#agentic-design)
- [Key Design Decisions](#key-design-decisions)
- [Limitations and Trade-offs](#limitations-and-trade-offs)
- [Future Improvements](#future-improvements)

---

## Architecture Overview

```
Client (HTTP)
      │
      ▼
UrlController / AnalyticsController
      │
      ▼
UrlService / AnalyticsService
      │
      ├──────────────────────┐
      ▼                      ▼
Redis Cache             H2 Database
(redirect fast path)    (UrlMapping, ClickEvent)
      │
      ▼
AgentOrchestrator (Workflow Engine)
      │
      ├── RequirementAgent
      ├── TaskPlannerAgent
      ├── DesignAgent
      ├── CodingAgent
      ├── TestAgent
      ├── ValidationAgent
      ├── DocumentationAgent
      ├── ReleaseReadinessAgent  ◄── Human Approval Gate
      └── ReleaseAgent
```

See `docs/Architecture.md` and `docs/Architecture.drawio` for full component diagrams.

---

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Persistence | Spring Data JPA + H2 (in-memory) |
| Caching | Redis (optional — see setup note below) |
| API Docs | Springdoc OpenAPI / Swagger UI |
| Build | Maven 3.8+ |
| Testing | JUnit 5, Mockito, MockMvc |
| Coverage | JaCoCo |
| Containerization | Docker + Docker Compose |

---

## Prerequisites

| Tool | Version | Required |
|------|---------|----------|
| Java JDK | 17+ | ✅ Required |
| Maven | 3.8+ | ✅ Required |
| Docker | 20+ | ⚠️ Required only for Redis |
| Redis | 7+ | ⚠️ Optional (app runs without it — see note) |

> **Redis Note:** The application is configured to fall back gracefully if Redis is
> unavailable. To run without Redis, set `spring.cache.type=none` in
> `application.properties`. To run with Redis, start it via Docker (see below).

---

## Setup and Run

### Option 1 — Run without Redis (quickest)

```bash
# 1. Clone the repository
git clone https://github.com/kavya-kolla-1/urlshortener.git
cd urlshortener/urlshortener

# 2. Disable Redis caching (run without cache)
# Edit src/main/resources/application.properties:
# spring.cache.type=none

# 3. Build
mvn clean install -DskipTests

# 4. Run
mvn spring-boot:run
```

Application starts at: `http://localhost:8080`

---

### Option 2 — Run with Redis (full stack)

```bash
# 1. Start Redis using Docker
docker run -d --name redis -p 6379:6379 redis:7

# 2. Clone and build
git clone https://github.com/kavya-kolla-1/urlshortener.git
cd urlshortener/urlshortener
mvn clean install -DskipTests

# 3. Run
mvn spring-boot:run
```

---

### Option 3 — Run with Docker Compose (recommended)

```bash
git clone https://github.com/kavya-kolla-1/urlshortener.git
cd urlshortener/urlshortener
docker-compose up --build
```

Application starts at: `http://localhost:8080`

---

## Running the Agent Workflow

The agentic orchestration layer is triggered via a dedicated API endpoint. It executes
the full SDLC pipeline: requirement → design → implementation → test → validation →
human approval → release.

### Trigger the full agentic workflow

```bash
curl -X POST http://localhost:8080/api/v1/agent/run \
  -H "Content-Type: application/json" \
  -d '{
    "requirement": "Add rate limiting to the URL shortener API",
    "scenario": "GREENFIELD"
  }'
```

### Check workflow status

```bash
curl http://localhost:8080/api/v1/agent/status/{workflowId}
```

### Approve a pending human checkpoint

```bash
curl -X POST http://localhost:8080/api/v1/agent/approve/{workflowId} \
  -H "Content-Type: application/json" \
  -d '{
    "approver": "tech-lead",
    "comment": "All gates passed. Approved for release."
  }'
```

### View agent audit trail

```bash
curl http://localhost:8080/api/v1/agent/audit/{workflowId}
```

---

## API Reference

### URL Shortener APIs

#### Shorten a URL
```bash
curl -X POST http://localhost:8080/api/v1/url/shorten \
  -H "Content-Type: application/json" \
  -d '{"originalUrl": "https://www.example.com/very/long/path"}'
```

**Response:**
```json
{
  "shortCode": "aB3xY9",
  "shortUrl": "http://localhost:8080/aB3xY9",
  "originalUrl": "https://www.example.com/very/long/path",
  "createdAt": "2026-07-19T10:00:00Z",
  "expiresAt": "2027-07-19T10:00:00Z"
}
```

#### Redirect to original URL
```bash
curl -L http://localhost:8080/api/v1/url/aB3xY9
# Returns HTTP 302 redirect to original URL
```

#### Get URL metadata
```bash
curl http://localhost:8080/api/v1/url/aB3xY9
```

#### Get click analytics
```bash
curl http://localhost:8080/api/v1/url/analytics/aB3xY9
```

**Response:**
```json
{
  "shortCode": "aB3xY9",
  "totalClicks": 42,
  "clickEvents": [
    {
      "clickedAt": "2026-07-19T10:05:00Z",
      "ipAddress": "192.168.1.1",
      "userAgent": "Mozilla/5.0",
      "referrer": "https://google.com"
    }
  ]
}
```

#### Delete a short URL
```bash
curl -X DELETE http://localhost:8080/api/v1/url/aB3xY9
```

### UI Endpoints

| Endpoint | Purpose |
|----------|---------|
| `http://localhost:8080/swagger-ui.html` | Interactive API documentation |
| `http://localhost:8080/h2-console` | H2 database console (JDBC URL: `jdbc:h2:mem:urlshortener`) |
| `http://localhost:8080/actuator/health` | Application health check |
| `http://localhost:8080/actuator/metrics` | Runtime metrics |

---

## Testing

### Run all tests

```bash
mvn test
```

### Run unit tests only

```bash
mvn test -Dtest="*ServiceTest,*AgentTest"
```

### Run integration tests only

```bash
mvn test -Dtest="*IntegrationTest,*ControllerTest"
```

### Run tests with coverage report

```bash
mvn clean test jacoco:report
```

Coverage report is generated at:
```
target/site/jacoco/index.html
```

Open it in a browser:
```bash
# macOS
open target/site/jacoco/index.html

# Linux
xdg-open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

### Test structure

```
src/test/java/com/schwab/urlshortener/
  ├── service/
  │   ├── UrlServiceImplTest.java       ← Unit tests for URL shortening logic
  │   └── AnalyticsServiceImplTest.java ← Unit tests for analytics
  ├── controller/
  │   └── UrlControllerTest.java        ← MockMvc integration tests
  ├── agent/
  │   └── AgentOrchestratorTest.java    ← Agent workflow tests
  └── workflow/
      └── WorkflowEngineTest.java       ← Workflow state machine tests
```

---

## Test Coverage Report

JaCoCo coverage report is committed under `docs/coverage/` for evaluator reference.

To regenerate:
```bash
mvn clean test jacoco:report
# Report: target/site/jacoco/index.html
```

Target coverage: **≥ 80% line coverage** across service and agent layers.

---

## Agentic Design

The orchestration layer models the full SDLC as a dependency graph of agent-owned tasks.

### Agent Responsibilities

| Agent | Responsibility |
|-------|---------------|
| `RequirementAgent` | Parse and normalize requirements; detect ambiguity; block if unclear |
| `TaskPlannerAgent` | Build dependency graph; assign agents; identify parallel paths |
| `DesignAgent` | Generate API contracts, DB schema, architecture documentation |
| `CodingAgent` | Implement service, controller, repository, and caching layers |
| `ImpactAnalysisAgent` | Brownfield: identify affected components and migration risk |
| `TestAgent` | Generate and execute unit + integration tests; produce coverage report |
| `ValidationAgent` | Security scan, input validation, performance check |
| `DocumentationAgent` | Generate README, API docs, architecture summary |
| `ReleaseReadinessAgent` | Verify all gates passed; produce readiness report |
| `ReleaseAgent` | Tag release, update changelog, deploy |

### Human Approval Checkpoints

The system enforces two mandatory human approval gates:

1. **Ambiguous Requirement Gate** — `RequirementAgent` blocks execution and requests
   clarification before `TaskPlannerAgent` runs. No code is generated from an
   unresolved ambiguous requirement.

2. **Release Gate** — `ReleaseReadinessAgent` produces a readiness report. A human
   reviewer must explicitly approve before `ReleaseAgent` executes deployment.

### Retry and Rollback Policy

- Each agent supports bounded retry: **maximum 3 attempts** with exponential backoff.
- On agent failure after max retries: workflow enters **safe-stop state**; all
  intermediate outputs are preserved for inspection.
- Brownfield deployments include a **rollback script** verified before production deployment.

### Audit Trail

Every agent decision is logged to the audit layer (`com.schwab.urlshortener.audit`)
with timestamp, agent name, input, output, decision rationale, and workflow ID.
Retrieve via `GET /api/v1/agent/audit/{workflowId}`.

---

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| SHA-256 + Base62 for short codes | Deterministic, collision-resistant, no external ID generator needed |
| Async analytics recording | Redirect latency is not blocked by analytics write failures |
| H2 in-memory database | Zero-config prototype; schema is PostgreSQL-compatible for production swap |
| Redis as optional dependency | Improves redirect performance; app degrades gracefully without it |
| In-memory workflow engine | Demonstrates agentic orchestration without requiring Temporal/Camunda infrastructure |
| Synchronous human approval gate | Ensures controlled autonomy; prevents unauthorized autonomous deployment |
| Flyway for schema migrations | Safe, versioned, repeatable schema changes in brownfield scenarios |

---

## Limitations and Trade-offs

| Limitation | Impact | Production Mitigation |
|-----------|--------|----------------------|
| H2 in-memory database | Data lost on restart | Replace with PostgreSQL |
| In-memory workflow state | Workflow lost on restart | Replace with Temporal or a persistent state store |
| No authentication on shorten API | Any caller can create URLs | Add OAuth2 / JWT authentication |
| Redis is optional | No caching in default mode | Make Redis required; add health check |
| Single-node deployment | No horizontal scaling | Add load balancer + distributed cache |
| Agent "execution" is simulated | Agents produce deterministic outputs, not LLM-driven | Integrate with LLM API for dynamic generation |

---

## Three Scenarios Demonstrated

See `docs/Scenarios.md` for full decomposition, agent workflows, and validation details.

| Scenario | Summary |
|----------|---------|
| **Greenfield** | Build URL shortener from scratch — full SDLC with parallel test/validation paths |
| **Brownfield** | Add per-click analytics to existing system — impact analysis, migration, regression gate |
| **Ambiguous** | "Improve reporting capabilities" — agent detects ambiguity, blocks, requests clarification |

---

## Future Improvements

| Improvement | Benefit |
|-------------|---------|
| PostgreSQL | Production-grade persistence with connection pooling |
| Kafka | Async event streaming for analytics at scale |
| OAuth2 / JWT | Authenticated URL creation and user-scoped analytics |
| Temporal.io | Durable, distributed workflow orchestration replacing in-memory engine |
| ELK Stack | Centralized logging and observability |
| Kubernetes | Horizontal scaling and production deployment |
| LLM Integration | True AI-driven agent decisions instead of simulated outputs |
| Distributed Rate Limiting | Redis-backed rate limiting per IP and per user |
