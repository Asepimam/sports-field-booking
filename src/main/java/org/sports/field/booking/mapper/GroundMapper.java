package org.sports.field.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.sports.field.booking.dto.GroundRequestDTO;
import org.sports.field.booking.dto.GroundResponseDTO;
import org.sports.field.booking.entity.GroundEntity;

@Mapper(config = QuarkusMappingConfig.class)
public interface GroundMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // Tidak perlu @Mapping untuk nameGround, location, pricePerHour, isAvailable
    // Karena nama field di DTO dan Entity SAMA PERSIS (nameGround, location,
    // pricePerHour, isAvailable)
    GroundEntity toEntity(GroundRequestDTO dto);

    // Tidak perlu @Mapping apapun untuk response karena nama field juga sama
    GroundResponseDTO toResponseDTO(GroundEntity entity);
}