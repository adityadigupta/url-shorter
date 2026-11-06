# Agentic Software Engineering System – URL Shortener

## 1. Project Overview

The Agentic Software Engineering System is a Java Spring Boot prototype that demonstrates how multiple specialized engineering agents can collaborate to process software engineering requirements through a centralized orchestration engine.

The project uses a URL Shortener application as its demonstration use case.

The system accepts a software engineering requirement and processes it through multiple stages:

1. Requirements Analysis
2. Architecture Design
3. Code Generation
4. Testing
5. Code Review
6. Documentation

The orchestration engine manages task dependencies, execution order, workflow state, human approval, retries, failure handling, and audit events.

**Current implementation note:** The agents are scripted prototype components that produce predefined engineering outputs. They do not currently use an LLM to generate actual application code, execute generated tests, or perform automated source-code reviews.

---

## 2. Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 4.1.2 | Application framework |
| Maven | Build and dependency management |
| Spring Data JPA | Database access |
| H2 Database | In-memory database |
| JUnit 5 | Unit testing |
| Mockito | Mocking and unit testing |
| IntelliJ IDEA | Development environment |

---

## 3. Project Architecture

The application consists of two major components:

### A. URL Shortener Application

The URL Shortener provides the demonstration business functionality:

- Create short URLs.
- Redirect users to original URLs.
- Track URL clicks.
- Retrieve click analytics.
- Handle expired URLs.
- Handle inactive URLs.

### B. Agentic Engineering Orchestration System

The orchestration system coordinates specialized engineering agents to process software requirements.

It contains:

- Engineering agents
- Agent registry
- Workflow planner
- Task dependency graph
- Orchestration engine
- Execution context
- Human approval service
- Audit logging service
- REST controllers

### Logical Package Structure

```text
com.assignment.urlshortener
│
├── controller
├── service
├── repository
├── entity
│
├── orchestrator
│   │
│   ├── agent
│   │   ├── EngineeringAgent
│   │   ├── RequirementsAgent
│   │   ├── ArchitectureAgent
│   │   ├── CodingAgent
│   │   ├── TestingAgent
│   │   ├── CodeReviewAgent
│   │   └── DocumentationAgent
│   │
│   ├── model
│   │   ├── EngineeringTask
│   │   ├── TaskType
│   │   ├── TaskStatus
│   │   ├── AgentResult
│   │   └── WorkflowStatus
│   │
│   ├── engine
│   │   ├── AgentRegistry
│   │   ├── TaskDependencyGraph
│   │   ├── WorkflowPlanner
│   │   └── OrchestrationEngine
│   │
│   ├── context
│   │   └── ExecutionContext
│   │
│   ├── approval
│   │   ├── ApprovalRequest
│   │   ├── ApprovalService
│   │   ├── ApprovalStatus
│   │   └── ApprovalController
│   │
│   ├── audit
│   │   ├── AuditEvent
│   │   └── AuditService
│   │
│   └── controller
│       └── OrchestrationController
│
└── UrlShortenerApplication.java
```

*The above is a logical package overview. Refer to the actual source tree for exact class locations.*

---

## 4. Engineering Agents

The system uses six specialized engineering agents.

| Agent | Responsibility | Current Implementation |
|---|---|---|
| RequirementsAgent | Analyzes the original requirement and identifies functional requirements. | Implemented |
| ArchitectureAgent | Produces an architecture design based on requirements. | Implemented |
| CodingAgent | Produces an implementation plan based on architecture. | Planning only |
| TestingAgent | Produces a testing plan based on implementation outputs. | Planning only |
| CodeReviewAgent | Produces a code review checklist. | Checklist only |
| DocumentationAgent | Consolidates engineering outputs into documentation. | Implemented |

All agents implement the common `EngineeringAgent` interface.

The interface provides:

- `getSupportedTaskType()` – identifies the task type supported by the agent.
- `execute(EngineeringTask task, ExecutionContext context)` – executes the agent's assigned task using the workflow context.

