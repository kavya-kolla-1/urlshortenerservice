# Agentic Workflow Scenarios

This document demonstrates how the agentic orchestration layer handles three distinct
engineering scenarios: greenfield development, brownfield enhancement, and ambiguous
requirement resolution. Each scenario shows requirement understanding, task decomposition
with dependencies, agent workflow, validation gates, and human approval checkpoints.

---

## 1. Greenfield Scenario

### Requirement
Develop a new URL Shortener application from scratch with core APIs, click analytics,
caching, and reliability features.

### Requirement Understanding
The RequirementAgent analyzes the input and produces the following normalized understanding:

- **Business goal:** Shorten long URLs, redirect users, and track link engagement.
- **Assumptions:**
  - Short codes are 6-character Base62 strings derived from SHA-256 hash.
  - Anonymous access is allowed for redirect; authenticated access required for creation.
  - Analytics are append-only (click events are never deleted).
  - H2 used for prototype; PostgreSQL-compatible schema for production.
- **APIs defined:**
  - `POST /api/urls` — Create short URL
  - `GET /{shortCode}` — Redirect to original URL
  - `GET /api/urls/{shortCode}/analytics` — Retrieve click analytics
  - `GET /api/urls/{shortCode}` — Get URL metadata
  - `DELETE /api/urls/{shortCode}` — Delete URL mapping
- **Data model:**
  - `UrlMapping` — id, originalUrl, shortCode, createdAt, expiresAt, clickCount
  - `ClickEvent` — id, shortCode, clickedAt, ipAddress, userAgent, referrer

### Task Decomposition (with Dependencies)

| Task | ID | Depends On | Owner Agent |
|------|----|------------|-------------|
| Design REST API contracts | T1 | — | DesignAgent |
| Create database schema | T2 | T1 | DesignAgent |
| Implement URL shortening logic | T3 | T1, T2 | CodingAgent |
| Implement redirect with cache | T4 | T3 | CodingAgent |
| Implement analytics recording | T5 | T3, T4 | CodingAgent |
| Implement analytics query API | T6 | T5 | CodingAgent |
| Write unit tests | T7 | T3, T4, T5, T6 | TestAgent |
| Write integration tests | T8 | T7 | TestAgent |
| Generate coverage report | T9 | T7, T8 | TestAgent |
| Security validation | T10 | T3, T4 | ValidationAgent |
| Performance validation | T11 | T4, T5 | ValidationAgent |
| Generate documentation | T12 | T6 | DocumentationAgent |
| Release readiness check | T13 | T9, T10, T11, T12 | ReleaseReadinessAgent |
| Human approval checkpoint | T14 | T13 | Human |
| Deploy | T15 | T14 | ReleaseAgent |

**Parallel execution paths (T3–T6 run sequentially; T7–T9 and T10–T11 run in parallel after T6):**

```
T1 → T2 → T3 → T4 → T5 → T6
                              ↓              ↓
                         [T7 → T8 → T9]  [T10, T11]  ← parallel
                              ↓              ↓
                              T12 ← sync ──┘
                              ↓
                              T13 (Release Readiness Gate)
                              ↓
                              T14 (Human Approval)
                              ↓
                              T15 (Deploy)
```

### Agent Workflow

```
RequirementAgent       → Normalize requirement, identify assumptions, define APIs
        ↓
TaskPlannerAgent       → Build dependency graph, assign agent owners, sequence tasks
        ↓
DesignAgent            → Generate API contracts (OpenAPI), DB schema, architecture doc
        ↓
CodingAgent            → Implement service, controller, repository, caching layers
        ↓
        ├──────────────────────────────┐
        ↓                             ↓
TestAgent                       ValidationAgent
(unit + integration tests,      (security scan, input validation,
 coverage report)                performance check)
        ↓                             ↓
        └──────────────┬──────────────┘
                       ↓
              DocumentationAgent   → Generate README, API docs, architecture summary
                       ↓
              ReleaseReadinessAgent → Verify all gates passed, produce readiness report
                       ↓
              ⛔ HUMAN APPROVAL CHECKPOINT
              (Reviewer checks coverage ≥ 80%, no critical security findings,
               readiness report signed off)
                       ↓
              ReleaseAgent           → Tag release, update changelog, deploy
```

### Entry and Exit Gates

| Stage | Entry Condition | Exit Condition |
|-------|----------------|----------------|
| Design | Requirement approved by RequirementAgent | OpenAPI spec + DB schema produced |
| Implementation | Design artifacts present | All endpoints return expected responses |
| Testing | Implementation complete | Coverage ≥ 80%, zero test failures |
| Validation | Tests passed | No critical security findings, p99 latency < 200ms |
| Release | All gates green + human approval received | Deployment successful, smoke test passed |

### Validation
- Unit tests cover service and controller layers.
- Integration tests validate end-to-end shorten → redirect → analytics flow.
- Security validation: input sanitization, URL format enforcement, rate limiting check.
- Performance validation: redirect p99 latency target < 200ms under simulated load.
- Coverage report generated via JaCoCo (target: 80% line coverage).

---

## 2. Brownfield Scenario

