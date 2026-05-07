package org.sports.field.booking.application.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.sports.field.booking.application.dto.BookingRequestDTO;
import org.sports.field.booking.application.dto.BookingResponseDTO;
import org.sports.field.booking.application.exception.DatabaseException;
import org.sports.field.booking.application.exception.InputException;
import org.sports.field.booking.application.exception.NotFoundException;
import org.sports.field.booking.domain.entity.BookingEntity;
import org.sports.field.booking.domain.entity.BookingStatus;
import org.sports.field.booking.domain.entity.GroundEntity;
import org.sports.field.booking.domain.entity.UserEntity;
import org.sports.field.booking.domain.repository.BookingRepository;
import org.sports.field.booking.domain.repository.GroundRepository;
import org.sports.field.booking.domain.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final GroundRepository groundRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, GroundRepository groundRepository,
            UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.groundRepository = groundRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BookingResponseDTO createBooking(String customerEmail, BookingRequestDTO request) {
        try {
            validateTimeRange(request);

            UserEntity customer = userRepository.findByEmail(customerEmail)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            GroundEntity ground = groundRepository.findOptionalById(request.getGroundId())
                    .orElseThrow(() -> new NotFoundException("Ground not found"));

            if (Boolean.FALSE.equals(ground.isAvailable)) {
                throw new InputException("Ground is not available");
            }

            validateInsideOperatingHours(ground, request.getStartTime(), request.getEndTime());

            if (bookingRepository.existsOverlappingBooking(
                    request.getGroundId(),
                    request.getBookingDate(),
                    request.getStartTime(),
                    request.getEndTime())) {
                throw new InputException("Selected slot is not available");
            }

            BookingEntity booking = new BookingEntity();
            booking.customer = customer;
            booking.ground = ground;
            booking.bookingDate = request.getBookingDate();
            booking.startTime = request.getStartTime();
            booking.endTime = request.getEndTime();
            booking.status = BookingStatus.PENDING;
            booking.totalPrice = calculateTotalPrice(ground.pricePerHour, request);

            bookingRepository.save(booking);
            return toResponse(booking);
        } catch (InputException | NotFoundException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to create booking", ex);
        }
    }

    @Override
    public List<BookingResponseDTO> getMyBookings(String customerEmail, int page, int size) {
        try {
            return bookingRepository.getBookingsByCustomerEmail(customerEmail, page, size)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch bookings", ex);
        }
    }

    @Override
    public long countMyBookings(String customerEmail) {
        try {
            return bookingRepository.countBookingsByCustomerEmail(customerEmail);
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to count bookings", ex);
        }
    }

    @Override
    public List<BookingResponseDTO> getOwnerBookings(String ownerEmail, int page, int size) {
        try {
            return bookingRepository.getBookingsByOwnerEmail(ownerEmail, page, size)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch owner bookings", ex);
        }
    }

    @Override
    public long countOwnerBookings(String ownerEmail) {
        try {
            return bookingRepository.countBookingsByOwnerEmail(ownerEmail);
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to count owner bookings", ex);
        }
    }

    @Override
    public List<String> getAvailableSlots(UUID groundId, LocalDate date) {
        try {
            if (date == null) {
                throw new InputException("Date is required");
            }

            GroundEntity ground = groundRepository.findOptionalById(groundId)
                    .orElseThrow(() -> new NotFoundException("Ground not found"));

            if (Boolean.FALSE.equals(ground.isAvailable)) {
                return List.of();
            }

            List<BookingEntity> bookings = bookingRepository.getBookingsByGroundAndDate(groundId, date);
            List<String> availableSlots = new ArrayList<>();

            LocalTime current = ground.openTime;
            while (current.isBefore(ground.closeTime)) {
                LocalTime slotEnd = current.plusHours(1);
                if (slotEnd.isAfter(ground.closeTime)) {
                    break;
                }

                LocalTime slotStart = current;
                boolean isBooked = bookings.stream()
                        .anyMatch(booking -> overlaps(slotStart, slotEnd, booking.startTime, booking.endTime));

                if (!isBooked) {
                    availableSlots.add(slotStart.toString());
                }

                current = slotEnd;
            }

            return availableSlots;
        } catch (InputException | NotFoundException ex) {
            throw ex;
        } catch (PersistenceException ex) {
            throw new DatabaseException("Failed to fetch available slots", ex);
        }
    }

    private void validateTimeRange(BookingRequestDTO request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InputException("End time must be after start time");
        }
    }

    private void validateInsideOperatingHours(GroundEntity ground, LocalTime startTime, LocalTime endTime) {
        if (startTime.isBefore(ground.openTime) || endTime.isAfter(ground.closeTime)) {
            throw new InputException("Booking time is outside operating hours");
        }
    }

    private boolean overlaps(LocalTime startTime, LocalTime endTime, LocalTime bookedStartTime,
            LocalTime bookedEndTime) {
        return startTime.isBefore(bookedEndTime) && endTime.isAfter(bookedStartTime);
    }

    private Long calculateTotalPrice(Long pricePerHour, BookingRequestDTO request) {
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        long hours = (long) Math.ceil(minutes / 60.0);
        return pricePerHour * Math.max(hours, 1);
    }

    private BookingResponseDTO toResponse(BookingEntity booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.id);
        dto.setGroundId(booking.ground.id);
        dto.setGroundName(booking.ground.nameGround);
        dto.setGroundLocation(booking.ground.location);
        dto.setCustomerEmail(booking.customer.email);
        dto.setBookingDate(booking.bookingDate);
        dto.setStartTime(booking.startTime);
        dto.setEndTime(booking.endTime);
        dto.setTotalPrice(booking.totalPrice);
        dto.setStatus(booking.status.name());
        dto.setCreatedAt(booking.createdAt);
        return dto;
    }
}
