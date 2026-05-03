package org.sports.field.booking.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "bookings")
@Data
public class BookingEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    public UserEntity customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ground_id", nullable = false)
    public GroundEntity ground;

    @Column(nullable = false, name = "booking_date")
    public LocalDate bookingDate;

    @Column(nullable = false, name = "start_time")
    public LocalTime startTime;

    @Column(nullable = false, name = "end_time")
    public LocalTime endTime;

    @Column(nullable = false, name = "total_price")
    public Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public BookingStatus status = BookingStatus.PENDING;

    @Column(nullable = false, updatable = false, name = "created_at")
    public LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