### Existing System
A working URL Shortener with `POST /api/urls` and `GET /{shortCode}` endpoints exists.
It stores URL mappings with a basic click counter in `UrlMapping.clickCount`. No
time-series analytics, no per-click metadata, and no analytics query API exist.

### Requirement
Add per-click analytics (timestamp, IP, user agent, referrer) with a queryable API,
without breaking existing shorten and redirect functionality.

### Impact Analysis (ImpactAnalysisAgent Output)

**Affected existing components:**

| Component | Change Type | Risk |
|-----------|-------------|------|
| `UrlController` | Add new endpoint `GET /api/urls/{shortCode}/analytics` | Low — additive only |
| `UrlService` | Add call to `AnalyticsService.record()` on every redirect | Medium — adds latency to redirect path |
| `UrlMapping` | Retain `clickCount` for backward compatibility; add async increment | Low |
| Database schema | Add new `click_events` table via migration | Medium — requires Flyway migration |

**New components introduced:**

| Component | Purpose |
|-----------|---------|
| `ClickEvent` | Entity capturing per-click metadata |
| `ClickEventRepository` | JPA repository for click events |
| `AnalyticsService` | Business logic for recording and querying click data |
| `AnalyticsController` | Exposes analytics query API |
| `V2__add_click_events.sql` | Flyway migration script |

**No breaking changes:** Existing `POST /api/urls` and `GET /{shortCode}` contracts
are unchanged. Analytics recording is fire-and-forget (async) to avoid redirect latency impact.

### Task Decomposition (with Dependencies)

| Task | ID | Depends On |
|------|----|------------|
| Impact analysis | T1 | — |
| Write Flyway migration V2 | T2 | T1 |
| Implement ClickEvent entity + repository | T3 | T2 |
| Implement AnalyticsService | T4 | T3 |
| Wire async analytics call into redirect path | T5 | T4 |
| Implement AnalyticsController | T6 | T4 |
| Write regression tests for existing endpoints | T7 | T5 |
| Write unit tests for new analytics logic | T8 | T4, T6 |
| Validate no breaking changes | T9 | T7 |
| Human approval | T10 | T8, T9 |
| Deploy with migration | T11 | T10 |

### Agent Workflow

```
RequirementAgent       → Understand change scope, confirm additive-only constraint
        ↓
ImpactAnalysisAgent    → Identify affected components, classify risk, propose migration
        ↓
TaskPlannerAgent       → Sequence tasks, flag regression testing as mandatory gate
        ↓
CodingAgent            → Implement new entities, services, controller, async wiring
        ↓
        ├──────────────────────────────┐
        ↓                             ↓
TestAgent                       ValidationAgent
(regression tests on existing   (confirm no breaking changes,
 endpoints + new analytics tests) validate DB migration)
        ↓                             ↓
        └──────────────┬──────────────┘
                       ↓
              ReleaseReadinessAgent → Migration dry-run, rollback script verified
                       ↓
              ⛔ HUMAN APPROVAL CHECKPOINT
              (Confirm regression suite green, migration rollback tested)
                       ↓
              ReleaseAgent           → Apply migration, deploy, verify analytics endpoint
```

### Rollback Plan
If deployment fails after migration:
1. Run `V2__rollback_click_events.sql` to drop the new table.
2. Revert service code to previous tag.
3. `UrlMapping.clickCount` continues working as before — zero data loss.

### Validation
- All existing endpoint tests pass without modification (regression gate).
- `POST /api/urls` response contract unchanged.
- `GET /{shortCode}` redirect behavior unchanged.
- New `GET /api/urls/{shortCode}/analytics` returns correct click data.
- DB migration applies cleanly and rollback script verified in staging.

---

## 3. Ambiguous Scenario

### Raw Requirement (as received)
> *"Improve reporting capabilities."*

### Agent Actions: Ambiguity Resolution

**Step 1 — RequirementAgent detects ambiguity**

The agent identifies the following gaps before any implementation begins:

| Ambiguity | Why It Matters |
|-----------|----------------|
| "Reporting" is undefined | Could mean analytics API, CSV export, dashboard, or email reports |
| No scope boundary | Applies to all URLs or per-user only? |
| No time range specified | Last 7 days? All time? Custom range? |
| No output format | JSON API, CSV download, PDF, or UI chart? |
| No performance expectation | Real-time or batch? Acceptable latency? |
| No access control | All users or admin only? |

**Step 2 — Clarification questions sent to business stakeholder**

```
RequirementAgent → Business Stakeholder:

1. Which reports are needed?
   (a) Click count by URL  (b) Click trend over time  (c) Top URLs by traffic  (d) All of the above

2. What time range should reports cover?
   (a) Last 7 days  (b) Last 30 days  (c) Custom date range  (d) All time

3. What is the expected output format?
   (a) JSON API response  (b) CSV download  (c) Both

4. What is the acceptable response time for report generation?
   (a) Real-time (< 2s)  (b) Near-real-time (< 30s)  (c) Batch/async (minutes)

5. Who can access reports?
   (a) Any authenticated user for their own URLs  (b) Admin only  (c) Public
```

**Step 3 — Business approves refined requirement**

