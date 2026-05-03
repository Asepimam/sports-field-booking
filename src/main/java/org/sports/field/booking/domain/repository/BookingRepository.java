package org.sports.field.booking.domain.repository;

import java.util.List;

import org.sports.field.booking.domain.entity.BookingEntity;

public interface BookingRepository {
    void save(BookingEntity booking);

    List<BookingEntity> getBookingsByCustomerEmail(String customerEmail, int page, int size);

    List<BookingEntity> getBookingsByOwnerEmail(String ownerEmail, int page, int size);

    long countBookingsByCustomerEmail(String customerEmail);

    long countBookingsByOwnerEmail(String ownerEmail);

    long sumRevenueByOwnerEmail(String ownerEmail);
}
