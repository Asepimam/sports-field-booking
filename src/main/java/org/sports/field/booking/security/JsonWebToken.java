package org.sports.field.booking.security;

import java.util.Set;

public interface JsonWebToken {
    String getName();

    String getIssuer();

    <T> T getClaim(String claimName);

    Set<String> getClaimNames();
}