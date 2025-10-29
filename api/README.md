# Warranty Tracker API service

## Overview
The Warranty Tracker API service, built on Spring Boot, is designed to streamline warranty management for your products, providing a reliable and efficient platform for tracking warranty details effortlessly.

## Objectives
* Familiarization with Spring Boot: Gain proficiency in utilizing the Spring Boot framework for building robust and scalable APIs.
* Building OAuth2 Resource Server: Learn the implementation of OAuth2 resource server functionality to enhance security and access control.
* Exploration of Identity as a Service (IDaaS) Solutions: Explore services like AWS Cognito to understand their role in providing secure identity management solutions.

## Features
The API service allows users to:

* Document warranties for their products, including key details like expected expiry dates and associated company information for technical support.
* Email reminders for products with warranties that are nearing expiration


## Technologies Utilized
* Spring Boot: Leveraged for efficient API development.
* Spring Security & OAuth2: Ensures robust security measures for access control.
* Lombok: Streamlines Java code with annotations, reducing boilerplate code.
* Maven: Used for project management and build automation.
* MapStruct: Facilitates mapping between Java bean types.
* AWS Java SDK for Amazon DynamoDB: Employed for seamless integration with Amazon DynamoDB.



## Installation and Setup Instructions
AWS Access and Secret keys are required and must be set as an environment variable to run this app

Example on setting environment variable on macOS:
```
export aws_accesskey=<KEY GOES HERE>
export aws_secretkey=<KEY GOES HERE>
```

To run the service, navigate to the root of the project and execute the command

```
mvn spring-boot:run
```
## Endpoints
|          | localhost                                   | Render                                                     |
|----------|---------------------------------------------|-------------------------------------------------------------|
| Open API | http://localhost:8080/swagger-ui/index.html | https://warranty-tracker-api.onrender.com/swagger-ui/index.html |
| Actuator | http://localhost:8080/actuator              | https://warranty-tracker-api.onrender.com/actuator              |

Note that endpoints are secured by Spring Security and require a valid JWT access token to be called. Only the `/actuator` and `/swagger-ui/**` endpoints are not secured.

Service on Render may spin down due to inactivity, resulting in delays of 50 seconds or more for requests.

## Reflection

This project bears resemblance to the [Subtracker project](https://github.com/649000/subtracker-rest-api) I previously worked on. Here, I've integrated AWS Cognito, an Identity as a Service solution offered by AWS. The primary objective was to acquaint myself with AWS services, which play a crucial role in my preparation for the AWS certification.
