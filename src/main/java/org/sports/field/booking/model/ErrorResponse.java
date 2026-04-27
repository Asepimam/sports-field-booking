package org.sports.field.booking.model;

import java.util.List;

public class ErrorResponse {
    public String code;
    public Object message;

    public ErrorResponse(String code, Object message) {
        this.code = code;
        this.message = message;
    }
}
