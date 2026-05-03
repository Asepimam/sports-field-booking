package org.sports.field.booking.domain.repository;

import java.util.List;
import java.util.Optional;

import org.sports.field.booking.domain.entity.UserEntity;

public interface UserRepository {
    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    List<UserEntity> getUsers(int page, int size);

    void save(UserEntity user);

    long countUsers();
}
