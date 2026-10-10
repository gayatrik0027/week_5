# Student Leave Workflow — Camunda 8 SaaS + Spring Boot

A small lab project that demonstrates deployment from code, starting a process over REST, Java job workers, and a human approval task in Tasklist.

## Requirements
- JDK 17+
- Maven 3.9+
- Camunda 8 SaaS cluster on the same minor version as the starter
- A Camunda API client with the Zeebe / Orchestration Cluster scope

## Configure credentials
Set these environment variables before running the app. Do not commit credentials to Git.

**Windows PowerShell**
```powershell
$env:CAMUNDA_CLIENT_ID="your-client-id"
$env:CAMUNDA_CLIENT_SECRET="your-client-secret"
$env:CAMUNDA_CLUSTER_ID="your-cluster-id"
$env:CAMUNDA_CLUSTER_REGION="your-region"
mvn spring-boot:run
```

**macOS / Linux**
```bash
export CAMUNDA_CLIENT_ID="your-client-id"
export CAMUNDA_CLIENT_SECRET="your-client-secret"
export CAMUNDA_CLUSTER_ID="your-cluster-id"
export CAMUNDA_CLUSTER_REGION="your-region"
mvn spring-boot:run
```

The application deploys `student-leave.bpmn` and `fa-approval.form` at startup.

## Test the REST endpoint
Send a future-dated request. Dates use `yyyy-MM-dd`.

```bash
curl -X POST http://localhost:8080/leave   -H "Content-Type: application/json"   -d '{"studentName":"Priya","regNo":"RA2311003010001","fromDate":"2026-10-20","toDate":"2026-10-21","reason":"Personal work"}'
```

The response contains a `processInstanceKey`. A valid request of up to two calendar days is auto-approved. A longer request waits for Faculty Advisor approval in Tasklist. A request with invalid or past dates is rejected.

## Workflow rules
- `validate-leave`: reads `fromDate`, `toDate`; writes `leaveDays`, `valid`, and `approved`.
- `notify-student`: reads `studentName`, `regNo`, `approved`, `remarks`; writes `leaveStatus` and logs a notification.
- Process ID: `student-leave`
- Worker types: `validate-leave`, `notify-student`
- Form ID: `fa-approval-form`

## Notes
- The notification is a log message, not a real email.
- This project is a starter lab; import the BPMN and form into Web Modeler if you want to inspect or adjust the model.
- If the cluster uses another Camunda 8.8 patch/minor version, align `camunda.version` in `pom.xml` with the version specified by your course or cluster.
