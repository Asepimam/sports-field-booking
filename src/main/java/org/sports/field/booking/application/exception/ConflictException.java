package org.sports.field.booking.application.exception;

import jakarta.ws.rs.core.Response;

public class ConflictException extends AppException {
    public ConflictException(String message) {
        super(Response.Status.CONFLICT.getStatusCode(), "CONFLICT_ERROR", message);
    }
}
