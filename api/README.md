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

### Application Layers

This application follows a layered architecture:

- **Resources** - REST endpoints that handle HTTP requests and responses
- **Services** - Business logic layer that orchestrates operations
- **Repositories** - Data access layer using Panache (Quarkus ORM) for database operations
- **Models** - JPA entities representing the domain model

### Domain Model

The system manages the following entities:

- **User** - Application users authenticated via Firebase
- **Product** - Product catalog with brand and model information
- **UserProduct** - User-owned products with purchase details and serial numbers
- **Warranty** - Warranty coverage periods linked to user products
- **Claim** - Warranty claims with status tracking
- **Company** - Warranty provider companies
- **Receipt** - Purchase receipt storage

### Cloud Infrastructure

This API is designed to run on AWS Lambda with API Gateway integration. The serverless architecture provides:

- Automatic scaling based on request volume
- Pay-per-use pricing model
- No server management overhead
- Built-in high availability

#### AWS Services

- **AWS Lambda** - Serverless compute for API execution (optimized with GraalVM native images)
- **API Gateway** - HTTP API endpoint management
- **RDS PostgreSQL** - Relational database for persistent storage (via Supabase)
- **S3** - Object storage for receipt images and documents
- **Textract** - OCR service for extracting data from receipt images
- **CDK** - Infrastructure as Code for provisioning AWS resources

The CDK stack (in the `cdk/` directory) defines the infrastructure including Lambda functions, API Gateway, and IAM roles.

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

- Java 17+
- Maven 3.8+
- PostgreSQL database
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

### Hot Reload

Quarkus provides hot reload during development. Changes to Java files are automatically compiled and reloaded when you refresh your browser or make a new request. No need to restart the application.

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

**Distributed Tracing** - Integrate AWS X-Ray for end-to-end request tracing across Lambda, API Gateway, RDS, and external services. This would provide visibility into performance bottlenecks and help identify optimization opportunities.

**Structured Logging** - Implement JSON-formatted logs with correlation IDs to enable better log aggregation and analysis in CloudWatch Logs Insights. Each request would carry a unique trace ID throughout its lifecycle.

**Custom Metrics** - Add business metrics (warranties created, claims processed, receipt uploads) to CloudWatch for better operational insights and alerting on business KPIs.

### Performance Optimization

**Caching Layer** - Introduce ElastiCache (Redis) for frequently accessed data like product catalogs and user profiles. Implement cache-aside pattern with TTL-based invalidation to reduce database load and improve response times.

**Connection Pooling** - Optimize database connection management with HikariCP tuning specific to Lambda's execution model, including connection lifecycle management across warm starts.

### Database Management

**Schema Migrations** - Replace Hibernate's schema validation with Flyway or Liquibase for version-controlled database migrations. This enables safer deployments with rollback capabilities and audit trails of schema changes.

**Read Replicas** - Implement read/write splitting to distribute query load across RDS read replicas, improving performance for read-heavy operations like product searches.

### Event-Driven Architecture

**Asynchronous Processing** - Move long-running operations (Textract OCR, warranty expiration notifications) to SQS queues with dedicated Lambda consumers. This improves API response times and enables better retry logic.

**Event Notifications** - Implement SNS topics for warranty lifecycle events (expiring soon, expired, claim status changes) to enable real-time notifications and integration with external systems.

### Security Enhancements

**Secrets Management** - Migrate from environment variables to AWS Secrets Manager for database credentials and API keys. Enable automatic rotation and centralized secret management.

**API Rate Limiting** - Implement per-user rate limiting using API Gateway throttling or custom token bucket implementation to prevent abuse and ensure fair resource allocation.

**Request Signing** - Add request signature validation for S3 operations to prevent unauthorized access and ensure request integrity.

### High Availability & Disaster Recovery

**Multi-Region Deployment** - Deploy the application across multiple AWS regions with Route53 health checks and failover routing for improved availability and disaster recovery.

**Backup Strategy** - Implement automated RDS snapshots with cross-region replication and point-in-time recovery capabilities.

### API Evolution

**Versioning Strategy** - Introduce `/api/v1/` URL versioning to support backward compatibility as the API evolves. This enables gradual migration for clients and prevents breaking changes.

**GraphQL Gateway** - Consider adding a GraphQL layer for complex queries and reducing over-fetching, particularly for mobile clients with bandwidth constraints.

### Testing & Quality

**Integration Testing** - Add Testcontainers-based integration tests with PostgreSQL to validate database interactions and query performance.

**Contract Testing** - Implement consumer-driven contract tests to ensure API compatibility across versions and prevent breaking changes.

**Load Testing** - Document performance benchmarks and establish baseline metrics for response times, throughput, and resource utilization under various load conditions.

### CI/CD Pipeline

**Automated Deployment** - Implement GitHub Actions workflow for:
- Automated testing on pull requests
- Native compilation and deployment to staging
- Production deployment with manual approval gates
- Security scanning with Snyk or OWASP dependency check

### Cost Optimization

**Lambda Optimization** - Document cost analysis comparing JVM vs native runtimes, including cold start frequency and execution duration metrics.

**S3 Lifecycle Policies** - Implement intelligent tiering and lifecycle policies to automatically move old receipts to cheaper storage classes (S3-IA, Glacier).

**Reserved Capacity** - Analyze usage patterns to identify opportunities for RDS reserved instances and Lambda provisioned concurrency where cost-effective.

## Related Projects

This API was originally built with Spring Boot. The migration to Quarkus was driven by the need for better serverless performance and native compilation support. The original Spring Boot version can be found in the project history.

For more information about Quarkus features and guides, visit https://quarkus.io/
