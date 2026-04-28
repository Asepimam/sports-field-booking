package org.sports.field.booking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.sports.field.booking.entity.GroundEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GroundRepository implements PanacheRepositoryBase<GroundEntity, UUID> {

    public Optional<GroundEntity> findByName(String name) {
        return find("name_ground", name).firstResultOptional();
    }

    public boolean existsByName(String name) {
        return count("name_ground", name) > 0
                || count("location", name) > 0;
    }

    public List<GroundEntity> getGrounds(int page, int size) {
        return findAll().page(page - 1, size).list();
    }
}
