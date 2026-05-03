package org.sports.field.booking.application.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponseDTO {
    public String id;
    public String username;
    public String email;
    public String role;
    public LocalDateTime createdAt;
}
