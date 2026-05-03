package org.sports.field.booking.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "grounds", indexes = {
        @Index(name = "idx_ground_name", columnList = "name_ground"),
})
@Data
public class GroundEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue()
    public UUID id;

    @Column(length = 1000, nullable = false, name = "name_ground")
    public String nameGround;

    @Column(length = 1000, nullable = false, name = "location")
    public String location;

    @Column(length = 1000, nullable = false, name = "price_per_hour")
    public Long pricePerHour;

    @Column(length = 1000, nullable = false, name = "is_available", columnDefinition = "BOOLEAN DEFAULT TRUE")
    public Boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    public UserEntity owner;

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
