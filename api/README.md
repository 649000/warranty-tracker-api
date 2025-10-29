# Warranty Tracker API

A Quarkus-based REST API for managing product warranties, claims, and user products. Originally built with Spring Boot, this project was migrated to Quarkus to leverage cloud-native features and GraalVM native compilation for optimal performance on AWS Lambda.

## Why Quarkus?

This project was migrated from Spring Boot to Quarkus for several key reasons:

- **Cloud-Native Design** - Built from the ground up for containerized and serverless environments
- **GraalVM Native Images** - Compile to native executables with sub-second startup times and minimal memory footprint
- **Lambda Optimization** - Native images drastically reduce cold start times on AWS Lambda
- **Developer Experience** - Live reload and dev mode make local development fast and efficient

## Architecture

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

## Cloud Deployment

This API is designed to run on AWS Lambda with API Gateway integration. The serverless architecture provides:

- Automatic scaling based on request volume
- Pay-per-use pricing model
- No server management overhead
- Built-in high availability

### AWS Services

- **AWS Lambda** - Serverless compute for API execution (optimized with GraalVM native images)
- **API Gateway** - HTTP API endpoint management
- **RDS PostgreSQL** - Relational database for persistent storage (via Supabase)
- **S3** - Object storage for receipt images and documents
- **Textract** - OCR service for extracting data from receipt images
- **CDK** - Infrastructure as Code for provisioning AWS resources

The CDK stack (in the `cdk/` directory) defines the infrastructure including Lambda functions, API Gateway, and IAM roles.

## Running Locally

Start the application in development mode with live reload:

```shell script
./mvnw quarkus:dev
```

The Dev UI is available at http://localhost:8080/q/dev/

Swagger UI is available at http://localhost:8080/api/q/swagger-ui (when running in dev mode)

## Database

The application uses PostgreSQL with the `warranty_tracker` schema. Configure your database connection using environment variables:

```
SUPABASE_DB_URL=jdbc:postgresql://your-host:5432/postgres
SUPABASE_DB_USER=your_username
SUPABASE_DB_PASSWORD=your_password
```

The schema validation strategy is set to `validate` in production to ensure database schema matches entity definitions.

## Authentication

API endpoints require Firebase authentication. The application verifies JWT tokens issued by Firebase:

- Public keys are fetched from Google's JWK endpoint
- The `@Authenticated` annotation protects resources
- Current user is extracted from JWT via `SecurityIdentity`
- `UserSecurityAugmentor` enriches the security context with user entity data

## Building and Deployment

This project uses AWS CDK for infrastructure provisioning and deployment. The build process differs between development and production environments.

### Development Build (JVM)

Development uses standard JVM runtime for faster iteration:

```shell script
./mvnw clean package
```

This creates a `function.zip` in `target/` containing the Quarkus application ready for Lambda deployment.

### Production Build (Native)

Production uses GraalVM native compilation for optimal performance:

```shell script
./mvnw clean package -Dnative -Dquarkus.native.container-build=true
```

The native build:
- Compiles to ARM64 architecture for AWS Graviton2 processors
- Produces a standalone executable with no JVM dependency
- Reduces cold start times from seconds to milliseconds
- Cuts memory usage by 50-70% compared to JVM

### Deploying to AWS

Deploy using CDK from the `cdk/` directory:

```shell script
# Development
cd cdk
cdk deploy --all

# Production
cd cdk
cdk deploy -c env=prd --all
```

See `cdk/README.md` for detailed deployment instructions and environment configuration.

## Technical Implementation

### Custom Security Integration

The application implements a custom security augmentor (`UserSecurityAugmentor`) that enriches the security context with user entity data. This eliminates redundant database queries by loading the user once during authentication and making it available throughout the request lifecycle via `SecurityIdentity.getAttribute("user")`.

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

### AWS Integration

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

## Development

Quarkus provides hot reload during development. Changes to Java files are automatically compiled and reloaded when you refresh your browser or make a new request. No need to restart the application.

The Dev UI at http://localhost:8080/q/dev/ provides useful tools for:
- Viewing configuration
- Testing endpoints
- Managing database schema
- Monitoring application metrics

## Related Projects

This API was originally built with Spring Boot. The migration to Quarkus was driven by the need for better serverless performance and native compilation support. The original Spring Boot version can be found in the project history.

For more information about Quarkus features and guides, visit https://quarkus.io/
