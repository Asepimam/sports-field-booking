package org.sports.field.booking.application.security;

import org.sports.field.booking.domain.entity.Role;

public interface TokenService {
    String generateToken(String username, Role role) throws Exception;

    String generateToken(String username) throws Exception;

    String generateRefreshToken(String username) throws Exception;

    String refreshAccessToken(String refreshToken) throws Exception;

    TokenValidationResult validateToken(String token);

    boolean isTokenValid(String token);

    String extractUsername(String token) throws Exception;

    String extractTokenType(String token) throws Exception;
}
