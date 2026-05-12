package org.sports.field.booking.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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

    @JsonProperty("sport_type")
    public String sportType;

    @JsonProperty("open_time")
    public LocalTime openTime;

    @JsonProperty("close_time")
    public LocalTime closeTime;

    @JsonProperty("rating")
    public Double rating;

    @JsonProperty("total_reviews")
    public Integer totalReviews;

    @JsonProperty("is_open_now")
    public Boolean isOpenNow;

    @JsonProperty("cove_image_url")
    public String coverImageUrl;

    @JsonProperty("facilities")
    public List<FacilityDTO> facilities = new ArrayList<>();

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}