package org.sports.field.booking.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.sports.field.booking.domain.entity.ProfileEntity;
import org.sports.field.booking.domain.repository.ProfileRepository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProfileRepositoryImpl implements ProfileRepository, PanacheRepositoryBase<ProfileEntity, UUID> {

    @Override
    public Optional<ProfileEntity> findByUserEmail(String email) {
        return find("user.email", email).firstResultOptional();
    }

    @Override
    public void save(ProfileEntity profile) {
        if (!isPersistent(profile)) {
            persist(profile);
        }
    }
}
