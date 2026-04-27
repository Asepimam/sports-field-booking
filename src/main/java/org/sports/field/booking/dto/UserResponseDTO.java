package org.sports.field.booking.dto;

import java.time.LocalDateTime;

import lombok.Data;


@Data
public class UserResponseDTO {
    private String id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}