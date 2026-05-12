package org.sports.field.booking.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.sports.field.booking.domain.entity.FacilityEntity;
import org.sports.field.booking.domain.repository.FacilityRepository;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FacilityRepositoryImpl implements FacilityRepository {
    @Override
    public Optional<FacilityEntity> findOptionalById(UUID id) {
        return findByIdOptional(id);
    }

    @Override
    public List<FacilityEntity> findByGroundId(UUID groundId) {
        return list("ground.id", groundId);
    }

    @Override
    public void save(FacilityEntity facility) {
        persist(facility);
    }
}
