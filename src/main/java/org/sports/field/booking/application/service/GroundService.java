package org.sports.field.booking.application.service;

import java.util.List;

import org.sports.field.booking.application.dto.GroundRequestDTO;
import org.sports.field.booking.application.dto.GroundResponseDTO;

public interface GroundService {
    GroundResponseDTO createGround(String ownerEmail, GroundRequestDTO groundRequestDTO);

    List<GroundResponseDTO> getGrounds(int page, int size);

    long countGrounds();

    List<GroundResponseDTO> getOwnerGrounds(String ownerEmail, int page, int size);

    long countOwnerGrounds(String ownerEmail);

    // Public methods untuk landing page
    List<GroundResponseDTO> getPublicGrounds(int page, int size, String sortBy, String order);

    long countPublicGrounds();

    List<GroundResponseDTO> getFeaturedGrounds(int limit);

    List<GroundResponseDTO> searchPublicGrounds(String keyword, String sportType, String location,
            Double minPrice, Double maxPrice, int page, int size);

    long countSearchPublicGrounds(String keyword, String sportType, String location,
            Double minPrice, Double maxPrice);
}