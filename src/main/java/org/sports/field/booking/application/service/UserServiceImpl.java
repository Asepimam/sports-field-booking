package org.sports.field.booking.application.service;

import java.util.List;

import org.sports.field.booking.application.dto.UserRequestDTO;
import org.sports.field.booking.application.dto.UserResponseDTO;
import org.sports.field.booking.application.exception.ConflictException;
import org.sports.field.booking.application.exception.DatabaseException;
import org.sports.field.booking.application.security.PasswordHasher;
import org.sports.field.booking.application.mapper.UserMapper;
import org.sports.field.booking.domain.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordHasher passwordHasher;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        try {
            if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
                throw new ConflictException("Email already in use");
            }

            var userEntity = userMapper.toEntity(userRequestDTO);
            userEntity.passwordHash = passwordHasher.hash(userRequestDTO.getPassword());
            userRepository.save(userEntity);

            return userMapper.toResponseDTO(userEntity);
        } catch (ConflictException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to create user", ex);
        }
    }

    @Override
    public List<UserResponseDTO> getUsers(int page, int size) {
        try {
            return userRepository.getUsers(page, size)
                    .stream()
                    .map(userMapper::toResponseDTO)
                    .toList();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch users", ex);
        }
    }

    @Override
    public long countUsers() {
        try {
            return userRepository.countUsers();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to count users", ex);
        }
    }
}