### Agent Registry

The `AgentRegistry`:

- Maintains the mapping between task types and agents.
- Registers available engineering agents.
- Retrieves the correct agent for a task.
- Prevents duplicate agent registrations for the same task type.

---

## 5. Workflow Planning and Task Dependencies

The `WorkflowPlanner` creates a workflow containing six engineering tasks.

### Workflow Dependency Diagram

```text
       Requirements Analysis
                 |
                 v
          Architecture Design
                 |
                 v
           Code Generation
                 |
                 v
               Testing
                 |
                 v
             Code Review
                 |
                 v
            Documentation
```

The Documentation task also depends on the successful completion of all preceding engineering stages.

### Task Dependency Management

The `TaskDependencyGraph`:

- Maintains engineering tasks.
- Stores dependencies between tasks.
- Identifies tasks ready for execution.
- Prevents execution before dependencies are completed.
- Marks tasks as BLOCKED when required dependencies fail.
- Blocks all unfinished tasks when the workflow enters safe-stop.

Each workflow receives its own dependency graph.

### Task Statuses

The `TaskStatus` enum includes:

- PENDING
- READY
- RUNNING
- WAITING_FOR_APPROVAL
- COMPLETED
- FAILED
- BLOCKED
- SKIPPED
- ROLLED_BACK

### Workflow Statuses

The `WorkflowStatus` enum includes:

- RUNNING
- WAITING_FOR_APPROVAL
- COMPLETED
- FAILED
- SAFE_STOPPED

---

## 6. Centralized Orchestration Engine

The `OrchestrationEngine` is responsible for coordinating workflow execution.

Its responsibilities include:

1. Retrieving ready tasks from the dependency graph.
2. Identifying the appropriate agent through the AgentRegistry.
3. Executing the agent.
4. Updating task status.
5. Recording execution events.
6. Handling retries.
7. Handling permanent failures.
8. Enforcing the human approval gate.
9. Stopping execution when the workflow enters SAFE_STOPPED.
10. Determining the final workflow status.

The current implementation processes ready tasks sequentially.

---

## 7. Execution Context and Artifacts

The `ExecutionContext` maintains information associated with an individual workflow run.

It contains:

- Unique run ID.
- Original engineering requirement.
- Workflow status.
- Generated artifacts.

Agents store their outputs in the execution context so subsequent agents can use the results of previous stages.

Examples of artifacts include:

- requirements
- architecture
- code
- testing
- code-review
- documentation

This provides in-memory context sharing across engineering stages.

---

## 8. Human-in-the-Loop Approval

A human approval gate is implemented before the Code Generation stage.

### Approval Flow

1. Requirements Analysis completes.
2. Architecture Design completes.
3. The workflow reaches Code Generation.
4. The orchestration engine checks whether approval has been granted.
5. If approval is missing, an ApprovalRequest is created.
6. The Code Generation task becomes WAITING_FOR_APPROVAL.
7. The workflow becomes WAITING_FOR_APPROVAL.
8. An approval audit event is recorded.
9. Workflow execution pauses.

### When Approval Is Granted

- The approval status becomes APPROVED.
- The code-generation-approved artifact is recorded.
- The Code Generation task is reset for execution.
- The workflow resumes.
- Remaining engineering stages execute.

### When Approval Is Rejected

- The approval status becomes REJECTED.
- Code Generation is marked FAILED.
- Downstream tasks are blocked.
- The workflow becomes FAILED.

This provides a basic human-in-the-loop control mechanism for a high-impact engineering stage.

---

## 9. Retry and Failure Handling

The orchestration engine implements bounded retry handling.

The default configuration allows two retries per task.

This means each task can have three total execution attempts:

| Attempt | Description |
|---|---|
| Attempt 1 | Initial execution |
| Attempt 2 | First retry |
| Attempt 3 | Second retry |

### Retry Behavior

When an agent returns an unsuccessful result or throws an exception:

