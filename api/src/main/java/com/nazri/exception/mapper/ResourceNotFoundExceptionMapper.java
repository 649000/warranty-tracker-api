package com.nazri.exception.mapper;

import com.nazri.dto.JsonApiErrorResponse;
import com.nazri.exception.ResourceNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        JsonApiErrorResponse errorResponse = new JsonApiErrorResponse(
            exception.getMessage(),
            "RESOURCE_NOT_FOUND",
            Response.Status.NOT_FOUND.getStatusCode()
        );

        return Response.status(Response.Status.NOT_FOUND)
                       .entity(errorResponse)
                       .build();
    }
}
