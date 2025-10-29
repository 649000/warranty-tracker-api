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

## Building for Production

Package the application as a standard JAR:

```shell script
./mvnw package
```

This creates `quarkus-run.jar` in `target/quarkus-app/`. Dependencies are in `target/quarkus-app/lib/`.

Run the packaged application:

```shell script
java -jar target/quarkus-app/quarkus-run.jar
```

### Uber JAR

Build a single JAR with all dependencies:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

Run it:

```shell script
java -jar target/*-runner.jar
```

## Native Executable (Recommended for Lambda)

Build a native executable with GraalVM for optimal Lambda performance:

```shell script
./mvnw package -Dnative
```

Or build in a container without local GraalVM installation:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

Execute the native binary:

```shell script
./target/api-1.0-SNAPSHOT-runner
```

Native executables provide:
- **Fast startup** - Sub-second cold starts on Lambda
- **Low memory** - Significantly reduced memory footprint
- **Cost savings** - Lower Lambda execution costs due to reduced duration and memory usage

## Configuration

The application supports multiple profiles:

- **dev** - Development configuration with Swagger UI enabled
- **prod** - Production configuration with Swagger UI disabled

Profile-specific properties are in `application-dev.properties` and `application-prod.properties`.

Key configuration options:

```properties
# API base path
quarkus.http.root-path=/api

# Pagination limits
app.pagination.default-size=20
app.pagination.max-size=20
app.pagination.max-page=500

# AWS S3 bucket
app.s3.bucket-name=warranty-tracker-dev

# Firebase JWT verification
mp.jwt.verify.publickey.location=https://www.googleapis.com/service_accounts/v1/jwk/securetoken%40system.gserviceaccount.com
mp.jwt.verify.issuer=https://securetoken.google.com/your-project-id
```

## API Endpoints

### Products

- `GET /api/product/search` - Search products by name, brand, or model number

### User Products

- `POST /api/user-product` - Register a new product for the current user
- `PUT /api/user-product/{id}` - Update user product details
- `DELETE /api/user-product/{id}` - Remove a user product

All endpoints require authentication and return JSON API formatted responses with proper error handling.

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
