package com.nazri.exception.mapper;

import com.nazri.dto.JsonApiErrorResponse;
import com.nazri.exception.ValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(ValidationException exception) {
        JsonApiErrorResponse errorResponse = new JsonApiErrorResponse(
            exception.getMessage(),
            "VALIDATION_ERROR",
            Response.Status.BAD_REQUEST.getStatusCode()
        );

        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(errorResponse)
                       .build();
    }
}
