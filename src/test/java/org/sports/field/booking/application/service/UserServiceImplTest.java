package org.sports.field.booking.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.sports.field.booking.application.dto.UserRequestDTO;
import org.sports.field.booking.application.dto.UserResponseDTO;
import org.sports.field.booking.domain.entity.UserEntity;
import org.sports.field.booking.application.exception.ConflictException;
import org.sports.field.booking.application.security.PasswordHasher;
import org.sports.field.booking.application.mapper.UserMapper;
import org.sports.field.booking.domain.repository.UserRepository;

class UserServiceImplTest {

    @Test
    void createUserHashesPasswordAndSavesUser() {
        FakeUserRepository repository = new FakeUserRepository();
        UserService service = new UserServiceImpl(repository, new TestUserMapper(), new TestPasswordService());

        UserResponseDTO response = service.createUser(userRequest("john@example.com"));

        assertEquals("john@example.com", response.email);
        assertEquals("hashed-secret", repository.saved.passwordHash);
        assertEquals(1, repository.users.size());
    }

    @Test
    void createUserRejectsDuplicateEmail() {
        FakeUserRepository repository = new FakeUserRepository();
        repository.users.add(user("john@example.com"));
        UserService service = new UserServiceImpl(repository, new TestUserMapper(), new TestPasswordService());

        assertThrows(ConflictException.class, () -> service.createUser(userRequest("john@example.com")));
    }

    @Test
    void getUsersReturnsMappedUsers() {
        FakeUserRepository repository = new FakeUserRepository();
        repository.users.add(user("one@example.com"));
        repository.users.add(user("two@example.com"));
        UserService service = new UserServiceImpl(repository, new TestUserMapper(), new TestPasswordService());

        assertEquals(2, service.getUsers(1, 10).size());
        assertEquals(2, service.countUsers());
    }

    private static UserRequestDTO userRequest(String email) {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername("john");
        dto.setEmail(email);
        dto.setPassword("secret");
        return dto;
    }

    private static UserEntity user(String email) {
        UserEntity entity = new UserEntity();
        entity.userName = "john";
        entity.email = email;
        entity.passwordHash = "hash";
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

    private static class TestUserMapper implements UserMapper {
        @Override
        public UserEntity toEntity(UserRequestDTO dto) {
            UserEntity entity = new UserEntity();
            entity.userName = dto.getUsername();
            entity.email = dto.getEmail();
            return entity;
        }

        @Override
        public UserResponseDTO toResponseDTO(UserEntity entity) {
            UserResponseDTO dto = new UserResponseDTO();
            dto.username = entity.userName;
            dto.email = entity.email;
            return dto;
        }
    }

    private static class FakeUserRepository implements UserRepository {
        private final List<UserEntity> users = new ArrayList<>();
        private UserEntity saved;

        @Override
        public Optional<UserEntity> findByEmail(String email) {
            return users.stream().filter(user -> user.email.equals(email)).findFirst();
        }

        @Override
        public Optional<UserEntity> findOptionalById(UUID id) {
            return users.stream().filter(user -> id.equals(user.id)).findFirst();
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
            saved = user;
            users.add(user);
        }

        @Override
        public long countUsers() {
            return users.size();
        }
    }
}
