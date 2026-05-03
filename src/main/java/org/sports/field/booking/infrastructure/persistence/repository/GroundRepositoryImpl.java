package org.sports.field.booking.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.sports.field.booking.domain.entity.GroundEntity;
import org.sports.field.booking.domain.repository.GroundRepository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GroundRepositoryImpl implements GroundRepository, PanacheRepositoryBase<GroundEntity, UUID> {

    @Override
    public Optional<GroundEntity> findByName(String name) {
        return find("nameGround", name).firstResultOptional();
    }

    @Override
    public boolean existsByName(String name) {
        return count("nameGround", name) > 0
                || count("location", name) > 0;
    }

    @Override
    public List<GroundEntity> getGrounds(int page, int size) {
        return findAll().page(page - 1, size).list();
    }

    @Override
    public void save(GroundEntity ground) {
        persist(ground);
    }

    @Override
    public long countGrounds() {
        return count();
    }
}
