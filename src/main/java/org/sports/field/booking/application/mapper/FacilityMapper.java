package org.sports.field.booking.application.mapper;

import org.mapstruct.Mapper;
import org.sports.field.booking.application.dto.FacilityDTO;
import org.sports.field.booking.domain.entity.FacilityEntity;

@Mapper(config = QuarkusMappingConfig.class)
public interface FacilityMapper {
    FacilityDTO toDTO(FacilityEntity entity);

    FacilityEntity toEntity(FacilityDTO dto);
}
