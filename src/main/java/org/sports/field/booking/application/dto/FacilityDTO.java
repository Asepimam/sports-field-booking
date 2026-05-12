package org.sports.field.booking.application.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FacilityDTO {
    private UUID id;

    @JsonProperty("ground_id")
    private UUID groundId;

    private String name;

    private String description;

    private Long price;
}
