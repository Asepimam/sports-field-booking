package org.sports.field.booking.application.service;

import java.util.List;

import org.sports.field.booking.application.dto.UserResponseDTO;

import org.sports.field.booking.application.exception.DatabaseException;
import org.sports.field.booking.application.mapper.UserMapper;

import org.sports.field.booking.domain.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;

@ApplicationScoped
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;

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
