package org.sports.field.booking.application.security;

public interface PasswordHasher {
    String hash(String password);

    boolean verify(String password, String hash);
}
