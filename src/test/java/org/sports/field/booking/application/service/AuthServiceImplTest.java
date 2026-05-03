package org.sports.field.booking.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.sports.field.booking.application.dto.LoginRequestDTO;
import org.sports.field.booking.application.dto.LoginResponseDTO;
import org.sports.field.booking.domain.entity.Role;
import org.sports.field.booking.domain.entity.UserEntity;
import org.sports.field.booking.application.exception.AuthenticationException;
import org.sports.field.booking.application.security.PasswordHasher;
import org.sports.field.booking.application.security.TokenService;
import org.sports.field.booking.domain.repository.UserRepository;
import org.sports.field.booking.application.security.TokenValidationResult;

class AuthServiceImplTest {

    @Test
    void loginReturnsAccessAndRefreshToken() {
        FakeUserRepository repository = new FakeUserRepository();
        repository.users.add(user("john@example.com"));
        AuthService service = new AuthServiceImpl(repository, new TestJwtService(), new TestPasswordService());

        LoginResponseDTO response = service.login(loginRequest("john@example.com", "secret"));

        assertEquals("access-john@example.com-CUSTOMER", response.getToken());
        assertEquals("refresh-john@example.com", response.getRefreshToken());
        assertEquals("john@example.com", response.getEmail());
    }

    @Test
    void loginRejectsWrongPassword() {
        FakeUserRepository repository = new FakeUserRepository();
        repository.users.add(user("john@example.com"));
        AuthService service = new AuthServiceImpl(repository, new TestJwtService(), new TestPasswordService());

        assertThrows(AuthenticationException.class, () -> service.login(loginRequest("john@example.com", "wrong")));
    }

    @Test
    void refreshTokenReturnsNewTokensForValidRefreshToken() {
        FakeUserRepository repository = new FakeUserRepository();
        repository.users.add(user("john@example.com"));
        AuthService service = new AuthServiceImpl(repository, new TestJwtService(), new TestPasswordService());

        LoginResponseDTO response = service.refreshToken(refreshRequest("valid-refresh"));

        assertEquals("access-john@example.com-CUSTOMER", response.getToken());
        assertEquals("refresh-john@example.com", response.getRefreshToken());
    }

    @Test
    void checkEmailExistsUsesRepository() {
        FakeUserRepository repository = new FakeUserRepository();
        repository.users.add(user("john@example.com"));
        AuthService service = new AuthServiceImpl(repository, new TestJwtService(), new TestPasswordService());

        assertTrue(service.checkEmailExists("john@example.com"));
    }

    private static LoginRequestDTO loginRequest(String email, String password) {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail(email);
        dto.setPassword(password);
        return dto;
    }

    private static org.sports.field.booking.application.dto.RefreshTokenRequestDTO refreshRequest(String token) {
        org.sports.field.booking.application.dto.RefreshTokenRequestDTO dto = new org.sports.field.booking.application.dto.RefreshTokenRequestDTO();
        dto.setRefreshToken(token);
        return dto;
    }

    private static UserEntity user(String email) {
        UserEntity entity = new UserEntity();
        entity.userName = "john";
        entity.email = email;
        entity.passwordHash = "hashed-secret";
        entity.role = Role.CUSTOMER;
        return entity;
    }

    private static class TestPasswordService implements PasswordHasher {
        @Override
        public String hash(String password) {
            return "hashed-" + password;
        }

        @Override
        public boolean verify(String password, String hash) {
            return ("hashed-" + password).equals(hash);
        }
    }

    private static class TestJwtService implements TokenService {
        @Override
        public String generateToken(String username, Role role) {
            return "access-" + username + "-" + role.name();
        }

        @Override
        public String generateToken(String username) {
            return generateToken(username, Role.CUSTOMER);
        }

        @Override
        public String generateRefreshToken(String username) {
            return "refresh-" + username;
        }

        @Override
        public String refreshAccessToken(String refreshToken) {
            return "access-john@example.com-CUSTOMER";
        }

        @Override
        public TokenValidationResult validateToken(String token) {
            TokenValidationResult result = new TokenValidationResult("valid-refresh".equals(token), null);
            if (result.isValid()) {
                result.setSubject("john@example.com");
                result.setClaim("token_type", "refresh");
            }
            return result;
        }

        @Override
        public boolean isTokenValid(String token) {
            return "valid-access".equals(token);
        }

        @Override
        public String extractUsername(String token) {
            return "john@example.com";
        }

        @Override
        public String extractTokenType(String token) {
            return "access";
        }
    }

    private static class FakeUserRepository implements UserRepository {
        private final List<UserEntity> users = new ArrayList<>();

        @Override
        public Optional<UserEntity> findByEmail(String email) {
            return users.stream().filter(user -> user.email.equals(email)).findFirst();
        }

        @Override
        public boolean existsByEmail(String email) {
            return findByEmail(email).isPresent();
        }

        @Override
        public List<UserEntity> getUsers(int page, int size) {
            return users;
        }

        @Override
        public void save(UserEntity user) {
            users.add(user);
        }

        @Override
        public long countUsers() {
            return users.size();
        }
    }
}
