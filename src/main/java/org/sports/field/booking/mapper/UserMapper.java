package org.sports.field.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.sports.field.booking.dto.UserRequestDTO;
import org.sports.field.booking.dto.UserResponseDTO;
import org.sports.field.booking.entity.UserEntity;

@Mapper(config = QuarkusMappingConfig.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "userName", source = "username")
    UserEntity toEntity(UserRequestDTO dto);

    @Mapping(target = "username", source = "userName")
    UserResponseDTO toResponseDTO(UserEntity entity);
}