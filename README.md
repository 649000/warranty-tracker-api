# Warranty Tracker API

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

## Future Work

- **X-Ray tracing** - End-to-end request tracing across Lambda, API Gateway, and Supabase to identify performance bottlenecks.
- **Structured logging** - JSON logs with correlation IDs for better CloudWatch Logs Insights queries and request tracking.
- **ElastiCache** - Redis cache for product catalog and user profiles to reduce database load.
- **Flyway migrations** - Version-controlled database schema changes instead of Hibernate's auto-update.
- **SQS for Textract** - Move OCR processing to async queue to improve API response times and handle retries.
- **SNS notifications** - Publish warranty lifecycle events (expiring soon, expired) for real-time alerts.
- **Secrets Manager** - Centralized credential management with automatic rotation instead of environment variables.
- **GitHub Actions CI/CD** - Automated testing, native compilation, and deployment pipeline with security scanning.
