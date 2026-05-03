package org.sports.field.booking.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class RatingRequestDTO {
    @JsonProperty("ground_id")
    @NotNull(message = "Ground ID is required")
    public UUID groundId;

    @JsonProperty("rating")
    @NotNull(message = "Rating is required")
    @DecimalMin(value = "0.0", message = "Rating must be at least 0")
    @DecimalMax(value = "5.0", message = "Rating must be at most 5")
    public Double rating;

    @JsonProperty("review_text")
    public String reviewText;
}