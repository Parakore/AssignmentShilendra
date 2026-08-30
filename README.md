# Road Cutting Permission (RCP) — API & Municipal Portal

A production-grade, multi-tenant municipal service platform for granting public road cutting permissions. Built with **Java 25 / Spring Boot 3**, **PostgreSQL & Flyway**, and a responsive **React 18 / TypeScript / Tailwind CSS** portal.

---

## 1. How to Run from a Clean Machine

### Option A: One-Command Startup (Recommended — Stretch Feature)
Prerequisites: [Docker](https://docs.docker.com/get-docker/) & Docker Compose.

```bash
docker compose up --build
```
- **Portal UI**: [http://localhost:3000](http://localhost:3000)
- **API Service**: [http://localhost:8080](http://localhost:8080)
- **PostgreSQL Database**: `localhost:5432` (`rcp_db` / `postgres` / `postgrespassword`)

---

### Option B: Local Development Run

#### Prerequisites:
- Java 17 or higher (`java -version`)
- Maven 3.9+ (`mvn -v`)
- Node.js 18+ & npm (`node -v`, `npm -v`)
- PostgreSQL (or uses in-memory H2 with PostgreSQL dialect for testing)

#### 1. Start Backend
```bash
cd backend
mvn clean spring-boot:run
```
*Runs on port `8080` with Flyway database migrations auto-applied.*

#### 2. Start Frontend Portal
```bash
cd frontend
npm install
npm run dev
```
*Portal runs at [http://localhost:3000](http://localhost:3000) with automatic proxying to `http://localhost:8080`.*

---

## 2. Passing Tests & Verification

Run the comprehensive unit and integration test suite:

```bash
cd backend
mvn clean test
```

### Test Coverage Highlights
- **Calculation Worked Example A (Dehradun BT)**: Verifies exact fee breakdown and total `₹24,485` with Addendum 3.1 `reviewRef: "K7Q2"`.
- **Calculation Worked Example B (Haridwar BT Tenant Override)**: Verifies overridden day-rate (₹20) and deposit floor (₹7,500) totaling `₹27,480`.
- **Inactive Road Type Rejection**: Verifies `KUTCHA` is rejected with `INVALID_ROAD_TYPE` 4xx error.
- **Government Agency Exemption**: Verifies `GOVERNMENT_AGENCY` applicant receives ₹0 permission fee and ₹0 surcharge while restoration and deposit apply.
- **Urgency Threshold Strict Comparison**: Verifies boundary strictly at `< 3 days` (3 days away = ₹0 surcharge; 2 days away = 10% surcharge).
- **Area Ceil Rounding**: Confirms whole square metres rounded up on the dimension product (`12.1 × 1.1 = 13.31 → 14 m²`).
- **Role-Gated Transitions**: Tests `VERIFY` & `SEND_BACK` (Junior Engineer), `APPROVE` & `REJECT` (Executive Engineer), `CANCEL` (Applicant).
- **Illegal Transitions & RBAC**: Confirms 4xx on illegal transitions (Applicant attempting to approve own file, Approver approving unverified file, etc.).
- **Tenant Isolation**: Confirms Haridwar officers cannot view, search, or act on Dehradun files.
- **Atomic Sequence Generation**: Tests Indian Financial Year boundaries and non-colliding formatted sequence IDs.

---

## 3. What Was Built

### Backend Architecture (Spring Boot 3)
- **Rate Engine Behind Interface (`RateProvider`)**: Decoupled rate resolution loading `rates-config.json` with fallback defaults and tenant overrides. Allows future swap for remote config services without modifying math logic.
- **Exact Monetary Arithmetic**: Uses `BigDecimal` with `HALF_UP` rounding to avoid double-precision floating point bugs.
- **Config-Driven State Machine (`WorkflowProvider`)**: Lifecycle transitions and role permissions are loaded from `workflow-config.json` rather than branching `if/else` logic in services.
- **Flyway Migrations (`db/migration/V1__init_rcp_schema.sql`)**: Manages tables for applications, action history audit timeline, and atomic sequence counters with `created_by`, `created_time`, `last_modified_by`, `last_modified_time`. `ddl-auto` is set to `validate`.
- **Strict Multi-Tenant Scoping**: All queries and mutations validate `tenantId` match against `RequestInfo.userInfo.tenantId`.
- **Addendum 3.1 Compliance**:
  - Calculation responses include `"reviewRef": "K7Q2"`.
  - Schema and README include required metadata.

### Frontend Portal (React + TypeScript + Tailwind CSS)
- **Multi-Persona Switcher**: Dropdown in the header allows instant toggling between:
  - Applicant (Dehradun / Haridwar)
  - Junior Engineer / Verifier (Dehradun / Haridwar)
  - Executive Engineer / Approver (Dehradun / Haridwar)
- **Applicant Experience (Usable down to 360px)**:
  - Form with real-time per-field validation.
  - Live debounced fee preview calling `POST /rcp/v1/_calculate` with crystal-clear line-item breakdowns.
  - Quick scenario buttons (Example A, Example B, Govt Agency, Inactive Kutcha).
  - Draft survival: form inputs persist in `localStorage` across page reloads.
  - Dynamic status timeline rendered from immutable transition history.
  - Edit & Resubmit support when application is in `APPLIED` state after `SEND_BACK`.
- **Officer Desktop Desk Queue**:
  - Filterable tabs by status (`APPLIED`, `PENDING_APPROVAL`, `APPROVED`, etc.).
  - Search by application number or applicant phone number.
  - Action buttons dynamically populated from API-reported allowed actions for the active role.
  - Modal dialogues for entering verification notes, send back corrections, or approval remarks.

---

## 4. What Was Deliberately Not Built

- **Full User Authentication / Login UI**: Identity is taken from `RequestInfo.userInfo` in the request body as per the specification.
- **File Upload / Document Attachments**: Document proofs (e.g. NOC copies, road cut drawings) are out of scope for the core lifecycle.
- **Payment Gateway Integration**: Payment processing is represented by the computed fee breakdown and statutory security deposit.

---

## 5. Assumptions

- **Indian Financial Year**: Follows April 1 to March 31 cycle (e.g. March 2026 is `2025-26`, while April 2026 and August 2026 are `2026-27`).
- **Urgency Comparison**: Evaluated using strict calendar day difference `ChronoUnit.DAYS.between(applicationDate, proposedStartDate) < urgencyThresholdDays`. Exactly 3 days is standard timeline (0% surcharge).
- **Tenant Scope**: Tenant is resolved from `RequestInfo.userInfo.tenantId`. Cross-tenant mutations or queries are blocked with HTTP 403 / 404.

---

## 6. Why Application Numbers Cannot Collide

> Application numbers cannot collide because sequence generation uses database-level pessimistic write locking (`SELECT ... FOR UPDATE` via `PESSIMISTIC_WRITE`) on the `rcp_application_sequence` table scoped strictly to composite primary key `(tenant_id, financial_year)` within serializable database transactions.

---

## 7. Stretch Item Picked

**One-command startup** (with Docker Compose, multi-stage backend and frontend Dockerfiles, and automated PostgreSQL health checks) + **Draft Survival** (local storage caching of in-progress citizen forms).

---

## 8. AI Usage Note

- **AI Tools Used**: Coding assistant for scaffolding boilerplate, Maven POM dependency alignment, test case permutations, and TypeScript type sync.
- **One place it helped**: Generating comprehensive test matrices covering boundary cases in fee calculation (e.g. government agency 0-surcharge rule and security deposit floor max comparisons).
- **One place it misled**: Initially modeled financial years on calendar years (`2026-2026`) instead of the Indian fiscal year cycle (`2025-26` vs `2026-27`), which was corrected with dedicated date utility tests.

---

## 9. Time Taken

Roughly **4.5 hours** total:
- Backend configuration engine, entities, sequence generator, and services: ~1.5 h
- Unit and integration tests: ~0.75 h
- Frontend UI, responsive components, fee calculator, timeline, and persona switcher: ~1.5 h
- Docker Compose, Flyway validation, and documentation: ~0.75 h

---

## 10. Written Answers (§4)

### 1. Rate Versioning
*Rates change by government order, mid-year, and must not alter permits already issued. What breaks in what you built, and how would you fix it?*

In the current implementation, rate configuration is loaded at startup into a single active version in memory. If a new government order updates rates mid-year, permits that have already been created retain their computed monetary values because fees are immutably persisted in `rcp_application` at creation time. However, any existing application returned via `SEND_BACK` and subsequently edited via `EDIT` would recompute fees against the newly active rate rather than the rate effective on its original application date. Furthermore, stateless recalculations or audit verifications would lack temporal context. To fix this, we would introduce effective date ranges (`validFrom`, `validTo`) and a rate version identifier (e.g., `rateVersion: "UK-2026.02"`) in the rate configuration schema. The `CalculationService` would accept an `asOfDate` (defaulting to application date), look up the active rate version for that timestamp, and store the applied `rateVersion` on the `rcp_application` record so historical audits and edit recalculations evaluate the exact rate snapshot active when the file was initiated.

### 2. Concurrency
*Two officers open the same application and act within the same second. What happens in your implementation, and what should happen?*

In what we built, both officers fetch the application in state `PENDING_APPROVAL`. The first officer's request (e.g. `APPROVE`) executes within a database transaction, transitions the state to `APPROVED`, records an action log, and commits. When the second officer's concurrent request (e.g. `REJECT`) commits in the same second, `WorkflowService.validateAndExecuteTransition` re-reads the application inside its transaction, finds that the current database state is now `APPROVED`, and determines that `REJECT` is not a valid transition from `APPROVED`, throwing an `InvalidTransitionException` (HTTP 400). What should happen in a large-scale system is optimistic concurrency control via a `@Version` field (`row_version`) on `rcp_application` and `If-Match` ETag headers. When the second officer submits stale state, the system immediately returns an actionable `HTTP 409 Conflict` stating *"This application was already approved by Officer Suresh at 17:15. Please refresh your queue."*, preventing accidental overwrites or duplicate actions gracefully.

### 3. The Decision You Are Least Happy With
*What did the time box force, what would you do with two more days, and what is the risk of changing it once the service is live?*

The decision I am least happy with is storing the workflow transition configuration exclusively in a static JSON file (`workflow-config.json`) rather than supporting dynamic, tenant-customizable workflow graphs in the database. The time box forced us to prioritize contract adherence, bulletproof multi-tenant data isolation, and pixel-perfect fee calculations over dynamic workflow definition tooling. With two more days, I would build a tenant-aware database workflow engine with support for parallel approvals (e.g. Traffic Police NOC alongside Municipal Engineer verification), configurable SLA escalation timers, and webhooks for citizen SMS/WhatsApp status updates. The risk of changing this once the service is live is workflow state schema drift: live applications sitting in legacy intermediate states could become orphaned or unable to transition if the workflow state graph or action naming is altered without a rigorous state migration and backward-compatibility mapping strategy.

---

Spec revision: 3.1-KESTREL
# Road-Cutting-Permission
# AssignmentShilendra
