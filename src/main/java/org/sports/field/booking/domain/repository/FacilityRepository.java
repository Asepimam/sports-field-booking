package org.sports.field.booking.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.sports.field.booking.domain.entity.FacilityEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

public interface FacilityRepository extends PanacheRepositoryBase<FacilityEntity, UUID> {
    Optional<FacilityEntity> findOptionalById(UUID id);

    List<FacilityEntity> findByGroundId(UUID groundId);

    void save(FacilityEntity facility);
}
