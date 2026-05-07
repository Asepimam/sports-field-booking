package org.sports.field.booking.application.service;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

import org.sports.field.booking.application.dto.BookingRequestDTO;
import org.sports.field.booking.application.dto.BookingResponseDTO;

public interface BookingService {
    BookingResponseDTO createBooking(String customerEmail, BookingRequestDTO request);

    List<BookingResponseDTO> getMyBookings(String customerEmail, int page, int size);

    long countMyBookings(String customerEmail);

    List<BookingResponseDTO> getOwnerBookings(String ownerEmail, int page, int size);

    long countOwnerBookings(String ownerEmail);

    List<String> getAvailableSlots(UUID groundId, LocalDate date);
}