```
Approved Requirement:
  - Reports needed: Click count by URL + Click trend over time (last 30 days)
  - Output format: JSON API response + CSV download
  - Performance: Real-time, response < 2 seconds
  - Access control: Any authenticated user, scoped to their own URLs
  - Out of scope: Email reports, PDF export, admin dashboard
```

**Step 4 — TaskPlannerAgent generates concrete task plan from approved requirement**

| Task | ID | Depends On |
|------|----|------------|
| Define analytics query API contracts | T1 | — |
| Implement click count by URL query | T2 | T1 |
| Implement 30-day click trend query | T3 | T1 |
| Add CSV export endpoint | T4 | T2, T3 |
| Add authentication scope filter | T5 | T2, T3 |
| Write unit tests for query logic | T6 | T2, T3, T4, T5 |
| Write integration tests | T7 | T6 |
| Validate performance (< 2s target) | T8 | T7 |
| Human approval | T9 | T8 |
| Deploy | T10 | T9 |

### Agent Workflow

```
RequirementAgent       → Detect ambiguity, generate clarification questions
        ↓
        ⛔ HUMAN CLARIFICATION CHECKPOINT
        (Business stakeholder reviews questions and approves refined requirement)
        ↓
RequirementAgent       → Normalize approved requirement, confirm scope boundary
        ↓
TaskPlannerAgent       → Generate dependency graph from refined requirement
        ↓
DesignAgent            → Define analytics query API contracts
        ↓
CodingAgent            → Implement queries, CSV export, auth scope filter
        ↓
TestAgent              → Unit + integration tests, performance validation
        ↓
ValidationAgent        → Confirm < 2s response, auth scoping verified
        ↓
ReleaseReadinessAgent  → All gates green
        ↓
        ⛔ HUMAN APPROVAL CHECKPOINT
        (Final sign-off before release)
        ↓
ReleaseAgent           → Deploy
```

### Key Principle Demonstrated
The agent does **not** proceed to implementation on an ambiguous requirement.
It blocks at the clarification checkpoint and waits for human input. This prevents
wasted work, wrong-scope implementation, and rework — a core benefit of controlled
agent autonomy with human oversight.

### Validation
- Business approval document captured before implementation begins.
- Click count API returns correct aggregation per shortCode.
- Trend API returns 30 daily data points for the past 30 days.
- CSV export produces valid, parseable output.
- Auth filter prevents users from accessing other users' URL reports.
- All queries respond within 2 seconds under normal load.

---

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Duplicate short code collision | Low | High | SHA-256 + Base62 + DB unique constraint + retry on collision |
| Database failure during redirect | Medium | High | Retry with backoff (max 3 attempts); fallback to cache |
| Invalid or malicious URL input | Medium | Medium | Bean Validation + URL format regex + blocklist check |
| Analytics recording failure | Low | Low | Async fire-and-forget; redirect never blocked by analytics failure |
| Deployment failure post-migration | Low | High | Rollback script tested in staging before production deployment |
| Agent failure mid-workflow | Low | Medium | Bounded retry (3 attempts); safe-stop with state preserved |
| Policy violation (security scan) | Medium | High | ValidationAgent blocks release if critical findings present |
| Ambiguous requirement proceeding to implementation | Medium | High | RequirementAgent blocks and requests clarification before TaskPlanner runs |
| Coverage below threshold | Medium | Medium | TestAgent reports coverage; ReleaseReadinessAgent blocks if < 80% |

---

## Trade-offs

| Decision | Chosen Approach | Trade-off |
|----------|----------------|-----------|
| Database | H2 (in-memory) for prototype | Simple setup; not production-grade (swap to PostgreSQL for prod) |
| Caching | Redis configured | Reduces DB load on redirect; adds operational dependency |
| Analytics recording | Async (fire-and-forget) | Redirect latency unaffected; small risk of lost click events under failure |
| Short code generation | SHA-256 + Base62 (first 6 chars) | Fast and deterministic; collision probability ~1 in 56 billion, handled by retry |
| Orchestration | In-memory workflow engine | Demonstrates agentic model without distributed infrastructure complexity |
| Human approval | Synchronous blocking checkpoint | Ensures controlled autonomy; adds latency to release cycle by design |
| Brownfield migration | Flyway versioned migration | Safe, repeatable schema change; requires rollback script discipline |

---

## Conclusion

This document demonstrates three engineering scenarios handled by the agentic orchestration
layer:

| Scenario | Key Demonstration |
|----------|------------------|
| **Greenfield** | Full SDLC from requirement to release with parallel test/validation paths and human approval gate |
| **Brownfield** | Impact analysis, additive-only change strategy, regression gate, rollback plan |
| **Ambiguous** | Agent-detected ambiguity, clarification checkpoint, requirement normalization before any implementation |

Across all three scenarios, the system enforces:
- Dependency-sequenced task execution (not blind linear chaining)
- Human approval checkpoints before high-impact actions
- Entry and exit gates per SDLC stage
- Bounded retry and rollback controls
- Audit-traceable agent decision lineage
- Controlled autonomy — agents execute; humans own oversight and final approval
