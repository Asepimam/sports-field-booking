package org.sports.field.booking.application.service;

import java.util.List;

import org.sports.field.booking.application.dto.UserRequestDTO;
import org.sports.field.booking.application.dto.UserResponseDTO;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);

    List<UserResponseDTO> getUsers(int page, int size);

    long countUsers();
}
