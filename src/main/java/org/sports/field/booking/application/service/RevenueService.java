package org.sports.field.booking.application.service;

import java.time.LocalDate;
import java.util.Map;

public interface RevenueService {
    Map<String, Object> getOwnerRevenue(String ownerEmail, LocalDate startDate, LocalDate endDate);

    Map<String, Object> getOwnerRevenueSummary(String ownerEmail);
}
