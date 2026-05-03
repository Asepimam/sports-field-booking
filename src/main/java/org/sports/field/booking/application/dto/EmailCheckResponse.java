package org.sports.field.booking.application.dto;

public class EmailCheckResponse {
    private boolean exists;
    private String message;

    public EmailCheckResponse(boolean exists, String message) {
        this.exists = exists;
        this.message = message;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}