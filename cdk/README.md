# Warranty Tracker CDK Infrastructure

This project contains the AWS infrastructure code for the Warranty Tracker application, built with the AWS Cloud Development Kit (CDK) for Java.

The infrastructure is defined as code using CDK, which allows us to provision AWS resources through Java code rather than manually through the AWS console.

## Project Structure

- `src/main/java/com/nazri/` - Java source files for CDK stacks and constructs
- `cdk.json` - CDK app configuration and entry point
- `pom.xml` - Maven dependencies and build configuration

## Prerequisites

- AWS CLI configured with appropriate credentials
- AWS CDK Toolkit installed
- Java 21+ and Maven

## Useful Commands

- `mvn compile` - Compile the Java code
- `cdk ls` - List all stacks in the app
- `cdk synth` - Synthesize CloudFormation templates
- `cdk deploy` - Deploy stacks to AWS
- `cdk diff` - Compare deployed stack with current state
- `cdk destroy` - Remove deployed stacks from AWS

## Stacks

### APIGatewayStack
Creates the HTTP API Gateway that serves as the entry point for all API requests. This stack sets up the API with proper naming and tagging based on the deployment environment.

### APIStack
Creates the Lambda functions that handle the API logic. This stack:
- Builds Lambda functions from the Quarkus application code
- Supports both JVM and native (GraalVM) deployment modes
- Configures environment-specific database connections
- Integrates the Lambda functions with the API Gateway routes
- Sets up proper resource tagging and permissions

## Environment Configuration

The infrastructure supports multiple environments (dev, staging, prod) through CDK context variables and environment-specific configuration in the Constant class.
