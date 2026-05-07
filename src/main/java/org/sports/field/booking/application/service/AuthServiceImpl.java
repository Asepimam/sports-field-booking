package org.sports.field.booking.application.service;

import org.sports.field.booking.application.dto.LoginRequestDTO;
import org.sports.field.booking.application.dto.LoginResponseDTO;
import org.sports.field.booking.application.dto.RefreshTokenRequestDTO;
import org.sports.field.booking.application.dto.UserRequestDTO;
import org.sports.field.booking.application.dto.UserResponseDTO;
import org.sports.field.booking.domain.entity.ProfileEntity;
import org.sports.field.booking.domain.entity.Role;
import org.sports.field.booking.domain.entity.UserEntity;
import org.sports.field.booking.application.exception.AuthenticationException;
import org.sports.field.booking.application.exception.DatabaseException;
import org.sports.field.booking.application.exception.NotFoundException;
import org.sports.field.booking.application.exception.ServerException;
import org.sports.field.booking.application.mapper.ProfileMapper;
import org.sports.field.booking.application.mapper.UserMapper;
import org.sports.field.booking.application.mapper.UserMapperImpl;
import org.sports.field.booking.application.security.PasswordHasher;
import org.sports.field.booking.application.security.TokenService;
import org.sports.field.booking.domain.repository.ProfileRepository;
import org.sports.field.booking.domain.repository.UserRepository;
import org.sports.field.booking.application.security.TokenValidationResult;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordHasher passwordHasher;
    private final TokenService tokenService;
    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;

    public AuthServiceImpl(UserRepository userRepository,
            TokenService tokenService,
            PasswordHasher passwordHasher,
            ProfileRepository profileRepository,
            UserMapper userMapper,
            ProfileMapper profileMapper) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordHasher = passwordHasher;
        this.profileRepository = profileRepository;
        this.userMapper = userMapper;
        this.profileMapper = profileMapper;
    }

    @Override
    @Transactional(rollbackOn = { AuthenticationException.class, DatabaseException.class, ServerException.class })
    public UserResponseDTO register(UserRequestDTO userRequest) {
        try {
            if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
                throw new AuthenticationException("Email already in use");
            }

            UserEntity user = userMapper.toEntity(userRequest);
            if (user.role == null) {
                user.role = Role.CUSTOMER;
            }
            user.passwordHash = passwordHasher.hash(userRequest.getPassword());

            ProfileEntity profile = profileMapper.toEntity(userRequest);
            userRepository.save(user);
            profile.user = user;
            profileRepository.save(profile);
            return userMapper.toResponseDTO(user);

        } catch (AuthenticationException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to register user", ex);
        } catch (Exception ex) {
            throw new ServerException("Failed to register user", ex);
        }
    }

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

            if (!passwordHasher.verify(loginRequest.getPassword(), user.passwordHash)) {
                throw new AuthenticationException("Invalid credentials");
            }

            String accessToken = tokenService.generateToken(user.email, user.role);
            String refreshToken = tokenService.generateRefreshToken(user.email);

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(accessToken);
            response.setRefreshToken(refreshToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(3600);
            response.setEmail(user.email);
            response.setUsername(user.userName);

            return response;
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to login user", ex);
        } catch (Exception ex) {
            throw new ServerException("Failed to generate token", ex);
        }
    }

    @Override
    @Transactional
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshRequest) {
        try {
            TokenValidationResult validationResult = tokenService.validateToken(refreshRequest.getRefreshToken());

            if (!validationResult.isValid()) {
                throw new AuthenticationException("Invalid refresh token: " + validationResult.getErrorMessage());
            }

            String tokenType = validationResult.getClaim("token_type");
            if (!"refresh".equals(tokenType)) {
                throw new AuthenticationException("Invalid token type. Expected refresh token");
            }

            String email = validationResult.getSubject();
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            String newAccessToken = tokenService.generateToken(email, user.role);
            String newRefreshToken = tokenService.generateRefreshToken(email);

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);
            response.setTokenType("Bearer");
            response.setExpiresIn(3600);
            response.setEmail(user.email);
            response.setUsername(user.userName);

            return response;
        } catch (AuthenticationException | NotFoundException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to refresh token", ex);
        } catch (Exception ex) {
            throw new ServerException("Failed to generate token", ex);
        }
    }

    @Override
    public boolean validateToken(String token) {
        return tokenService.isTokenValid(token);
    }

    @Override
    public TokenValidationResult getTokenValidationResult(String token) {
        return tokenService.validateToken(token);
    }

    @Override
    public String extractEmailFromToken(String token) {
        try {
            TokenValidationResult result = tokenService.validateToken(token);
            if (result.isValid()) {
                return result.getSubject();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void logout(String token) {
        // JWT is stateless; server-side logout can be extended with a blacklist later.
    }

    @Override
    @Transactional
    public UserEntity getCurrentUser(String token) {
        try {
            String email = extractEmailFromToken(token);
            if (email == null) {
                throw new AuthenticationException("Invalid token");
            }

            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User not found"));
        } catch (AuthenticationException | NotFoundException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch current user", ex);
        }
    }

    @Override
    public boolean checkEmailExists(String email) {
        try {
            return userRepository.findByEmail(email).isPresent();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to check email", ex);
        }
    }
}
