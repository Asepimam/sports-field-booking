package org.sports.field.booking.application.exception;

import jakarta.ws.rs.core.Response;

public class InputException extends AppException {
    public InputException(String message) {
        super(Response.Status.BAD_REQUEST.getStatusCode(), "INPUT_ERROR", message);
    }
}
