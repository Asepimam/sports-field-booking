package org.sports.field.booking.exceptions;

import org.sports.field.booking.model.ErrorResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception ex) {

        if (ex instanceof AppException appEx) {
            return Response.status(appEx.getStatus())
                    .entity(new ErrorResponse(appEx.getCode(), appEx.getMessage()))
                    .build();
        }

        return Response.status(500)
                .entity(new ErrorResponse("INTERNAL_ERROR", ex.getMessage()))
                .build();
    }
}