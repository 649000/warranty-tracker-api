package com.nazri.exception.mapper;

import com.nazri.dto.JsonApiErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        // Log the full stack trace for debugging on the server side
        LOG.error("Unhandled exception occurred", exception);

        // Create a generic error response for the client
        // You might want to hide internal details from the client in production
        JsonApiErrorResponse errorResponse = new JsonApiErrorResponse(
                "An unexpected error occurred. Please try again later or contact support.",
                "INTERNAL_SERVER_ERROR",
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
    }
}

