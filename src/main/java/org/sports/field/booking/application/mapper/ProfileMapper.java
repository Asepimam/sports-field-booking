package org.sports.field.booking.application.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.sports.field.booking.application.dto.ProfileResponseDTO;
import org.sports.field.booking.application.dto.UserRequestDTO;
import org.sports.field.booking.domain.entity.ProfileEntity;
import org.sports.field.booking.domain.entity.UserEntity;

@Mapper(config = QuarkusMappingConfig.class)
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "avatarUrl", ignore = true)
    ProfileEntity toEntity(UserRequestDTO dto);

    @AfterMapping
    default void setFullName(@MappingTarget ProfileEntity entity, UserRequestDTO dto) {
        if (dto.getFirstName() != null && dto.getLastName() != null) {
            entity.fullName = dto.getFirstName() + " " + dto.getLastName();
        } else if (dto.getFirstName() != null) {
            entity.fullName = dto.getFirstName();
        } else if (dto.getLastName() != null) {
            entity.fullName = dto.getLastName();
        }
    }

    @Mapping(target = "username", source = "user.userName")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "role", source = "user.role")
    @Mapping(target = "fullName", source = "entity.fullName")
    @Mapping(target = "phoneNumber", source = "entity.phoneNumber")
    @Mapping(target = "address", source = "entity.address")
    @Mapping(target = "avatarUrl", source = "entity.avatarUrl")
    @Mapping(target = "createdAt", source = "entity.createdAt")
    @Mapping(target = "updatedAt", source = "entity.updatedAt")
    @Mapping(target = "id", source = "entity.id")
    ProfileResponseDTO toResponseDTO(ProfileEntity entity, UserEntity user);
}
