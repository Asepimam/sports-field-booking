package org.sports.field.booking.repository;

import java.util.Optional;
import java.util.UUID;

import org.sports.field.booking.entity.UserEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<UserEntity, UUID> {

    public Optional<UserEntity> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }

    public java.util.List<UserEntity> getUsers(int page, int size) {
        return findAll().page(page - 1, size).list();
    }
}