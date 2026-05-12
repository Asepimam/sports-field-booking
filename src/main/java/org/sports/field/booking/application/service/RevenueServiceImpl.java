package org.sports.field.booking.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sports.field.booking.application.exception.DatabaseException;
import org.sports.field.booking.domain.entity.BookingEntity;
import org.sports.field.booking.domain.entity.BookingStatus;
import org.sports.field.booking.domain.repository.BookingRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;

@ApplicationScoped
public class RevenueServiceImpl implements RevenueService {
    private final BookingRepository bookingRepository;

    public RevenueServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Map<String, Object> getOwnerRevenue(String ownerEmail, LocalDate startDate, LocalDate endDate) {
        try {
            List<BookingEntity> bookings = bookingRepository.getBookingsByOwnerEmailAndDateRange(
                    ownerEmail, startDate, endDate);

            long totalRevenue = 0;
            long confirmedRevenue = 0;
            int totalBookings = bookings.size();
            int confirmedBookings = 0;

            for (BookingEntity booking : bookings) {
                totalRevenue += booking.totalPrice;
                if (booking.status == BookingStatus.CONFIRMED) {
                    confirmedRevenue += booking.totalPrice;
                    confirmedBookings++;
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("totalRevenue", totalRevenue);
            result.put("confirmedRevenue", confirmedRevenue);
            result.put("pendingRevenue", totalRevenue - confirmedRevenue);
            result.put("totalBookings", totalBookings);
            result.put("confirmedBookings", confirmedBookings);
            result.put("pendingBookings", totalBookings - confirmedBookings);
            result.put("startDate", startDate);
            result.put("endDate", endDate);

            return result;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to calculate revenue", ex);
        }
    }

    @Override
    public Map<String, Object> getOwnerRevenueSummary(String ownerEmail) {
        try {
            // This month
            LocalDate now = LocalDate.now();
            LocalDate startOfMonth = now.withDayOfMonth(1);
            Map<String, Object> thisMonth = getOwnerRevenue(ownerEmail, startOfMonth, now);

            // Last month
            LocalDate lastMonth = now.minusMonths(1);
            LocalDate startOfLastMonth = lastMonth.withDayOfMonth(1);
            LocalDate endOfLastMonth = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
            Map<String, Object> lastMonthRevenue = getOwnerRevenue(ownerEmail, startOfLastMonth, endOfLastMonth);

            // This year
            LocalDate startOfYear = now.withDayOfYear(1);
            Map<String, Object> thisYear = getOwnerRevenue(ownerEmail, startOfYear, now);

            Map<String, Object> summary = new HashMap<>();
            summary.put("thisMonth", thisMonth);
            summary.put("lastMonth", lastMonthRevenue);
            summary.put("thisYear", thisYear);

            // Calculate growth
            long thisMonthRevenueValue = (Long) thisMonth.get("confirmedRevenue");
            long lastMonthRevenueValue = (Long) lastMonthRevenue.get("confirmedRevenue");

            double growth = 0.0;
            if (lastMonthRevenueValue > 0) {
                growth = ((double) (thisMonthRevenueValue - lastMonthRevenueValue) / lastMonthRevenueValue) * 100;
            }

            summary.put("monthlyGrowth", Math.round(growth * 100.0) / 100.0);

            return summary;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to calculate revenue summary", ex);
        }
    }
}
