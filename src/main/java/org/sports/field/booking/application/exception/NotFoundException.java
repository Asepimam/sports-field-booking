package org.sports.field.booking.application.exception;

import jakarta.ws.rs.core.Response;

public class NotFoundException extends AppException {
    public NotFoundException(String message) {
        super(Response.Status.NOT_FOUND.getStatusCode(), "NOT_FOUND_ERROR", message);
    }
}
