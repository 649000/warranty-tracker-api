# Warranty Tracker CDK Infrastructure

This project contains the AWS infrastructure code for the Warranty Tracker application using AWS Cloud Development Kit (CDK) for Java.

The infrastructure is defined as code, allowing you to provision AWS resources through Java instead of manually through the AWS console.

## Project Structure
```
cdk/
├── src/main/java/com/nazri/
│ ├── CdkApp.java # Main CDK application entry point
│ ├── stack/
│ │ ├── APIGatewayStack.java # HTTP API Gateway stack
│ │ └── APIStack.java # Lambda function stack
│ ├── config/
│ │ └── StackConfig.java # Environment configuration
│ └── util/
│ ├── Constant.java # Shared constants
│ └── TagUtil.java # Resource tagging utilities
├── cdk.json # CDK app configuration
└── pom.xml # Maven dependencies
```


## Prerequisites

- AWS CLI configured with appropriate credentials
- AWS CDK Toolkit installed (`npm install -g aws-cdk`)
- Java 17+ and Maven
- Environment variables set for database connections

## Environments

Two environments are supported:
- **dev** - Development environment (default)
    - Uses JVM-based Lambda runtime
    - Deployed to `ap-southeast-1` region
    - 512 MB memory, 15 second timeout
- **prd** - Production environment
    - Uses GraalVM native Lambda runtime (ARM64)
    - Deployed to `ap-southeast-1` region
    - 512 MB memory, 15 second timeout

## Environment Variables

Set these environment variables before deploying:

### Development
```
export SUPABASE_DB_URL="your-dev-db-url"
export SUPABASE_DB_USER="your-dev-db-user"
export SUPABASE_DB_PASSWORD="your-dev-db-password"
```
### Production
```
export SUPABASE_PROD_DB_URL="your-prod-db-url"
export SUPABASE_PROD_DB_USER="your-prod-db-user"
export SUPABASE_PROD_DB_PASSWORD="your-prod-db-password"
```
## Commands

### Development Environment (default)
```
cdk ls --all                    # List all stacks
cdk synth --all                 # Synthesize CloudFormation templates
cdk deploy --all                # Deploy all stacks to AWS
cdk diff --all                  # Compare deployed stacks with current state
cdk destroy --all               # Remove all deployed stacks from AWS
```
### Production Environment
```
cdk ls -c env=prd --all         # List all stacks
cdk synth -c env=prd --all      # Synthesize CloudFormation templates
cdk deploy -c env=prd --all     # Deploy all stacks to AWS
cdk diff -c env=prd --all       # Compare deployed stacks with current state
cdk destroy -c env=prd --all    # Remove all deployed stacks from AWS
```
### Other Commands
```
mvn compile                     # Compile the Java code
mvn package                     # Build the project
```

## Stacks

### APIGatewayStack
**Stack Name:** `warranty-tracker-api-gw-stack-{env}`

Creates the HTTP API Gateway that serves as the entry point for all API requests.

**Resources created:**
- HTTP API Gateway
    - Name: `Warranty Tracker HTTP API Gateway - {env}`
    - No default authorization (uses `HttpNoneAuthorizer`)
    - Tagged with project and environment

### APIStack
**Stack Name:** `warranty-tracker-api-stack-{env}`

Creates the Lambda function that handles all API requests and integrates it with the API Gateway.

**Resources created:**
- Lambda Function
    - Name: `warranty-tracker-api-{env}`
    - **Dev:** Java 21 runtime with JVM
    - **Prod:** Native runtime (GraalVM) on ARM64 architecture
    - Handler: `io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest`
    - Memory: 512 MB
    - Timeout: 15 seconds
    - Environment variables for database connection
- API Gateway Integration
    - Route: `/api/{proxy+}` (catches all API requests)
    - Method: ANY (all HTTP methods)
    - Integration: Lambda proxy integration

## Configuration

Environment-specific configuration is managed through:
- **Context variables:** Pass `-c env=prd` to select production environment
- **StackConfig class:** Manages environment-specific settings including:
    - AWS region (ap-southeast-1)
    - Runtime type (JVM vs Native)
    - Resource tags
- **Constant class:** Stores environment variables and shared constants
    - Database connection strings from environment variables
    - Environment names (dev, prd)

## Resource Tagging

All resources are automatically tagged with:
- `project: warranty-tracker`
- `environment: dev` or `environment: prd`

These tags help with:
- Cost allocation and tracking
- Resource organization
- Access control policies

## Architecture Notes

- **Development** uses JVM runtime for faster build times and easier debugging
- **Production** uses GraalVM native compilation for:
    - Faster cold starts
    - Lower memory usage
    - Reduced costs
- Both environments use the same Quarkus application code
- API Gateway uses a catch-all route (`/api/{proxy+}`) to forward all requests to the Lambda function
- The Lambda function handles routing internally via Quarkus

## Troubleshooting

### Build Issues
- Build the API before deploying CDK
- For native builds, install GraalVM first
- Check that `function.zip` exists in `../api/target/`

### Deployment Issues
- Verify environment variables are set correctly
- Check AWS credentials are configured
- Verify you have permissions to create Lambda functions and API Gateways
- Confirm the AWS region is correct (ap-southeast-1)

### Runtime Issues
- Check Lambda function logs in CloudWatch
- Verify database connection strings are correct
