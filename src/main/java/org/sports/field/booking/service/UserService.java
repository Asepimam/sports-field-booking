package org.sports.field.booking.service;

import java.util.List;
import org.sports.field.booking.dto.UserRequestDTO;
import org.sports.field.booking.dto.UserResponseDTO;
import org.sports.field.booking.mapper.UserMapper;
import org.sports.field.booking.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    PasswordService passwordService;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordService = passwordService;

    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        var userEntity = userMapper.toEntity(userRequestDTO);

        userEntity.passwordHash = passwordService.hash(userRequestDTO.getPassword());
        userRepository.persist(userEntity);

        return userMapper.toResponseDTO(userEntity);
    }

    public List<UserResponseDTO> getAllUser() {
        List<UserResponseDTO> users = userRepository.listAll()
                .stream()
                .map(userMapper::toResponseDTO)
                .toList();
        return users;
    }
}
