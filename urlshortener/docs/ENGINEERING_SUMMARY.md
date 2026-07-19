# Engineering Summary — Agentic URL Shortener

**Assignment:** Schwab Staff Engineer I — Agentic Software Engineering System  
**Deliverable:** URL Shortener with Agentic SDLC Orchestration  
**Submitted by:** Kavya Kolla  
**Repository:** https://github.com/kavya-kolla-1/urlshortener

---

## 1. Plan and Rationale

### Objective
Build a production-oriented URL Shortener that demonstrates AI-assisted software
engineering practices across the full SDLC — requirement understanding, task
decomposition, multi-agent execution, validation, and release — with human oversight
and approval gates at critical checkpoints.

### Approach
Rather than building a simple CRUD service, the solution is structured around an
**Agentic Orchestration Layer** — a workflow engine that models the SDLC as a
directed dependency graph of agent-owned tasks. Each agent is responsible for a
specific SDLC stage and produces a typed output that downstream agents consume.

The URL Shortener service itself serves as the engineering artifact that the agents
design, implement, test, validate, and release. This makes the agentic model
demonstrably end-to-end rather than theoretical.

### Why This Architecture
The second Schwab email clarified the core expectation: demonstrate how AI-assisted
engineering practices (Design Agent, Development Agent, QA Agent) are applied
throughout the SDLC. The architecture therefore maps directly to those three agent
categories, with additional agents for impact analysis (brownfield), release readiness,
and human approval gates.

---

## 2. Artifacts Produced

### Source Code
| Package | Purpose |
|---------|---------|
| `com.schwab.urlshortener.agent` | 12 agent classes + orchestrator + result model |
| `com.schwab.urlshortener.controller` | REST API controllers (URL, analytics, agent) |
| `com.schwab.urlshortener.service` | Business logic (URL shortening, analytics, audit) |
| `com.schwab.urlshortener.entity` | JPA entities (UrlMapping, ClickEvent) |
| `com.schwab.urlshortener.repository` | Spring Data JPA repositories |
| `com.schwab.urlshortener.exception` | Custom exceptions + GlobalExceptionHandler |
| `com.schwab.urlshortener.audit` | Audit event capture and persistence |
| `com.schwab.urlshortener.metrics` | Micrometer-based reliability metrics |
| `com.schwab.urlshortener.security` | Security configuration |
| `com.schwab.urlshortener.workflow` | Workflow state management |

### Documentation
| File | Purpose |
|------|---------|
| `README.md` | Setup, run, API reference, agent workflow commands |
| `docs/Architecture.md` | System architecture, component diagrams, data model, sequence flows |
| `docs/Architecture.drawio` | Visual architecture diagram (draw.io format) |
| `docs/Scenarios.md` | Greenfield, brownfield, and ambiguous scenario walkthroughs |
| `ENGINEERING_SUMMARY.md` | This document — plan, artifacts, risks, assumptions, limitations |

### Test Artifacts
| Artifact | Location |
|---------|---------|
| Unit tests | `src/test/java/com/schwab/urlshortener/service/` |
| Integration tests | `src/test/java/com/schwab/urlshortener/controller/` |
| Agent workflow tests | `src/test/java/com/schwab/urlshortener/agent/` |
| JaCoCo coverage report | `docs/coverage/index.html` |

---

## 3. Three Scenarios Demonstrated

### Scenario 1 — Greenfield
**Requirement:** Build a URL Shortener from scratch with analytics and reliability features.

**How the agentic system handled it:**
- `RequirementAgent` normalized the requirement into 5 APIs, 2 entities, and 4 assumptions.
- `TaskPlannerAgent` produced a 15-task dependency graph with parallel test/validation paths.
- `DesignAgent` generated the OpenAPI spec and database schema.
- `CodingAgent` implemented all layers (service, controller, repository, cache).
- `TestAgent` and `ValidationAgent` ran in parallel after implementation.
- `ReleaseReadinessAgent` verified all gates before surfacing the human approval checkpoint.
- `ReleaseAgent` executed deployment only after human approval was received.

**Key demonstration:** Parallel execution paths, entry/exit gates, and human-gated release.

---

### Scenario 2 — Brownfield
**Requirement:** Add per-click analytics (timestamp, IP, user agent, referrer) to an
existing URL Shortener that only has a basic click counter.

**How the agentic system handled it:**
- `ImpactAnalysisAgent` identified 4 affected components and 5 new components to introduce.
- Change was classified as additive-only — no breaking changes to existing contracts.
- Analytics recording wired as async fire-and-forget to protect redirect latency.
- Flyway migration (`V2__add_click_events.sql`) with corresponding rollback script.
- Regression gate enforced: all existing endpoint tests must pass before deployment.

**Key demonstration:** Impact analysis, migration strategy, rollback plan, regression gate.

---

### Scenario 3 — Ambiguous
**Requirement:** *"Improve reporting capabilities."*

**How the agentic system handled it:**
- `RequirementAgent` detected 6 dimensions of ambiguity before any implementation started.
- Raised a structured clarification request with multiple-choice questions for each gap.
- **Blocked at human clarification checkpoint** — no tasks generated until approved.
- After business stakeholder approved a refined requirement, `TaskPlannerAgent` generated
  a 10-task plan scoped to click count by URL + 30-day trend + CSV export.

**Key demonstration:** Agent-detected ambiguity, blocking checkpoint, requirement
normalization before implementation — preventing wasted work and wrong-scope delivery.

---

## 4. Risks, Trade-offs, and Mitigations

