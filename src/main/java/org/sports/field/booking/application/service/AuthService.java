package org.sports.field.booking.application.service;

import org.sports.field.booking.application.dto.LoginRequestDTO;
import org.sports.field.booking.application.dto.LoginResponseDTO;
import org.sports.field.booking.application.dto.RefreshTokenRequestDTO;
import org.sports.field.booking.domain.entity.UserEntity;
import org.sports.field.booking.application.security.TokenValidationResult;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    LoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshRequest);

    boolean validateToken(String token);

    TokenValidationResult getTokenValidationResult(String token);

    String extractEmailFromToken(String token);

    void logout(String token);

    UserEntity getCurrentUser(String token);

    boolean checkEmailExists(String email);
}
