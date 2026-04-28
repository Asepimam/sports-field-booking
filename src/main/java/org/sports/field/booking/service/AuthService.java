package org.sports.field.booking.service;

import org.sports.field.booking.dto.LoginRequestDTO;
import org.sports.field.booking.dto.LoginResponseDTO;
import org.sports.field.booking.dto.RefreshTokenRequestDTO;
import org.sports.field.booking.entity.UserEntity;
import org.sports.field.booking.repository.UserRepository;
import org.sports.field.booking.security.JwtService;
import org.sports.field.booking.security.TokenValidationResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
            JwtService jwtService,
            PasswordService passwordService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) throws Exception {
        // Find user by email
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + loginRequest.getEmail()));

        // Verify password
        if (!passwordService.verify(loginRequest.getPassword(), user.passwordHash)) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate tokens
        String accessToken = jwtService.generateToken(user.email);
        String refreshToken = jwtService.generateRefreshToken(user.email);

        // Update last login time (add field to UserEntity if needed)
        // user.lastLoginAt = LocalDateTime.now();
        user.persist();

        // Create response
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(3600); // 1 hour
        response.setEmail(user.email);
        response.setUsername(user.userName);

        return response;
    }

    @Transactional
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshRequest) throws Exception {
        // Validate refresh token
        TokenValidationResult validationResult = jwtService.validateToken(refreshRequest.getRefreshToken());

        if (!validationResult.isValid()) {
            throw new RuntimeException("Invalid refresh token: " + validationResult.getErrorMessage());
        }

        // Check if it's a refresh token
        String tokenType = validationResult.getClaim("token_type");
        if (!"refresh".equals(tokenType)) {
            throw new RuntimeException("Invalid token type. Expected refresh token");
        }

        // Get email from token
        String email = validationResult.getSubject();

        // Verify user still exists
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate new access token
        String newAccessToken = jwtService.generateToken(email);

        // Optional: Generate new refresh token (refresh token rotation)
        String newRefreshToken = jwtService.generateRefreshToken(email);

        // Create response
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setTokenType("Bearer");
        response.setExpiresIn(3600);
        response.setEmail(user.email);
        response.setUsername(user.userName);

        return response;
    }

    public boolean validateToken(String token) {
        return jwtService.isTokenValid(token);
    }

    public TokenValidationResult getTokenValidationResult(String token) {
        return jwtService.validateToken(token);
    }

    public String extractEmailFromToken(String token) {
        try {
            TokenValidationResult result = jwtService.validateToken(token);
            if (result.isValid()) {
                return result.getSubject();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public void logout(String token) {
        // For JWT stateless, we don't need to do anything server-side
        // But you could add the token to a blacklist in Redis or database
        // For now, just log the logout
        System.out.println("User logged out");
    }

    @Transactional
    public UserEntity getCurrentUser(String token) throws Exception {
        String email = extractEmailFromToken(token);
        if (email == null) {
            throw new RuntimeException("Invalid token");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean checkEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}