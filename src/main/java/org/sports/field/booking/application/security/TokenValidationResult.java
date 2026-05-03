package org.sports.field.booking.application.security;

import java.util.HashMap;
import java.util.Map;

public class TokenValidationResult {
    private final boolean valid;
    private final String errorMessage;
    private String subject;
    private Map<String, Object> claims = new HashMap<>();

    public TokenValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public <T> T getClaim(String claimName) {
        return (T) claims.get(claimName);
    }

    public void setClaim(String claimName, Object value) {
        this.claims.put(claimName, value);
    }

    public Map<String, Object> getClaims() {
        return claims;
    }
}