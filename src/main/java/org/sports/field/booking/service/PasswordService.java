package org.sports.field.booking.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordService {

    private final Argon2 argon2 = Argon2Factory.create();

    public String hash(String password) {
        return argon2.hash(2, 65536, 1, password.toCharArray());
    }

    public boolean verify(String password, String hash) {
        return argon2.verify(hash, password.toCharArray());
    }
}