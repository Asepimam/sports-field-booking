package org.sports.field.booking.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OwnerDashboardResponseDTO {
    @JsonProperty("total_grounds")
    private long totalGrounds;

    @JsonProperty("available_grounds")
    private long availableGrounds;

    @JsonProperty("total_bookings")
    private long totalBookings;

    @JsonProperty("total_revenue")
    private long totalRevenue;
}
