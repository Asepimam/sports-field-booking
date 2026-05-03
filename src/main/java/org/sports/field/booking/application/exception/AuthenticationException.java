package org.sports.field.booking.application.exception;

import jakarta.ws.rs.core.Response;

public class AuthenticationException extends AppException {
    public AuthenticationException(String message) {
        super(Response.Status.UNAUTHORIZED.getStatusCode(), "AUTHENTICATION_ERROR", message);
    }
}
