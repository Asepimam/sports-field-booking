package org.sports.field.booking.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequestDTO {
    @JsonProperty("ground_id")
    @NotNull(message = "Ground id is required")
    private UUID groundId;

    @JsonProperty("booking_date")
    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date must be today or in the future")
    private LocalDate bookingDate;

    @JsonProperty("start_time")
    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @JsonProperty("end_time")
    @NotNull(message = "End time is required")
    private LocalTime endTime;
}
