package org.sports.field.booking.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.sports.field.booking.application.dto.GroundRequestDTO;
import org.sports.field.booking.application.dto.GroundResponseDTO;
import org.sports.field.booking.domain.entity.GroundEntity;
import org.sports.field.booking.application.exception.ConflictException;
import org.sports.field.booking.application.mapper.GroundMapper;
import org.sports.field.booking.domain.repository.GroundRepository;
import org.sports.field.booking.domain.entity.UserEntity;
import org.sports.field.booking.domain.repository.UserRepository;

class GroundServiceImplTest {

    @Test
    void createGroundDefaultsAvailabilityAndSavesGround() {
        FakeGroundRepository repository = new FakeGroundRepository();
        GroundService service = new GroundServiceImpl(new TestGroundMapper(), repository, new FakeUserRepository());

        GroundRequestDTO request = groundRequest("Court A");
        request.isAvailable = null;
        GroundResponseDTO response = service.createGround("owner@example.com", request);

        assertEquals("Court A", response.getNameGround());
        assertTrue(repository.saved.isAvailable);
        assertEquals("owner@example.com", repository.saved.owner.email);
    }

    @Test
    void createGroundRejectsDuplicateName() {
        FakeGroundRepository repository = new FakeGroundRepository();
        repository.grounds.add(ground("Court A"));
        GroundService service = new GroundServiceImpl(new TestGroundMapper(), repository, new FakeUserRepository());

        assertThrows(ConflictException.class, () -> service.createGround("owner@example.com", groundRequest("Court A")));
    }

    @Test
    void getGroundsReturnsMappedGrounds() {
        FakeGroundRepository repository = new FakeGroundRepository();
        repository.grounds.add(ground("Court A"));
        repository.grounds.add(ground("Court B"));
        GroundService service = new GroundServiceImpl(new TestGroundMapper(), repository, new FakeUserRepository());

        assertEquals(2, service.getGrounds(1, 10).size());
        assertEquals(2, service.countGrounds());
    }

    private static GroundRequestDTO groundRequest(String name) {
        GroundRequestDTO dto = new GroundRequestDTO();
        dto.nameGround = name;
        dto.location = "Jakarta";
        dto.pricePerHour = 100_000L;
        dto.isAvailable = true;
        return dto;
    }

    private static GroundEntity ground(String name) {
        GroundEntity entity = new GroundEntity();
        entity.nameGround = name;
        entity.location = "Jakarta";
        entity.pricePerHour = 100_000L;
        entity.isAvailable = true;
        entity.owner = owner("owner@example.com");
        return entity;
    }

    private static UserEntity owner(String email) {
        UserEntity entity = new UserEntity();
        entity.email = email;
        entity.userName = "owner";
        return entity;
    }

    private static class TestGroundMapper implements GroundMapper {
        @Override
        public GroundEntity toEntity(GroundRequestDTO dto) {
            GroundEntity entity = new GroundEntity();
            entity.nameGround = dto.nameGround;
            entity.location = dto.location;
            entity.pricePerHour = dto.pricePerHour;
            entity.isAvailable = dto.isAvailable;
            return entity;
        }

        @Override
        public GroundResponseDTO toResponseDTO(GroundEntity entity) {
            GroundResponseDTO dto = new GroundResponseDTO();
            dto.setNameGround(entity.nameGround);
            dto.setLocation(entity.location);
            dto.setPricePerHour(entity.pricePerHour);
            dto.setIsAvailable(entity.isAvailable);
            return dto;
        }
    }

    private static class FakeGroundRepository implements GroundRepository {
        private final List<GroundEntity> grounds = new ArrayList<>();
        private GroundEntity saved;

        @Override
        public Optional<GroundEntity> findByName(String name) {
            return grounds.stream().filter(ground -> ground.nameGround.equals(name)).findFirst();
        }

        @Override
        public Optional<GroundEntity> findOptionalById(UUID id) {
            return grounds.stream().filter(ground -> id.equals(ground.id)).findFirst();
        }

        @Override
        public boolean existsByName(String name) {
            return findByName(name).isPresent();
        }

        @Override
        public List<GroundEntity> getGrounds(int page, int size) {
            return grounds;
        }

        @Override
        public List<GroundEntity> getGroundsByOwnerEmail(String ownerEmail, int page, int size) {
            return grounds.stream().filter(ground -> ground.owner != null && ownerEmail.equals(ground.owner.email)).toList();
        }

        @Override
        public void save(GroundEntity ground) {
            saved = ground;
            grounds.add(ground);
        }

        @Override
        public long countGrounds() {
            return grounds.size();
        }

        @Override
        public long countGroundsByOwnerEmail(String ownerEmail) {
            return getGroundsByOwnerEmail(ownerEmail, 1, 10).size();
        }

        @Override
        public long countAvailableGroundsByOwnerEmail(String ownerEmail) {
            return getGroundsByOwnerEmail(ownerEmail, 1, 10).stream()
                    .filter(ground -> Boolean.TRUE.equals(ground.isAvailable))
                    .count();
        }
    }

    private static class FakeUserRepository implements UserRepository {

        @Override
        public Optional<UserEntity> findByEmail(String email) {
            return Optional.of(owner(email));
        }

        @Override
        public Optional<UserEntity> findOptionalById(UUID id) {
            return Optional.empty();
        }

        @Override
        public boolean existsByEmail(String email) {
            return false;
        }

        @Override
        public List<UserEntity> getUsers(int page, int size) {
            return List.of();
        }

        @Override
        public void save(UserEntity user) {
        }

        @Override
        public long countUsers() {
            return 0;
        }
    }
}
