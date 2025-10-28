package com.nazri.exception.mapper;

import com.nazri.dto.JsonApiErrorResponse;
import com.nazri.exception.ResourceConflictException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ResourceConflictExceptionMapper implements ExceptionMapper<ResourceConflictException> {

    @Override
    public Response toResponse(ResourceConflictException exception) {
        JsonApiErrorResponse errorResponse = new JsonApiErrorResponse(
            exception.getMessage(),
            "RESOURCE_CONFLICT",
            Response.Status.CONFLICT.getStatusCode()
        );

        return Response.status(Response.Status.CONFLICT)
                       .entity(errorResponse)
                       .build();
    }
}
