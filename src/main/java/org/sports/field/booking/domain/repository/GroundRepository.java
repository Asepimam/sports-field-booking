package org.sports.field.booking.domain.repository;

import java.util.List;
import java.util.Optional;

import org.sports.field.booking.domain.entity.GroundEntity;

public interface GroundRepository {
    Optional<GroundEntity> findByName(String name);

    boolean existsByName(String name);

    List<GroundEntity> getGrounds(int page, int size);

    void save(GroundEntity ground);

    long countGrounds();
}
