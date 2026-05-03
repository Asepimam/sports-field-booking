package org.sports.field.booking.application.service;

import java.util.List;

import org.sports.field.booking.application.dto.GroundRequestDTO;
import org.sports.field.booking.application.dto.GroundResponseDTO;
import org.sports.field.booking.application.exception.ConflictException;
import org.sports.field.booking.application.exception.DatabaseException;
import org.sports.field.booking.application.mapper.GroundMapper;
import org.sports.field.booking.domain.repository.GroundRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GroundServiceImpl implements GroundService {
    private final GroundRepository groundRepository;
    private final GroundMapper groundMapper;

    public GroundServiceImpl(GroundMapper groundMapper, GroundRepository groundRepository) {
        this.groundMapper = groundMapper;
        this.groundRepository = groundRepository;
    }

    @Override
    @Transactional
    public GroundResponseDTO createGround(GroundRequestDTO groundRequestDTO) {
        try {
            if (groundRepository.existsByName(groundRequestDTO.getNameGround())) {
                throw new ConflictException("Ground name or location already exists");
            }

            var groundEntity = groundMapper.toEntity(groundRequestDTO);
            if (groundEntity.isAvailable == null) {
                groundEntity.isAvailable = true;
            }

            groundRepository.save(groundEntity);
            return groundMapper.toResponseDTO(groundEntity);
        } catch (ConflictException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to create ground", ex);
        }
    }

    @Override
    public List<GroundResponseDTO> getGrounds(int page, int size) {
        try {
            return groundRepository.getGrounds(page, size)
                    .stream()
                    .map(groundMapper::toResponseDTO)
                    .toList();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch grounds", ex);
        }
    }

    @Override
    public long countGrounds() {
        try {
            return groundRepository.countGrounds();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to count grounds", ex);
        }
    }
}
