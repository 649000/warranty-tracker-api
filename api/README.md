# Warranty Tracker API

A Quarkus-based REST API for managing product warranties, claims, and user products. Originally built with Spring Boot, this project was migrated to Quarkus to leverage cloud-native features and GraalVM native compilation for optimal performance on AWS Lambda.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technical Implementation](#technical-implementation)
- [Getting Started](#getting-started)
- [Building and Deployment](#building-and-deployment)
- [Development](#development)
- [Future Enhancements](#future-enhancements)

## Overview

### Why Quarkus?

This project was migrated from Spring Boot to Quarkus for several key reasons:

- **Cloud-Native Design** - Built from the ground up for containerized and serverless environments
- **GraalVM Native Images** - Compile to native executables with sub-second startup times and minimal memory footprint
- **Lambda Optimization** - Native images drastically reduce cold start times on AWS Lambda
- **Developer Experience** - Live reload and dev mode make local development fast and efficient

### Key Features

- Firebase JWT authentication with custom security context enrichment
- AWS S3 integration for receipt storage with presigned URLs
- AWS Textract OCR for automated receipt data extraction
- JSON:API compliant error responses
- Configurable pagination with performance safeguards
- Health checks for liveness and readiness probes

## Architecture

### Serverless-First Design

This application is built specifically for AWS Lambda, not adapted from a traditional server-based architecture. Key architectural decisions:

**Why Lambda over ECS/EKS:**
- This is a personal project with minimal traffic - no need to pay for idle servers
- Lambda's pay-per-request model means zero cost when not in use
- Great opportunity to learn serverless architecture and deployment patterns
- Sub-second cold starts with GraalVM native images make Lambda viable for user-facing APIs
- Automatic scaling without managing cluster capacity or auto-scaling groups

**Why HTTP API Gateway over REST API:**
- Lower cost (up to 70% cheaper than REST API)
- Lower latency
- Simpler configuration for proxy integration
- No need for REST API features like API keys or usage plans (handled by Firebase auth)

### Database Architecture

**PostgreSQL via Supabase instead of DynamoDB:**
- Complex relational queries (warranties linked to products, users, companies, claims)
- ACID transactions for warranty claim processing
- Existing SQL expertise and tooling
- Supabase provides built-in connection pooling (PgBouncer) which is critical for serverless

**Why Supabase over AWS RDS:**
- Built-in connection pooler designed specifically for serverless workloads
- No need to manage separate RDS Proxy or configure connection pooling
- Supabase handles the Lambda connection churn automatically
- Lower operational overhead and simpler configuration
- Free tier suitable for personal projects

**Connection Management in Serverless:**
- Lambda functions are stateless and short-lived
- Traditional connection pools don't work well (connections die between invocations)
- Supabase's PgBouncer sits between Lambda and PostgreSQL, pooling connections efficiently
- Hibernate's `validate` strategy in production prevents schema drift
- Each Lambda invocation gets a connection from the pool, returns it after use

### Storage Strategy

**S3 with Presigned URLs instead of direct API uploads:**
- Offloads bandwidth from Lambda (no need to proxy large files)
- Client uploads directly to S3, reducing Lambda execution time and cost
- Presigned URLs provide time-limited, secure upload capability without exposing credentials
- Lambda only generates URLs and stores metadata, not the actual file transfer

**Textract Integration:**
- Receipt OCR is handled synchronously for now (acceptable for small receipts)
- Textract processes images directly from S3 (no need to download to Lambda)
- Extracted data (merchant, amount, date) is parsed and stored in PostgreSQL

### Authentication Flow

**Firebase JWT + Custom Security Augmentor:**
- Firebase handles user authentication (no need to build auth infrastructure)
- JWT tokens verified using Google's public keys (fetched from JWK endpoint)
- Custom `UserSecurityAugmentor` loads the User entity once per request and caches it in `SecurityIdentity`
- Eliminates N+1 query problem where every endpoint would otherwise query the users table

**Why this matters in Lambda:**
- Each Lambda invocation is a fresh request context
- Without the augmentor, every protected endpoint would hit the database twice (once for auth, once for user data)
- The augmentor pattern reduces database round-trips by 50% for authenticated requests

### Domain Model

The system manages the following entities:

- **User** - Application users authenticated via Firebase
- **Product** - Product catalog with brand and model information
- **UserProduct** - User-owned products with purchase details and serial numbers
- **Warranty** - Warranty coverage periods linked to user products
- **Claim** - Warranty claims with status tracking
- **Company** - Warranty provider companies
- **Receipt** - Purchase receipt storage with S3 references

### Deployment Architecture

**Development Environment:**
- JVM-based Lambda for faster build times (no native compilation wait)
- Easier debugging with standard Java tooling
- Same codebase as production, just different runtime

**Production Environment:**
- GraalVM native compilation to ARM64 for Graviton2 processors
- 50-70% memory reduction compared to JVM
- Cold starts under 1 second vs 5-10 seconds for JVM
- Lower Lambda costs due to reduced execution time and memory

**Infrastructure as Code:**
- AWS CDK (Java) for type-safe infrastructure definitions
- Separate stacks for API Gateway and Lambda (independent deployment)
- Environment-specific configuration (dev/prod) via CDK context

### Trade-offs and Constraints

**What we gave up for serverless:**
- No WebSockets (HTTP API Gateway doesn't support them)
- No long-running background jobs (15-second Lambda timeout)
- Cold start latency for infrequent endpoints (mitigated by native compilation)
- Connection pooling complexity (solved by Supabase's built-in pooler)

**What we gained:**
- Zero cost when not in use (perfect for personal projects)
- Zero infrastructure management
- Automatic scaling from 0 to thousands of requests
- Pay only for actual usage
- Built-in high availability across multiple AZs
- Hands-on experience with serverless architecture

## Technical Implementation

### Authentication & Authorization

API endpoints require Firebase authentication. The application verifies JWT tokens issued by Firebase:

- Public keys are fetched from Google's JWK endpoint
- The `@Authenticated` annotation protects resources
- Current user is extracted from JWT via `SecurityIdentity`
- `UserSecurityAugmentor` enriches the security context with user entity data

The custom security augmentor eliminates redundant database queries by loading the user once during authentication and making it available throughout the request lifecycle via `SecurityIdentity.getAttribute("user")`.

### Exception Handling Strategy

A global exception mapper (`GlobalExceptionHandler`) translates domain exceptions into JSON:API compliant error responses. This provides:

- Consistent error format across all endpoints
- Unique error IDs for tracking and debugging
- Structured error codes for client-side handling
- Proper HTTP status code mapping

Custom exceptions include:
- `ResourceNotFoundException` - 404 responses with resource type and ID
- `ResourceConflictException` - 409 responses for duplicate resources
- `ValidationException` - 400 responses with field-level validation errors
- `UnauthorizedException` / `ForbiddenException` - 401/403 for auth failures

### AWS Service Integration

The application integrates with AWS services using the Quarkus AWS SDK extensions:

**S3 Integration** - Receipt image storage with presigned URL generation for secure client-side uploads. The service handles multipart uploads and generates time-limited download URLs.

**Textract Integration** - OCR processing for receipt images. The `TextractService` extracts structured data (merchant name, total amount, date) from uploaded receipts using AWS Textract's document analysis API.

Both services use Apache HTTP client for native compilation compatibility.

### Database Schema Management

The application uses Hibernate ORM with a strict validation strategy in production (`validate`). Schema changes are managed through:

- Development: `update` strategy for rapid iteration
- Production: `validate` strategy to prevent accidental schema modifications
- All entities use the `warranty_tracker` schema for namespace isolation

### Pagination and Query Optimization

Repository layer implements configurable pagination with:

- Default page size: 20 items
- Maximum page size: 20 items (prevents excessive memory usage)
- Maximum page number: 500 (prevents deep pagination performance issues)

Search queries use LIKE patterns with proper indexing for efficient filtering.

### Health Checks

The application includes SmallRye Health checks accessible at `/q/health`:

- `/q/health/live` - Liveness probe
- `/q/health/ready` - Readiness probe (includes database connectivity check)

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Supabase PostgreSQL database
- AWS account (for deployment)
- Firebase project (for authentication)

### Environment Variables

Configure your database connection:

```bash
export SUPABASE_DB_URL="jdbc:postgresql://your-host:5432/postgres"
export SUPABASE_DB_USER="your_username"
export SUPABASE_DB_PASSWORD="your_password"
```

### Running Locally

Start the application in development mode with live reload:

```bash
./mvnw quarkus:dev
```

Available endpoints:
- API: http://localhost:8080/api
- Dev UI: http://localhost:8080/q/dev/
- Swagger UI: http://localhost:8080/api/q/swagger-ui
- Health: http://localhost:8080/q/health

## Building and Deployment

This project uses AWS CDK for infrastructure provisioning and deployment. The build process differs between development and production environments.

### Development Build (JVM)

Development uses standard JVM runtime for faster iteration:

```bash
./mvnw clean package
```

This creates a `function.zip` in `target/` containing the Quarkus application ready for Lambda deployment.

### Production Build (Native)

Production uses GraalVM native compilation for optimal performance:

```bash
./mvnw clean package -Dnative -Dquarkus.native.container-build=true
```

The native build:
- Compiles to ARM64 architecture for AWS Graviton2 processors
- Produces a standalone executable with no JVM dependency
- Reduces cold start times from seconds to milliseconds
- Cuts memory usage by 50-70% compared to JVM

### Deploying to AWS

Deploy using CDK from the `cdk/` directory:

```bash
# Development
cd cdk
cdk deploy --all

# Production
cd cdk
cdk deploy -c env=prd --all
```

See `cdk/README.md` for detailed deployment instructions and environment configuration.

## Development

### Dev UI

The Dev UI at http://localhost:8080/q/dev/ provides useful tools for:
- Viewing configuration
- Testing endpoints
- Managing database schema
- Monitoring application metrics

### Configuration Profiles

The application supports multiple profiles:

- **dev** - Development configuration with Swagger UI enabled
- **prod** - Production configuration with Swagger UI disabled

Profile-specific properties are in `application-dev.properties` and `application-prod.properties`.

## Future Enhancements

The following improvements are planned to enhance scalability, observability, and operational excellence:

### Observability & Monitoring

**Distributed Tracing** - Integrate AWS X-Ray for end-to-end request tracing across Lambda, API Gateway, Supabase, and external services. This would provide visibility into performance bottlenecks and help identify optimization opportunities.

**Structured Logging** - Implement JSON-formatted logs with correlation IDs to enable better log aggregation and analysis in CloudWatch Logs Insights. Each request would carry a unique trace ID throughout its lifecycle.

**Custom Metrics** - Add business metrics (warranties created, claims processed, receipt uploads) to CloudWatch for better operational insights and alerting on business KPIs.

### Performance Optimization

**Caching Layer** - Introduce ElastiCache (Redis) for frequently accessed data like product catalogs and user profiles. Implement cache-aside pattern with TTL-based invalidation to reduce database load and improve response times.

### Database Management

**Schema Migrations** - Replace Hibernate's schema validation with Flyway or Liquibase for version-controlled database migrations. This enables safer deployments with rollback capabilities and audit trails of schema changes.

**Read Replicas** - Leverage Supabase's read replica capabilities to distribute query load, improving performance for read-heavy operations like product searches.

### Event-Driven Architecture

**Asynchronous Processing** - Move long-running operations (Textract OCR, warranty expiration notifications) to SQS queues with dedicated Lambda consumers. This improves API response times and enables better retry logic.

**Event Notifications** - Implement SNS topics for warranty lifecycle events (expiring soon, expired, claim status changes) to enable real-time notifications and integration with external systems.

### Security Enhancements

**Secrets Management** - Migrate from environment variables to AWS Secrets Manager for database credentials and API keys. Enable automatic rotation and centralized secret management.

**API Rate Limiting** - Implement per-user rate limiting using API Gateway throttling or custom token bucket implementation to prevent abuse and ensure fair resource allocation.

**Request Signing** - Add request signature validation for S3 operations to prevent unauthorized access and ensure request integrity.

### API Evolution

**Versioning Strategy** - Introduce `/api/v1/` URL versioning to support backward compatibility as the API evolves. This enables gradual migration for clients and prevents breaking changes.

**GraphQL Gateway** - Consider adding a GraphQL layer for complex queries and reducing over-fetching, particularly for mobile clients with bandwidth constraints.

### Testing & Quality

**Integration Testing** - Add Testcontainers-based integration tests with PostgreSQL to validate database interactions and query performance.

**Contract Testing** - Implement consumer-driven contract tests to ensure API compatibility across versions and prevent breaking changes.

### CI/CD Pipeline

**Automated Deployment** - Implement GitHub Actions workflow for:
- Automated testing on pull requests
- Native compilation and deployment to staging
- Production deployment with manual approval gates
- Security scanning with Snyk or OWASP dependency check

### Cost Optimization

**Lambda Optimization** - Document cost analysis comparing JVM vs native runtimes, including cold start frequency and execution duration metrics.

**S3 Lifecycle Policies** - Implement intelligent tiering and lifecycle policies to automatically move old receipts to cheaper storage classes (S3-IA, Glacier).
