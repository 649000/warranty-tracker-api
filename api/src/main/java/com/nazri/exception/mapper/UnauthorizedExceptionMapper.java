package com.nazri.exception.mapper;

import com.nazri.dto.JsonApiErrorResponse;
import com.nazri.exception.UnauthorizedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {

    @Override
    public Response toResponse(UnauthorizedException exception) {
        JsonApiErrorResponse errorResponse = new JsonApiErrorResponse(
            exception.getMessage(),
            "UNAUTHORIZED",
            Response.Status.UNAUTHORIZED.getStatusCode()
        );

        return Response.status(Response.Status.UNAUTHORIZED)
                       .entity(errorResponse)
                       .build();
    }
}
