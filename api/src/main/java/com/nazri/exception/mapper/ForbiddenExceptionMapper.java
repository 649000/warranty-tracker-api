package com.nazri.exception.mapper;

import com.nazri.dto.JsonApiErrorResponse;
import com.nazri.exception.ForbiddenException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    @Override
    public Response toResponse(ForbiddenException exception) {
        JsonApiErrorResponse errorResponse = new JsonApiErrorResponse(
            exception.getMessage(),
            "FORBIDDEN",
            Response.Status.FORBIDDEN.getStatusCode()
        );

        return Response.status(Response.Status.FORBIDDEN)
                       .entity(errorResponse)
                       .build();
    }
}
