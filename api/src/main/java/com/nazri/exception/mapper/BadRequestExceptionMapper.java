package com.nazri.exception.mapper;

import com.nazri.dto.JsonApiErrorResponse;
import com.nazri.exception.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(BadRequestException exception) {
        JsonApiErrorResponse errorResponse = new JsonApiErrorResponse(
            exception.getMessage(),
            "BAD_REQUEST",
            Response.Status.BAD_REQUEST.getStatusCode()
        );

        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(errorResponse)
                       .build();
    }
}
