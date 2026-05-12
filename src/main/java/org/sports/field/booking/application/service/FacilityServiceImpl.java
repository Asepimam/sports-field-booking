package org.sports.field.booking.application.service;

import java.util.List;
import java.util.UUID;

import org.sports.field.booking.application.dto.FacilityDTO;
import org.sports.field.booking.application.exception.DatabaseException;
import org.sports.field.booking.application.exception.NotFoundException;
import org.sports.field.booking.application.mapper.FacilityMapper;
import org.sports.field.booking.domain.entity.FacilityEntity;
import org.sports.field.booking.domain.entity.GroundEntity;
import org.sports.field.booking.domain.repository.FacilityRepository;
import org.sports.field.booking.domain.repository.GroundRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FacilityServiceImpl implements FacilityService {
    private final FacilityRepository facilityRepository;
    private final GroundRepository groundRepository;
    private final FacilityMapper facilityMapper;

    public FacilityServiceImpl(
            FacilityRepository facilityRepository,
            GroundRepository groundRepository,
            FacilityMapper facilityMapper) {
        this.facilityRepository = facilityRepository;
        this.groundRepository = groundRepository;
        this.facilityMapper = facilityMapper;
    }

    @Override
    @Transactional
    public FacilityDTO createFacility(String ownerEmail, UUID groundId, FacilityDTO request) {
        try {
            GroundEntity ground = groundRepository.findOptionalById(groundId)
                    .orElseThrow(() -> new NotFoundException("Ground not found"));

            if (ground.owner == null || !ownerEmail.equals(ground.owner.email)) {
                throw new NotFoundException("Ground not found");
            }

            FacilityEntity facility = facilityMapper.toEntity(request);
            facility.ground = ground;

            facilityRepository.save(facility);
            return facilityMapper.toDTO(facility);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to create facility", ex);
        }
    }

    @Override
    @Transactional
    public FacilityDTO updateFacility(String ownerEmail, UUID groundId, UUID facilityId, FacilityDTO request) {
        try {
            FacilityEntity facility = facilityRepository.findOptionalById(facilityId)
                    .orElseThrow(() -> new NotFoundException("Facility not found"));

            if (!facility.ground.id.equals(groundId)) {
                throw new NotFoundException("Facility not found");
            }

            if (facility.ground.owner == null || !ownerEmail.equals(facility.ground.owner.email)) {
                throw new NotFoundException("Facility not found");
            }

            facility.name = request.getName();
            facility.description = request.getDescription();
            facility.price = request.getPrice();

            return facilityMapper.toDTO(facility);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to update facility", ex);
        }
    }

    @Override
    @Transactional
    public void deleteFacility(String ownerEmail, UUID groundId, UUID facilityId) {
        try {
            FacilityEntity facility = facilityRepository.findOptionalById(facilityId)
                    .orElseThrow(() -> new NotFoundException("Facility not found"));

            if (!facility.ground.id.equals(groundId)) {
                throw new NotFoundException("Facility not found");
            }

            if (facility.ground.owner == null || !ownerEmail.equals(facility.ground.owner.email)) {
                throw new NotFoundException("Facility not found");
            }

            facilityRepository.delete(facility);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to delete facility", ex);
        }
    }

    @Override
    public List<FacilityDTO> getGroundFacilities(UUID groundId) {
        try {
            return facilityRepository.findByGroundId(groundId)
                    .stream()
                    .map(facilityMapper::toDTO)
                    .toList();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch facilities", ex);
        }
    }
}
