package org.sports.field.booking.application.exception;

import jakarta.ws.rs.core.Response;

public class DatabaseException extends AppException {
    public DatabaseException(String message, Throwable cause) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "DATABASE_ERROR", message, cause);
    }
}
