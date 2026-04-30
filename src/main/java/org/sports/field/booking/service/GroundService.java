package org.sports.field.booking.service;

import java.util.List;

import org.sports.field.booking.dto.GroundRequestDTO;
import org.sports.field.booking.dto.GroundResponseDTO;
import org.sports.field.booking.entity.GroundEntity;
import org.sports.field.booking.mapper.GroundMapper;
import org.sports.field.booking.repository.GroundRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GroundService {
    private final GroundRepository groundRepository;
    private final GroundMapper groundMapper;

    public GroundService(GroundMapper groundMapper, GroundRepository groundRepository) {
        this.groundMapper = groundMapper;
        this.groundRepository = groundRepository;
    }

    @Transactional
    public GroundResponseDTO createGround(GroundRequestDTO groundRequestDTO) {
        if (groundRepository.existsByName(groundRequestDTO.getNameGround())) {
            throw new IllegalArgumentException("Name Ground Ready exist");
        }

        var groundEntity = groundMapper.toEntity(groundRequestDTO);
        GroundEntity entity = groundMapper.toEntity(groundRequestDTO);
        if (entity.isAvailable == null) {
            entity.isAvailable = true; // Set default true
        }
        groundRepository.persist(groundEntity);
        return groundMapper.toResponseDTO(groundEntity);
    };

    public List<GroundResponseDTO> getGrounds(int page, int size) {
        List<GroundResponseDTO> grounds = groundRepository.getGrounds(page, size)
                .stream()
                .map(groundMapper::toResponseDTO)
                .toList();

        return grounds;

    }

    public long countGrounds() {
        return groundRepository.count();
    }

}
