package org.sports.field.booking.dto;

import org.sports.field.booking.validator.ValidBigDecimalPrice;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroundRequestDTO {
    @JsonProperty("name_ground")
    @NotBlank(message = "Ground name is required")
    public String nameGround;

    @JsonProperty("location")
    @NotBlank(message = "Location is required")
    public String location;

    @JsonProperty("price_per_hour")
    @NotNull(message = "Price per hour is required")
    @ValidBigDecimalPrice(min = 1000, max = 100000000, maxDecimalPlaces = 0)
    public Long pricePerHour;

    @JsonProperty("is_available")
    public Boolean isAvailable = true;
}