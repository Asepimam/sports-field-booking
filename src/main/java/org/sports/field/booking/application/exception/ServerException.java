package org.sports.field.booking.application.exception;

import jakarta.ws.rs.core.Response;

public class ServerException extends AppException {
    public ServerException(String message, Throwable cause) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "SERVER_ERROR", message, cause);
    }
}
