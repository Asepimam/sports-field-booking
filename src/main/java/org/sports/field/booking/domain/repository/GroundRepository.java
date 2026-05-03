package org.sports.field.booking.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.sports.field.booking.domain.entity.GroundEntity;

public interface GroundRepository {
    Optional<GroundEntity> findByName(String name);

    Optional<GroundEntity> findOptionalById(UUID id);

    boolean existsByName(String name);

    List<GroundEntity> getGrounds(int page, int size);

    List<GroundEntity> getGroundsByOwnerEmail(String ownerEmail, int page, int size);

    void save(GroundEntity ground);

    long countGrounds();

    long countGroundsByOwnerEmail(String ownerEmail);

    long countAvailableGroundsByOwnerEmail(String ownerEmail);
}
