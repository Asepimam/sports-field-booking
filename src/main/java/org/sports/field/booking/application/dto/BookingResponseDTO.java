package org.sports.field.booking.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BookingResponseDTO {
    private UUID id;

    @JsonProperty("ground_id")
    private UUID groundId;

    @JsonProperty("ground_name")
    private String groundName;

    @JsonProperty("ground_location")
    private String groundLocation;

    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonProperty("booking_date")
    private LocalDate bookingDate;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_time")
    private LocalTime endTime;

    @JsonProperty("total_price")
    private Long totalPrice;

    private String status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