### Technical Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Short code collision | Low | High | SHA-256 + Base62 + DB unique constraint + retry on collision (max 3) |
| Redis unavailability | Medium | Medium | Graceful fallback to DB-only mode; cache miss is not a failure |
| H2 data loss on restart | High (by design) | Low (prototype) | Acknowledged trade-off; PostgreSQL swap documented |
| Analytics write failure | Low | Low | Async fire-and-forget; redirect is never blocked by analytics failure |
| Agent failure mid-workflow | Low | Medium | Bounded retry (3 attempts); safe-stop with state preserved |
| Ambiguous requirement proceeding to code | Medium | High | RequirementAgent hard-blocks before TaskPlannerAgent executes |
| Deployment without approval | Low | High | ReleaseAgent cannot execute without explicit human approval API call |

### Architecture Trade-offs

| Decision | Trade-off Accepted | Production Path |
|----------|-------------------|----------------|
| H2 in-memory DB | Data lost on restart | PostgreSQL with connection pool |
| In-memory workflow engine | Workflow lost on restart | Temporal.io for durable execution |
| Simulated agent execution | Not LLM-driven | Integrate Claude/GPT API for dynamic outputs |
| No authentication | Any caller can shorten URLs | OAuth2 / JWT with user-scoped URL ownership |
| Async analytics | Small risk of lost click events on crash | Kafka event stream with at-least-once delivery |
| Single-node deployment | No horizontal scaling | Kubernetes + distributed Redis + stateless service |

---

## 5. Assumptions

1. **Short code uniqueness:** SHA-256 + Base62 (first 6 characters) provides sufficient
   uniqueness for prototype scale (~56 billion combinations). Collision handling via
   retry is acceptable at this scale.

2. **Redis availability:** Redis is treated as an optional performance enhancement, not
   a hard dependency. The application degrades gracefully to direct DB reads on cache miss.

3. **Human approval is API-driven:** For this prototype, human approval is modeled as
   an explicit API call (`POST /api/v1/agent/approve/{workflowId}`). In production,
   this would integrate with a ticket/approval system (Jira, ServiceNow, Slack workflow).

4. **Agent execution is deterministic:** Agents in this prototype produce predefined,
   deterministic outputs demonstrating the orchestration model. In a production system,
   agents would call an LLM API for dynamic requirement interpretation and code generation.

5. **Analytics are append-only:** Click events are never updated or deleted. This
   simplifies the data model and ensures an immutable audit trail of link engagement.

6. **H2 is acceptable for evaluation:** The assignment specifies a working prototype.
   The schema is PostgreSQL-compatible and the switch is a single configuration change.

7. **Single-region deployment:** No multi-region failover is implemented. This is
   a deliberate scope boundary for the prototype.

---

## 6. Limitations

| Limitation | Scope Impact | Notes |
|-----------|-------------|-------|
| No authentication/authorization | Security | OAuth2 integration documented as next step |
| H2 in-memory storage | Persistence | Zero data survival across restarts |
| In-memory workflow state | Reliability | Workflow lost if JVM restarts mid-execution |
| Simulated agent AI | Intelligence | Agents execute scripted logic, not LLM-generated decisions |
| No rate limiting | Availability | Can be abused; Redis-based rate limiting is the documented fix |
| No distributed tracing | Observability | Audit trail exists but no OpenTelemetry / Jaeger integration |
| No load testing results | Performance | Redirect latency target (< 200ms p99) not empirically validated |
| Manual Git commit history | Process | Commits represent development progression but not AI-generated commit messages |

---

## 7. Engineering Principles Applied

### Modularity
Each agent, service, and repository is a single-responsibility class with a clear
interface. No agent has direct knowledge of another agent's internals — all
communication goes through `AgentOrchestrator` via typed `AgentResult` objects.

### Testability
Service layer is interface-driven, enabling clean unit testing with Mockito.
Controller layer is tested via MockMvc without starting a full server.
Agent workflow is tested by injecting mock agents into the orchestrator.

### Reliability
- Bounded retries with exponential backoff on agent failure.
- Safe-stop state preserves intermediate outputs for inspection.
- Rollback scripts for brownfield database migrations.
- Async analytics ensures redirect availability is not coupled to analytics writes.

### Security
- `GlobalExceptionHandler` prevents stack trace leakage in error responses.
- URL input validated via Bean Validation before processing.
- Custom exceptions (`DuplicateUrlException`, `ResourceNotFoundException`) return
  structured, safe error payloads.
- Security package provides configuration hooks for auth integration.

### Scalability (Design-Ready)
- Stateless service layer — no session state; horizontal scaling ready.
- Redis cache layer reduces database read load on redirect hot path.
- PostgreSQL-compatible schema — production swap is a config change, not a refactor.
- Async analytics decouples write throughput from redirect throughput.

### Controlled Autonomy
The core principle across all scenarios: **agents execute, humans own oversight**.
No deployment happens without an explicit human approval API call. No implementation
starts on an ambiguous requirement. Every agent decision is logged to the audit trail.

---

## 8. AI Assistance Acknowledgment

This project was developed with AI assistance (Claude/Copilot) as encouraged by the
assignment. AI tools were used for:
- Requirement normalization and task decomposition modeling
- Agent workflow design and dependency graph structuring
- Code scaffolding for service, controller, and agent layers
- Test case generation for service and controller layers
- Documentation drafting (README, Architecture, Scenarios, this summary)

All AI-generated outputs were reviewed, validated, and refined to ensure correctness,
consistency with the codebase, and alignment with Schwab's engineering expectations.
Engineering judgment was applied at every step — AI accelerated the work; it did not
replace the engineering decisions.