1. The engine checks whether the task can be retried.
2. If retries remain, the retry count is incremented.
3. The task is returned to PENDING.
4. A TASK_RETRY audit event is recorded.
5. The task is attempted again.

### Safe-Stop Behavior

When all retry attempts are exhausted:

1. The task is marked FAILED.
2. The workflow status becomes SAFE_STOPPED.
3. All unfinished tasks are marked BLOCKED.
4. A TASK_FAILED event is recorded.
5. The engine stops executing further tasks.

Both dependent and independent unfinished tasks are covered by the safe-stop implementation.

---

## 10. Audit Logging

The system provides basic audit logging through `AuditService`.

Each audit event includes:

- Run ID
- Task ID, when applicable
- Task name
- Event type
- Event details
- Timestamp

### Implemented Audit Events

| Event | Description |
|---|---|
| TASK_STARTED | Task execution begins. |
| TASK_COMPLETED | Task execution completes successfully. |
| TASK_RETRY | Task is scheduled for another attempt. |
| TASK_FAILED | Task permanently fails. |
| APPROVAL_REQUESTED | Human approval is requested. |
| APPROVAL_APPROVED | Approval is granted. |
| APPROVAL_REJECTED | Approval is rejected. |
| WORKFLOW_COMPLETED | Workflow completes successfully. |
| WORKFLOW_FAILED | Workflow fails. |

**Limitation:** Audit events are currently stored in memory and are lost when the application restarts. A distinct WORKFLOW_SAFE_STOPPED audit event has not yet been verified in the current implementation.

---

## 11. REST API Endpoints

### 11.1 Orchestration APIs

Base path:

`/api/v1/orchestrations`

| HTTP Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/orchestrations` | Starts an engineering workflow. |
| GET | `/api/v1/orchestrations/{runId}` | Retrieves workflow status, artifacts, and task statuses. |
| GET | `/api/v1/orchestrations/{runId}/audit` | Retrieves audit events for a workflow. |

### 11.2 Approval APIs

Base path:

`/api/v1/approvals`

| HTTP Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/approvals/{approvalId}` | Retrieves an approval request. |
| GET | `/api/v1/approvals/run/{runId}` | Retrieves approval requests for a workflow. |
| POST | `/api/v1/approvals/{approvalId}/approve` | Approves a pending request and resumes execution. |
| POST | `/api/v1/approvals/{approvalId}/reject` | Rejects a pending request. |

### 11.3 URL Shortener APIs

The URL Shortener application exposes REST endpoints for:

- URL creation.
- Short URL redirection.
- Click tracking.
- Click analytics.

Refer to the URL controller classes for the exact endpoint paths, request formats, and response formats.

---

## 12. Running the Application

### Prerequisites

Install:

- Java 21
- Maven
- IntelliJ IDEA or another Java IDE

### Clone the Repository

```bash
git clone <repository-url>
cd url-shortener
```

### Build the Application

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

Alternatively, run `UrlShortenerApplication.java` from IntelliJ IDEA.

### Run Tests

```bash
mvn test
```

---

## 13. Testing and Verification

The project uses:

- JUnit 5
- Mockito
- Spring Boot Test

### Current Verified Test Results

The latest Maven test execution completed successfully.

