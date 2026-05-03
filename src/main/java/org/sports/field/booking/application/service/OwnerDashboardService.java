package org.sports.field.booking.application.service;

import org.sports.field.booking.application.dto.OwnerDashboardResponseDTO;

public interface OwnerDashboardService {
    OwnerDashboardResponseDTO getDashboard(String ownerEmail);
}
