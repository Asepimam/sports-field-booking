package org.sports.field.booking.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponseDTO {
    public String id;
    public String username;
    public String email;
    public LocalDateTime createdAt;
}