package com.nazri.exception;

import com.nazri.dto.JsonApiErrorResponse;
import com.nazri.exception.mapper.*;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionMappersTest {

    @Test
    void testBadRequestExceptionMapper() {
        BadRequestExceptionMapper mapper = new BadRequestExceptionMapper();
        BadRequestException ex = new BadRequestException("Invalid input");

        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        JsonApiErrorResponse body = (JsonApiErrorResponse) response.getEntity();
        assertEquals("Invalid input", body.errors.get(0).detail);
        assertEquals("BAD_REQUEST", body.errors.get(0).code);
        assertEquals("400", body.errors.get(0).status);
    }

    @Test
    void testForbiddenExceptionMapper() {
        ForbiddenExceptionMapper mapper = new ForbiddenExceptionMapper();
        ForbiddenException ex = new ForbiddenException("Access denied");

        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());

        JsonApiErrorResponse body = (JsonApiErrorResponse) response.getEntity();
        assertEquals("Access denied", body.errors.get(0).detail);
        assertEquals("FORBIDDEN", body.errors.get(0).code);
        assertEquals("403", body.errors.get(0).status);
    }

    @Test
    void testUnauthorizedExceptionMapper() {
        UnauthorizedExceptionMapper mapper = new UnauthorizedExceptionMapper();
        UnauthorizedException ex = new UnauthorizedException("Authentication required");

        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        JsonApiErrorResponse body = (JsonApiErrorResponse) response.getEntity();
        assertEquals("Authentication required", body.errors.get(0).detail);
        assertEquals("UNAUTHORIZED", body.errors.get(0).code);
        assertEquals("401", body.errors.get(0).status);
    }

    @Test
    void testResourceConflictExceptionMapper() {
        ResourceConflictExceptionMapper mapper = new ResourceConflictExceptionMapper();
        ResourceConflictException ex = new ResourceConflictException("User", "Email already exists");

        assertEquals("User", ex.getResourceType());
        assertEquals("Email already exists", ex.getReason());

        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());

        JsonApiErrorResponse body = (JsonApiErrorResponse) response.getEntity();
        assertTrue(body.errors.get(0).detail.contains("Conflict for User"));
        assertEquals("RESOURCE_CONFLICT", body.errors.get(0).code);
        assertEquals("409", body.errors.get(0).status);

        // test single message constructor
        ResourceConflictException exSingle = new ResourceConflictException("Direct conflict message");
        assertEquals("Resource", exSingle.getResourceType());
        assertEquals("General conflict", exSingle.getReason());
    }

    @Test
    void testResourceNotFoundExceptionMapper() {
        ResourceNotFoundExceptionMapper mapper = new ResourceNotFoundExceptionMapper();
        ResourceNotFoundException ex = new ResourceNotFoundException("Product", 999L);

        assertEquals("Product", ex.getResourceType());
        assertEquals(999L, ex.getResourceId());

        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

        JsonApiErrorResponse body = (JsonApiErrorResponse) response.getEntity();
        assertTrue(body.errors.get(0).detail.contains("Product not found with identifier: 999"));
        assertEquals("RESOURCE_NOT_FOUND", body.errors.get(0).code);
        assertEquals("404", body.errors.get(0).status);

        // test single message constructor
        ResourceNotFoundException exSingle = new ResourceNotFoundException("Not found");
        assertEquals("Resource", exSingle.getResourceType());
        assertNull(exSingle.getResourceId());
    }

    @Test
    void testValidationExceptionMapper() {
        ValidationExceptionMapper mapper = new ValidationExceptionMapper();
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "must be a valid email");
        ValidationException ex = new ValidationException("Validation failed", errors);

        assertEquals(errors, ex.getValidationErrors());

        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        JsonApiErrorResponse body = (JsonApiErrorResponse) response.getEntity();
        assertEquals("Validation failed", body.errors.get(0).detail);
        assertEquals("VALIDATION_ERROR", body.errors.get(0).code);
        assertEquals("400", body.errors.get(0).status);

        // single message constructor
        ValidationException exSingle = new ValidationException("Field invalid");
        assertNull(exSingle.getValidationErrors());
    }

    @Test
    void testGlobalExceptionMapper() {
        GlobalExceptionMapper mapper = new GlobalExceptionMapper();
        RuntimeException ex = new RuntimeException("Something unexpected happened");

        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        JsonApiErrorResponse body = (JsonApiErrorResponse) response.getEntity();
        assertTrue(body.errors.get(0).detail.contains("An unexpected error occurred"));
        assertEquals("INTERNAL_SERVER_ERROR", body.errors.get(0).code);
        assertEquals("500", body.errors.get(0).status);
    }

    @Test
    void testInternalServerErrorException() {
        InternalServerErrorException ex1 = new InternalServerErrorException("Server broke");
        assertEquals("Server broke", ex1.getMessage());

        Throwable cause = new IllegalArgumentException("Bad input");
        InternalServerErrorException ex2 = new InternalServerErrorException("Server error with cause", cause);
        assertEquals(cause, ex2.getCause());

        BadRequestException ex3 = new BadRequestException("bad", cause);
        assertEquals(cause, ex3.getCause());

        ForbiddenException ex4 = new ForbiddenException("forbidden", cause);
        assertEquals(cause, ex4.getCause());

        UnauthorizedException ex5 = new UnauthorizedException("unauth", cause);
        assertEquals(cause, ex5.getCause());
    }
}
