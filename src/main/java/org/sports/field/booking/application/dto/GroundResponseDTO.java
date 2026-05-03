package org.sports.field.booking.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class GroundResponseDTO {
    private UUID id;

    @JsonProperty("name_ground")
    private String nameGround; // Field tetap nameGround, JSON property name_ground

    @JsonProperty("location")
    private String location;

    @JsonProperty("price_per_hour")
    private Long pricePerHour;

    @JsonProperty("is_available")
    private Boolean isAvailable;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}