package org.sports.field.booking.application.service;

import org.sports.field.booking.application.dto.ProfileResponseDTO;
import org.sports.field.booking.application.dto.UpdateUserDTO;
import org.sports.field.booking.application.exception.ConflictException;
import org.sports.field.booking.application.exception.DatabaseException;
import org.sports.field.booking.application.exception.NotFoundException;
import org.sports.field.booking.domain.entity.ProfileEntity;
import org.sports.field.booking.domain.entity.UserEntity;
import org.sports.field.booking.domain.repository.ProfileRepository;
import org.sports.field.booking.domain.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public ProfileResponseDTO getProfile(String email) {
        try {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            ProfileEntity profile = getOrCreateProfile(user);
            return toResponse(user, profile);
        } catch (NotFoundException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch profile", ex);
        }
    }

    @Override
    @Transactional
    public ProfileResponseDTO updateProfile(String email, UpdateUserDTO request) {
        try {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            ProfileEntity profile = getOrCreateProfile(user);

            if (request.getEmail() != null && !request.getEmail().equals(user.email)
                    && userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email already in use");
            }

            if (request.getUsername() != null && !request.getUsername().isBlank()) {
                user.userName = request.getUsername();
            }
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                user.email = request.getEmail();
            }
            if (request.getFullName() != null) {
                profile.fullName = request.getFullName();
            }
            if (request.getPhoneNumber() != null) {
                profile.phoneNumber = request.getPhoneNumber();
            }
            if (request.getAddress() != null) {
                profile.address = request.getAddress();
            }
            if (request.getAvatarUrl() != null) {
                profile.avatarUrl = request.getAvatarUrl();
            }

            userRepository.save(user);
            profileRepository.save(profile);
            return toResponse(user, profile);
        } catch (ConflictException | NotFoundException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to update profile", ex);
        }
    }

    private ProfileEntity getOrCreateProfile(UserEntity user) {
        return profileRepository.findByUserEmail(user.email)
                .orElseGet(() -> {
                    ProfileEntity profile = new ProfileEntity();
                    profile.user = user;
                    profileRepository.save(profile);
                    return profile;
                });
    }

    private ProfileResponseDTO toResponse(UserEntity user, ProfileEntity profile) {
        ProfileResponseDTO dto = new ProfileResponseDTO();
        dto.setId(user.id);
        dto.setUsername(user.userName);
        dto.setEmail(user.email);
        dto.setRole(user.role == null ? null : user.role.name());
        dto.setFullName(profile.fullName);
        dto.setPhoneNumber(profile.phoneNumber);
        dto.setAddress(profile.address);
        dto.setAvatarUrl(profile.avatarUrl);
        dto.setCreatedAt(profile.createdAt);
        dto.setUpdatedAt(profile.updatedAt);
        return dto;
    }
}
