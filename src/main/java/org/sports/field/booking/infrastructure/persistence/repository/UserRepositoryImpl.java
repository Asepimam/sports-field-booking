package org.sports.field.booking.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.sports.field.booking.domain.entity.UserEntity;
import org.sports.field.booking.domain.repository.UserRepository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, PanacheRepositoryBase<UserEntity, UUID> {

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    @Override
    public Optional<UserEntity> findOptionalById(UUID id) {
        return findByIdOptional(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }

    @Override
    public List<UserEntity> getUsers(int page, int size) {
        return findAll().page(page - 1, size).list();
    }

    @Override
    public void save(UserEntity user) {
        persist(user);
    }

    @Override
    public long countUsers() {
        return count();
    }
}