```text
Tests run: 13
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

### Test Coverage

| Test Area | Verified Functionality |
|---|---|
| URL Controller | Controller behavior |
| URL Service | URL business logic |
| URL Click Service | Click tracking behavior |
| Task Dependency Graph | Dependency failure and blocking |
| Orchestration Engine | Retry behavior and permanent failure |
| Exception Handling | Retry after agent exceptions |
| Safe-Stop | Workflow stops after retry exhaustion |
| Task Blocking | Dependent and independent tasks become BLOCKED |
| Spring Application | Application context startup |

The tests verify the behaviors listed above. They do not establish that all future capabilities described in this README are implemented.

---

## 14. Demonstration Scenarios

The current prototype supports demonstration of the following execution paths.

### Scenario 1: Successful Workflow with Human Approval

**Requirement:**

Build a URL shortener with analytics and reliable redirects.

**Expected execution:**

1. Requirements Analysis completes.
2. Architecture Design completes.
3. Code Generation requests human approval.
4. The workflow pauses.
5. A reviewer approves the request.
6. Code Generation executes.
7. Testing executes.
8. Code Review executes.
9. Documentation executes.
10. Workflow status becomes COMPLETED.

**Expected result:** All six tasks complete successfully.

### Scenario 2: Human Approval Rejected

**Expected execution:**

1. Requirements Analysis completes.
2. Architecture Design completes.
3. Code Generation requests approval.
4. The reviewer rejects the request.
5. Code Generation becomes FAILED.
6. Testing, Code Review, and Documentation become BLOCKED.
7. Workflow status becomes FAILED.

**Expected result:** No downstream engineering tasks execute after rejection.

### Scenario 3: Permanent Agent Failure and Safe-Stop

**Expected execution:**

1. An engineering agent starts executing a task.
2. The agent returns an unsuccessful result.
3. The orchestration engine retries the task.
4. The task fails after exhausting all retries.
5. The task becomes FAILED.
6. Workflow status becomes SAFE_STOPPED.
7. Dependent unfinished tasks become BLOCKED.
8. Independent unfinished tasks also become BLOCKED.
9. The engine stops further execution.

**Expected result:** The workflow safely halts instead of continuing after a permanent failure.

### Additional Scenario Coverage

The assignment also calls for greenfield, brownfield, and ambiguous-requirement scenarios.

These have not yet been fully implemented and demonstrated as separate end-to-end scenarios.

---

# 15. Implementation Status – Completed vs. Missing

This section summarizes the actual implementation status of the prototype against the assignment requirements.

## 15.1 Completed and Implemented Features

| # | Feature | Status | Details |
|---|---|---|---|
| 1 | URL Shortener Application | COMPLETED | URL creation, redirects, click tracking, analytics, expiry and inactive URL handling. |
| 2 | Requirements Analysis Agent | COMPLETED | Produces requirements analysis output. |
| 3 | Architecture Design Agent | COMPLETED | Produces architecture output using requirements context. |
| 4 | Agent Interface | COMPLETED | Common interface for engineering agents. |
| 5 | Agent Registry | COMPLETED | Maps task types to specialized agents. |
| 6 | Workflow Planner | COMPLETED | Creates the six-stage engineering workflow. |
| 7 | Task Dependency Graph | COMPLETED | Maintains dependencies and identifies ready tasks. |
| 8 | Orchestration Engine | COMPLETED | Coordinates sequential task execution. |
| 9 | Execution Context | IMPLEMENTED | Shares workflow requirements and artifacts in memory. |
| 10 | Human Approval Gate | COMPLETED | Pauses before code generation for reviewer approval. |
| 11 | Approval and Rejection APIs | COMPLETED | Supports approval and rejection requests. |
| 12 | Workflow Resume | COMPLETED | Resumes execution after approval. |
| 13 | Workflow Rejection Handling | COMPLETED | Fails the approved stage and blocks downstream work. |
| 14 | Bounded Retry Mechanism | COMPLETED | Supports two retries after the initial attempt. |
| 15 | Exception Handling | COMPLETED | Handles agent exceptions and applies retry logic. |
| 16 | Safe-Stop Mechanism | COMPLETED | Stops execution after permanent failure. |
| 17 | Task Blocking | COMPLETED | Blocks dependent and independent unfinished tasks. |
| 18 | Basic Audit Logging | IMPLEMENTED | Records task and approval lifecycle events in memory. |
| 19 | Orchestration REST APIs | IMPLEMENTED | Starts workflows and retrieves workflow information. |
| 20 | Approval REST APIs | IMPLEMENTED | Retrieves and processes approval requests. |
| 21 | Unit and Integration Tests | IMPLEMENTED | Current test suite has 13 passing tests. |
| 22 | Maven Build | VERIFIED | Latest `mvn test` execution reports BUILD SUCCESS. |

## 15.2 Partially Implemented Features

| # | Feature | Current Status | Remaining Work |
|---|---|---|---|
| 1 | Code Generation Agent | PARTIAL | Generate actual source code instead of an implementation plan. |
| 2 | Testing Agent | PARTIAL | Generate and execute tests rather than only producing a test plan. |
| 3 | Code Review Agent | PARTIAL | Inspect actual source code rather than only producing a checklist. |
| 4 | Documentation Agent | PARTIAL | Generate richer documentation from validated engineering outputs. |
| 5 | Cross-Stage Context | PARTIAL | Persist context and decision lineage beyond an in-memory workflow run. |
| 6 | Audit Observability | PARTIAL | Add persistent audit storage and complete workflow outcome events. |
| 7 | Policy Guardrails | PARTIAL | Introduce configurable policies and enforce them around agent actions. |
| 8 | Entry and Exit Gates | PARTIAL | Add explicit validation gates before and after each engineering stage. |
| 9 | Production Observability | PARTIAL | Add structured logs, metrics, tracing, and monitoring. |
| 10 | Scenario Coverage | PARTIAL | Demonstrate greenfield, brownfield, and ambiguous requirements independently. |

## 15.3 Missing Features and Future Enhancements

| # | Feature | Status | Required Enhancement |
|---|---|---|---|
| 1 | LLM Integration | NOT IMPLEMENTED | Integrate an LLM for context-aware reasoning and engineering outputs. |
| 2 | Actual Code Generation | NOT IMPLEMENTED | Generate real Java source files from engineering requirements. |
| 3 | Generated Code Execution | NOT IMPLEMENTED | Compile and execute generated code in a controlled environment. |
| 4 | Automated Test Execution by Agent | NOT IMPLEMENTED | Allow the testing agent to generate and execute tests against generated code. |
| 5 | Automated Source Code Review | NOT IMPLEMENTED | Inspect source code for defects, quality, and security issues. |
| 6 | Parallel Task Execution | NOT IMPLEMENTED | Execute independent ready tasks concurrently and synchronize their results. |
| 7 | Persistent Workflow Storage | NOT IMPLEMENTED | Store workflow runs, tasks, statuses, and artifacts in a database. |
| 8 | Persistent Audit Storage | NOT IMPLEMENTED | Persist audit events across application restarts. |
| 9 | Rollback Mechanism | NOT IMPLEMENTED | Revert changes made by failed or rejected engineering stages. |
| 10 | Fallback Mechanism | NOT IMPLEMENTED | Switch to alternative agents or strategies when execution fails. |
| 11 | Dynamic Replanning | NOT IMPLEMENTED | Reorganize tasks and dependencies based on execution outcomes. |
| 12 | Reliability Metrics | NOT IMPLEMENTED | Calculate success rate, retry frequency, rollback frequency, MTTR, and end-to-end latency. |
| 13 | Retry Backoff | NOT IMPLEMENTED | Introduce configurable delays between retry attempts. |
| 14 | Failure Classification | NOT IMPLEMENTED | Distinguish transient failures from permanent failures. |
| 15 | Workflow Concurrency and Isolation | NOT IMPLEMENTED | Support concurrent workflow execution with stronger isolation. |
| 16 | Advanced Decision Lineage | NOT IMPLEMENTED | Store structured reasoning, decisions, and supporting evidence across stages. |
| 17 | Production Security and Guardrails | NOT IMPLEMENTED | Add authentication, authorization, secure agent execution, and configurable action policies. |

---

## 16. Assignment Requirement Coverage Summary

| Assignment Requirement | Current Coverage |
|---|---|
| Runnable prototype | IMPLEMENTED |
| URL Shortener demonstration | IMPLEMENTED |
| Multiple specialized agents | IMPLEMENTED |
| Requirement decomposition | SCRIPTED IMPLEMENTATION |
| Architecture planning | SCRIPTED IMPLEMENTATION |
| Code generation | IMPLEMENTATION PLAN ONLY |
| Testing and validation | TEST PLAN; SEPARATE MAVEN TESTS |
| Code review | CHECKLIST ONLY |
| Explicit dependency graph | IMPLEMENTED |
| Sequential execution | IMPLEMENTED |
| Parallel execution | MISSING |
| Human approval for high-impact actions | IMPLEMENTED |
| Retry handling | IMPLEMENTED |
| Safe-stop behavior | IMPLEMENTED |
| Rollback | MISSING |
| Fallback | MISSING |
| Persistent cross-stage context | PARTIAL; IN-MEMORY |
| Audit observability | BASIC IN-MEMORY IMPLEMENTATION |
| Policy guardrails | PARTIAL |
| Entry and exit gates | PARTIAL |
| Dynamic replanning | MISSING |
| Reliability metrics | MISSING |
| Greenfield scenario | NOT FULLY DEMONSTRATED |
| Brownfield scenario | NOT FULLY DEMONSTRATED |
| Ambiguous requirement scenario | NOT FULLY DEMONSTRATED |
| Architecture and setup documentation | DOCUMENTED |
| Testing approach and limitations | DOCUMENTED |

---

## 17. Known Limitations

The current project is a working orchestration prototype, not a fully autonomous AI software engineering platform.

Important limitations:

1. Agents currently use predefined scripted outputs.
2. There is no LLM integration.
3. Code Generation produces an implementation plan, not actual source code.
4. Testing and Code Review agents produce plans and checklists rather than executing autonomous validation.
5. Ready tasks are executed sequentially.
6. Workflow and approval state is stored in memory.
7. Audit records are lost when the application restarts.
8. Rollback, fallback, and dynamic replanning are not implemented.
9. Reliability metrics are not implemented.
10. Greenfield, brownfield, and ambiguous requirement scenarios still need separate end-to-end demonstrations.

These limitations are explicitly identified to distinguish implemented prototype functionality from future enhancements.

---

## 18. Future Development Roadmap

The following roadmap can be used to extend the prototype.

### Phase 1 – AI-Powered Engineering
- Integrate an LLM.
- Enable context-aware agent reasoning.
- Generate actual source code.
- Generate and execute automated tests.
- Perform automated code review.

### Phase 2 – Advanced Orchestration
- Add parallel task execution.
- Add synchronization and concurrency controls.
- Implement dynamic replanning.
- Add entry and exit validation gates.

### Phase 3 – Reliability and Recovery
- Implement rollback.
- Implement fallback agents and strategies.
- Add failure classification.
- Add retry backoff.
- Add safe recovery procedures.

### Phase 4 – Persistence and Observability
- Store workflows and artifacts in a database.
- Persist audit events.
- Implement reliability metrics.
- Add monitoring, tracing, and dashboards.

### Phase 5 – Assignment Scenario Demonstrations
- Greenfield project scenario.
- Brownfield enhancement scenario.
- Ambiguous requirement scenario.
- Document the expected and actual outcomes of each scenario.

---

## 19. Conclusion

This project establishes a foundation for an Agentic Software Engineering System using Java and Spring Boot.

The current prototype demonstrates:

- Multiple specialized engineering agents.
- Workflow planning and task decomposition.
- Dependency-aware execution.
- Centralized orchestration.
- Shared execution context.
- Human-in-the-loop approval.
- Workflow resume and rejection.
- Bounded retries.
- Exception handling.
- Safe-stop behavior.
- Blocking of unfinished tasks.
- Basic audit logging.
- REST APIs for workflow and approval management.
- Automated application and orchestration tests.

The latest verified Maven test run completed with 13 tests passing and zero failures.

The next major step toward a fully autonomous engineering platform is integrating an LLM and enabling real code generation, testing, and code review, followed by advanced orchestration, persistence, rollback, fallback, dynamic replanning, and reliability metrics.

**Project status:** Core orchestration prototype implemented and tested; advanced autonomous engineering capabilities remain future enhancements.