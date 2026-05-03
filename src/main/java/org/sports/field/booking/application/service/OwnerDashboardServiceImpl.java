package org.sports.field.booking.application.service;

import org.sports.field.booking.application.dto.OwnerDashboardResponseDTO;
import org.sports.field.booking.application.exception.DatabaseException;
import org.sports.field.booking.domain.repository.BookingRepository;
import org.sports.field.booking.domain.repository.GroundRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;

@ApplicationScoped
public class OwnerDashboardServiceImpl implements OwnerDashboardService {
    private final GroundRepository groundRepository;
    private final BookingRepository bookingRepository;

    public OwnerDashboardServiceImpl(GroundRepository groundRepository, BookingRepository bookingRepository) {
        this.groundRepository = groundRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public OwnerDashboardResponseDTO getDashboard(String ownerEmail) {
        try {
            long totalGrounds = groundRepository.countGroundsByOwnerEmail(ownerEmail);
            long availableGrounds = groundRepository.countAvailableGroundsByOwnerEmail(ownerEmail);
            long totalBookings = bookingRepository.countBookingsByOwnerEmail(ownerEmail);
            long totalRevenue = bookingRepository.sumRevenueByOwnerEmail(ownerEmail);

            return new OwnerDashboardResponseDTO(totalGrounds, availableGrounds, totalBookings, totalRevenue);
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch owner dashboard", ex);
        }
    }
}
