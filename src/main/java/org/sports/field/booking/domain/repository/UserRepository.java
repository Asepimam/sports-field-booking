package org.sports.field.booking.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.sports.field.booking.domain.entity.UserEntity;

public interface UserRepository {
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findOptionalById(UUID id);

    boolean existsByEmail(String email);

    List<UserEntity> getUsers(int page, int size);

    void save(UserEntity user);

    long countUsers();
}
