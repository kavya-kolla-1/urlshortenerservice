# Agentic URL Shortener — Architecture

This document describes the system architecture, component responsibilities, data flow,
agentic orchestration model, and key design decisions for the URL Shortener service.

---

## 1. High-Level System Architecture

```mermaid
graph TD
    Client["Client (Browser / API Consumer)"]

    Client -->|POST /api/v1/url/shorten| UrlController
    Client -->|GET /{shortCode}| UrlController
    Client -->|GET /api/v1/url/analytics/{code}| AnalyticsController
    Client -->|POST /api/v1/agent/run| AgentController

    subgraph Application Layer
        UrlController["UrlController"]
        AnalyticsController["AnalyticsController"]
        AgentController["AgentController"]
    end

    subgraph Service Layer
        UrlService["UrlService"]
        AnalyticsService["AnalyticsService"]
        AuditService["AuditService"]
    end

    subgraph Infrastructure
        RedisCache["Redis Cache\n(redirect fast path)"]
        H2DB["H2 Database\n(UrlMapping, ClickEvent)"]
    end

    subgraph Agentic Orchestration
        AgentOrchestrator["AgentOrchestrator\n(Workflow Engine)"]
    end

    UrlController --> UrlService
    AnalyticsController --> AnalyticsService
    AgentController --> AgentOrchestrator

    UrlService --> RedisCache
    UrlService --> H2DB
    UrlService --> AuditService
    AnalyticsService --> H2DB
    AnalyticsService --> AuditService
```

---

## 2. Agentic Orchestration Model

The `AgentOrchestrator` executes the SDLC pipeline as a directed dependency graph.
Agents run sequentially or in parallel based on task dependencies. Human approval
gates block execution at critical checkpoints.

```mermaid
flowchart TD
    START([Requirement Input]) --> RA

    RA["RequirementAgent\nNormalize + detect ambiguity"]
    TPA["TaskPlannerAgent\nBuild dependency graph"]
    DA["DesignAgent\nAPI contracts + DB schema + architecture doc"]
    CA["CodingAgent\nService + controller + repository + cache"]
    TA["TestAgent\nUnit + integration tests + coverage report"]
    VA["ValidationAgent\nSecurity + input validation + performance"]
    DOC["DocumentationAgent\nREADME + API docs + architecture summary"]
    RRA["ReleaseReadinessAgent\nVerify all gates passed"]
    HUMAN{{"⛔ HUMAN APPROVAL\nCheckpoint"}}
    REL["ReleaseAgent\nTag + changelog + deploy"]
    DONE([Release Complete])

    RA -->|Ambiguous?| CLARIFY{{"⛔ CLARIFICATION\nCheckpoint"}}
    CLARIFY -->|Approved requirement| TPA
    RA -->|Clear requirement| TPA

    TPA --> DA
    DA --> CA
    CA --> TA
    CA --> VA
    TA --> DOC
    VA --> DOC
    DOC --> RRA
    RRA --> HUMAN
    HUMAN -->|Approved| REL
    HUMAN -->|Rejected| CA
    REL --> DONE
```

---

## 3. Agent Responsibility Map

