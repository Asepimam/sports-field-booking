package org.sports.field.booking.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "facilities")
@Data
public class FacilityEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ground_id", nullable = false)
    public GroundEntity ground;

    @Column(nullable = false, length = 100, name = "name")
    public String name;

    @Column(length = 500, name = "description")
    public String description;

    @Column(nullable = false, name = "price", columnDefinition = "BIGINT DEFAULT 0")
    public Long price = 0L; // Harga tambahan untuk fasilitas ini

    @Column(nullable = false, updatable = false, name = "created_at")
    public LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (price == null) {
            price = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
