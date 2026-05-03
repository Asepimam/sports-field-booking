package org.sports.field.booking.application.dto;

import org.sports.field.booking.application.validation.ValidBigDecimalPrice;
import org.sports.field.booking.domain.entity.SportType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.time.LocalTime;

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

    @JsonProperty("sport_type")
    @NotNull(message = "Sport type is required")
    public SportType sportType;

    @JsonProperty("open_time")
    @NotNull(message = "Open time is required")
    @JsonFormat(pattern = "HH:mm")
    public LocalTime openTime;

    @JsonProperty("close_time")
    @NotNull(message = "Close time is required")
    @JsonFormat(pattern = "HH:mm")
    public LocalTime closeTime;

    @JsonProperty("cove_image_url")
    @NotBlank(message = "URL gambar wajib diisi")
    @Pattern(regexp = "^(https?://).*", message = "Harus berupa URL yang valid")
    public String coverImageUrl;
}