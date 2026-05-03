package org.sports.field.booking.domain.repository;

import java.util.Optional;

import org.sports.field.booking.domain.entity.ProfileEntity;

public interface ProfileRepository {
    Optional<ProfileEntity> findByUserEmail(String email);

    void save(ProfileEntity profile);
}
