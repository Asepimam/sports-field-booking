package org.sports.field.booking.application.service;

import java.util.List;

import org.sports.field.booking.application.dto.GroundRequestDTO;
import org.sports.field.booking.application.dto.GroundResponseDTO;

public interface GroundService {
    GroundResponseDTO createGround(GroundRequestDTO groundRequestDTO);

    List<GroundResponseDTO> getGrounds(int page, int size);

    long countGrounds();
}
