# Warranty Tracker API

[![CI - Build & Security Validation](https://github.com/649000/warranty-tracker-api/actions/workflows/ci.yml/badge.svg)](https://github.com/649000/warranty-tracker-api/actions/workflows/ci.yml)
[![CodeQL SAST](https://github.com/649000/warranty-tracker-api/actions/workflows/codeql.yml/badge.svg)](https://github.com/649000/warranty-tracker-api/actions/workflows/codeql.yml)
[![DAST - OWASP ZAP](https://github.com/649000/warranty-tracker-api/actions/workflows/dast.yml/badge.svg)](https://github.com/649000/warranty-tracker-api/actions/workflows/dast.yml)
[![CD - Deploy to AWS](https://github.com/649000/warranty-tracker-api/actions/workflows/cd.yml/badge.svg)](https://github.com/649000/warranty-tracker-api/actions/workflows/cd.yml)

A Quarkus REST API for managing product warranties, claims, and receipts. Migrated from Spring Boot to Quarkus for GraalVM native compilation and AWS Lambda deployment.

## Why Serverless?

This is a personal project with minimal traffic. Lambda's pay-per-request model means zero cost when idle. Also a good opportunity to learn serverless architecture.

## Tech Stack

- **Quarkus** - Cloud-native Java framework with GraalVM support
- **AWS Lambda** - Serverless compute (ARM64 Graviton2)
- **API Gateway** - HTTP API for routing
- **Supabase PostgreSQL** - Database with built-in connection pooling (critical for Lambda)
- **Firebase** - JWT authentication
- **S3** - Receipt image storage
- **Textract** - OCR for receipt data extraction
- **CDK** - Infrastructure as code

## Key Features

- Firebase JWT authentication with custom security augmentor
- Presigned S3 URLs for direct client uploads
- Textract OCR for receipt parsing
- JSON:API error responses
- Health checks at `/q/health`

## Architecture Decisions

**Supabase over RDS** - Built-in PgBouncer connection pooler handles Lambda's connection churn. No need for RDS Proxy.

**Presigned URLs** - Clients upload directly to S3. Lambda only generates URLs, doesn't proxy files. Reduces execution time and cost.

**Security Augmentor** - Loads user entity once per request and caches in `SecurityIdentity`. Eliminates redundant database queries.

**Native Compilation** - GraalVM compiles to ARM64. Cold starts under 1 second vs 5-10 seconds for JVM. 50-70% memory reduction.

## Local Development

```bash
./mvnw quarkus:dev
```

Endpoints:
- API: http://localhost:8080/api
- Dev UI: http://localhost:8080/q/dev
- Swagger: http://localhost:8080/api/q/swagger-ui
- Health: http://localhost:8080/q/health

Environment variables:
```bash
export SUPABASE_DB_URL="jdbc:postgresql://..."
export SUPABASE_DB_USER="..."
export SUPABASE_DB_PASSWORD="..."
```

## Build & Deploy

**Development (JVM):**
```bash
./mvnw clean package
cd cdk
cdk deploy --all
```

**Production (Native):**
```bash
./mvnw clean package -Dnative -Dquarkus.native.container-build=true
cd cdk
cdk deploy -c env=prd --all
```

See `cdk/README.md` for infrastructure details.

## Project Structure

- `api/` - Quarkus application
- `cdk/` - AWS CDK infrastructure (Java)

## CI/CD & DevSecOps Architecture

The project employs a modern DevSecOps pipeline using **GitHub Actions**, adhering to the "Shift-Left" philosophy to catch bugs, vulnerabilities, and credential leaks early.

```
Git Push / PR ──► Gitleaks (Secrets) ──► Maven Test (Java 21) ──► CodeQL (SAST) ──► CDK Synth (IaC)
                                                                                          │
                                                              Merge to main               ▼
Prod Native Lambda ◄── Manual Gate ◄── DAST (OWASP ZAP) ◄── CDK Deploy Dev (JVM) ◄───────┘
```

### Pipeline Overview

1. **Continuous Integration (`ci.yml`)**:
   - **Gitleaks**: Scans commit history for accidentally leaked passwords or tokens.
   - **Automated Tests**: Compiles on Java 21, executes 232 unit tests, and uploads surefire reports.
   - **Infrastructure Validation**: Synthesizes AWS CDK CloudFormation stacks for both `dev` and `prd`.
   - **Dependency Review (SCA)**: Blocks PRs introducing dependencies with high/critical CVEs.

2. **Static Application Security Testing (`codeql.yml`)**:
   - Analyzes source code AST and dataflow graphs for OWASP Top 10 vulnerabilities (SQLi, path traversal, injection).
   - Generates SARIF reports integrated into GitHub's **Security > Code Scanning** alerts.

3. **Dynamic Application Security Testing (`dast.yml`)**:
   - Uses **OWASP ZAP API Scan** with the Quarkus OpenAPI schema (`/api/q/openapi`).
   - Automatically probes endpoints for information leaks (stack traces), CORS issues, and missing security headers.
   - Can run against an ephemeral in-runner PostgreSQL/Quarkus instance or against deployed staging URLs via `workflow_dispatch`.

4. **Continuous Delivery (`cd.yml`)**:
   - Uses **AWS IAM OpenID Connect (OIDC)** for temporary, keyless authentication (no static AWS secrets).
   - Deploys JVM Lambda to `dev` on push to `main`.
   - Deploys GraalVM Native ARM64 Lambda to `production` behind GitHub Environment protection gates.

5. **Automated Maintenance (`dependabot.yml`)**:
   - Weekly scans for Maven library updates and GitHub Actions versions.

---

### Setting up AWS OIDC for Keyless Deployments

Instead of storing long-lived `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` in GitHub Secrets, configure an AWS IAM OIDC Role:

1. **Add GitHub as an Identity Provider in AWS IAM**:
   - Provider Type: `OpenID Connect`
   - Provider URL: `https://token.actions.githubusercontent.com`
   - Audience: `sts.amazonaws.com`

2. **Create an IAM Role for GitHub Actions**:
   - Trust Policy (replace `<GITHUB_OWNER>` and `<REPO>` with your repo details):
     ```json
     {
       "Version": "2012-10-17",
       "Statement": [
         {
           "Effect": "Allow",
           "Principal": {
             "Federated": "arn:aws:iam::<ACCOUNT_ID>:oidc-provider/token.actions.githubusercontent.com"
           },
           "Action": "sts:AssumeRoleWithWebIdentity",
           "Condition": {
             "StringEquals": {
               "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
             },
             "StringLike": {
               "token.actions.githubusercontent.com:sub": "repo:<GITHUB_OWNER>/<REPO>:*"
             }
           }
         }
       ]
     }
     ```
   - Attach policies allowing CDK deployment (`AdministratorAccess` or scoped CDK execution policies).

3. **Configure GitHub Repository Secrets**:
   Go to **Settings > Secrets and variables > Actions**:
   - `AWS_ROLE_ARN`: `arn:aws:iam::<ACCOUNT_ID>:role/<ROLE_NAME>`
   - `SUPABASE_DB_URL`: `jdbc:postgresql://...`
   - `SUPABASE_DB_USER`: `...`
   - `SUPABASE_DB_PASSWORD`: `...`

---

## Future Work

- **X-Ray tracing** - End-to-end request tracing across Lambda, API Gateway, and Supabase to identify performance bottlenecks.
- **Structured logging** - JSON logs with correlation IDs for better CloudWatch Logs Insights queries and request tracking.
- **ElastiCache** - Redis cache for product catalog and user profiles to reduce database load.
- **Flyway migrations** - Version-controlled database schema changes instead of Hibernate's auto-update.
- **SQS for Textract** - Move OCR processing to async queue to improve API response times and handle retries.
- **SNS notifications** - Publish warranty lifecycle events (expiring soon, expired) for real-time alerts.
- **Secrets Manager** - Centralized credential management with automatic rotation instead of environment variables.