```mermaid
graph LR
    subgraph SDLC Stage: Requirements
        REQ["RequirementAgent\n- Parse requirement\n- Detect ambiguity\n- Define APIs + data model\n- Block if unclear"]
    end

    subgraph SDLC Stage: Planning
        PLAN["TaskPlannerAgent\n- Build dependency graph\n- Assign agent owners\n- Identify parallel paths\n- Sequence tasks"]
    end

    subgraph SDLC Stage: Design
        DESIGN["DesignAgent\n- Generate OpenAPI spec\n- Create DB schema\n- Produce architecture doc"]
        IMPACT["ImpactAnalysisAgent\n- Brownfield only\n- Identify affected components\n- Classify risk\n- Propose migration"]
    end

    subgraph SDLC Stage: Implementation
        CODE["CodingAgent\n- Implement service layer\n- Implement controllers\n- Implement repositories\n- Wire caching"]
    end

    subgraph SDLC Stage: Quality
        TEST["TestAgent\n- Unit tests\n- Integration tests\n- Coverage report (JaCoCo)"]
        VALID["ValidationAgent\n- Security scan\n- Input validation\n- Performance check"]
    end

    subgraph SDLC Stage: Release
        DOCS["DocumentationAgent\n- README\n- API docs\n- Architecture summary"]
        RR["ReleaseReadinessAgent\n- Verify all gates\n- Produce readiness report"]
        REL["ReleaseAgent\n- Tag release\n- Update changelog\n- Deploy"]
    end

    REQ --> PLAN --> DESIGN --> CODE --> TEST --> DOCS --> RR --> REL
    PLAN --> IMPACT --> CODE
    CODE --> VALID --> DOCS
```

---

## 4. Data Model

```mermaid
erDiagram
    URL_MAPPING {
        Long id PK
        String originalUrl
        String shortCode UK
        LocalDateTime createdAt
        LocalDateTime expiresAt
        Long clickCount
        String createdBy
        Boolean active
    }

    CLICK_EVENT {
        Long id PK
        String shortCode FK
        LocalDateTime clickedAt
        String ipAddress
        String userAgent
        String referrer
        String country
    }

    AUDIT_EVENT {
        Long id PK
        String workflowId
        String agentName
        String action
        String input
        String output
        String decision
        LocalDateTime timestamp
        String status
    }

    URL_MAPPING ||--o{ CLICK_EVENT : "has many"
```

---

## 5. Request Flow — URL Shortening

```mermaid
sequenceDiagram
    participant Client
    participant UrlController
    participant UrlService
    participant Redis
    participant H2
    participant AuditService

    Client->>UrlController: POST /api/v1/url/shorten {originalUrl}
    UrlController->>UrlService: shorten(originalUrl)
    UrlService->>UrlService: validate(originalUrl)
    UrlService->>UrlService: generateShortCode() [SHA-256 + Base62]
    UrlService->>H2: save(UrlMapping)
    H2-->>UrlService: saved entity
    UrlService->>Redis: cache(shortCode → originalUrl)
    UrlService->>AuditService: log(SHORTEN, shortCode)
    UrlService-->>UrlController: ShortenResponse
    UrlController-->>Client: 201 Created {shortCode, shortUrl}
```

---

## 6. Request Flow — Redirect

```mermaid
sequenceDiagram
    participant Client
    participant UrlController
    participant UrlService
    participant Redis
    participant H2
    participant AnalyticsService

    Client->>UrlController: GET /{shortCode}
    UrlController->>UrlService: redirect(shortCode)
    UrlService->>Redis: get(shortCode)

    alt Cache Hit
        Redis-->>UrlService: originalUrl
    else Cache Miss
        UrlService->>H2: findByShortCode(shortCode)
        H2-->>UrlService: UrlMapping
        UrlService->>Redis: cache(shortCode → originalUrl)
    end

    UrlService->>AnalyticsService: recordClick(shortCode, request) [async]
    UrlService-->>UrlController: originalUrl
    UrlController-->>Client: 302 Redirect → originalUrl
```

---

## 7. Agent Retry and Rollback Policy

```mermaid
flowchart TD
    EXEC["Agent.execute()"]
    SUCCESS["✅ Success\nProceed to next stage"]
    RETRY["Retry with backoff\n(attempt 2 of 3)"]
    RETRY2["Retry with backoff\n(attempt 3 of 3)"]
    SAFESTOP["⛑ Safe-Stop\nPreserve state\nAlert operator"]
    ROLLBACK["🔄 Rollback\n(Brownfield: run rollback SQL\nRevert service to previous tag)"]

    EXEC --> SUCCESS
    EXEC -->|Failure attempt 1| RETRY
    RETRY --> SUCCESS
    RETRY -->|Failure attempt 2| RETRY2
    RETRY2 --> SUCCESS
    RETRY2 -->|Failure attempt 3| SAFESTOP
    SAFESTOP -->|Brownfield deployment| ROLLBACK
    SAFESTOP -->|Greenfield| ALERT["Alert: Manual intervention required"]
```

