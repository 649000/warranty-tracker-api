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

- X-Ray tracing
- Structured logging with correlation IDs
- ElastiCache for product catalog
- Flyway migrations
- SQS for async Textract processing
- SNS for warranty expiration notifications
- Secrets Manager for credentials
- GitHub Actions CI/CD
