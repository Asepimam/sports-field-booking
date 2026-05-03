package org.sports.field.booking.interfaces.exception;

import org.sports.field.booking.interfaces.model.ErrorResponse;
import org.sports.field.booking.application.exception.AppException;

import jakarta.persistence.PersistenceException;
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

        if (ex instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("INPUT_ERROR", ex.getMessage()))
                    .build();
        }

        if (ex instanceof PersistenceException) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("DATABASE_ERROR", "Database operation failed"))
                    .build();
        }

        return Response.status(500)
                .entity(new ErrorResponse("SERVER_ERROR", "Internal server error"))
                .build();
    }
}