---

## 8. Package Structure

```
com.schwab.urlshortener
├── agent
│   ├── Agent.java                    ← Base agent contract (interface)
│   ├── AgentOrchestrator.java        ← Workflow engine + dependency graph
│   ├── AgentResult.java              ← Standardized agent output
│   ├── RequirementAgent.java         ← Requirement parsing + ambiguity detection
│   ├── TaskPlannerAgent.java         ← Dependency graph construction
│   ├── DesignAgent.java              ← API contract + schema generation
│   ├── CodingAgent.java              ← Implementation coordination
│   ├── ImpactAnalysisAgent.java      ← Brownfield impact classification
│   ├── TestAgent.java                ← Test generation + coverage reporting
│   ├── ValidationAgent.java          ← Security + performance validation
│   ├── DocumentationAgent.java       ← Doc generation
│   ├── ReleaseReadinessAgent.java    ← Gate verification + readiness report
│   └── ReleaseAgent.java             ← Deployment execution
├── audit
│   ├── AuditEvent.java               ← Audit record entity
│   ├── AuditService.java             ← Audit interface
│   └── AuditServiceImpl.java         ← Audit persistence implementation
├── controller
│   ├── UrlController.java            ← REST endpoints for URL operations
│   └── AnalyticsController.java      ← REST endpoints for analytics
├── service
│   ├── UrlService.java               ← URL shortening interface
│   ├── UrlServiceImpl.java           ← Shortening + redirect logic
│   ├── AnalyticsService.java         ← Analytics interface
│   └── AnalyticsServiceImpl.java     ← Click recording + query logic
├── entity
│   ├── UrlMapping.java               ← JPA entity: URL mapping
│   └── ClickEvent.java               ← JPA entity: per-click analytics
├── repository
│   ├── UrlMappingRepository.java     ← Spring Data JPA repository
│   └── ClickEventRepository.java     ← Spring Data JPA repository
├── dto
│   ├── ShortenRequest.java
│   ├── ShortenResponse.java
│   └── AnalyticsResponse.java
├── exception
│   ├── DuplicateUrlException.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java   ← @ControllerAdvice
├── security
│   └── ...                           ← Security configuration
├── metrics
│   └── ...                           ← Custom metrics (Micrometer)
├── logging
│   └── ...                           ← Structured logging config
├── workflow
│   └── ...                           ← Workflow state management
└── config
    └── ...                           ← Redis, cache, Swagger config
```

---

## 9. Key Architecture Decisions

| Decision | Chosen Approach | Alternative Considered | Reason |
|----------|----------------|----------------------|--------|
| Short code generation | SHA-256 + Base62 (6 chars) | UUID, NanoID, sequential ID | Deterministic, collision-resistant, no DB round-trip for generation |
| Caching strategy | Redis with cache-aside | No cache, write-through | Cache-aside gives control; graceful degradation if Redis unavailable |
| Analytics write path | Async fire-and-forget | Synchronous write | Redirect p99 latency not blocked by analytics DB write |
| Database | H2 in-memory | PostgreSQL | Zero-config for prototype; schema is PostgreSQL-compatible |
| Workflow engine | In-memory state machine | Temporal, Camunda, Quartz | Demonstrates orchestration concepts without infrastructure overhead |
| Human approval | Synchronous blocking API call | Event-driven approval | Explicit, auditable; evaluator can trigger and observe the gate |
| Error handling | GlobalExceptionHandler + custom exceptions | Per-controller try/catch | Centralized, consistent error responses across all endpoints |
| Audit trail | Dedicated AuditService + AuditEvent entity | Application logs only | Queryable audit records; supports compliance and traceability requirements |
