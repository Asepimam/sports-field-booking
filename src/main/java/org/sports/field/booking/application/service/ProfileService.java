package org.sports.field.booking.application.service;

import org.sports.field.booking.application.dto.UpdateUserDTO;
import org.sports.field.booking.application.dto.ProfileResponseDTO;

public interface ProfileService {
    ProfileResponseDTO getProfile(String email);

    ProfileResponseDTO updateProfile(String email, UpdateUserDTO request);
}
