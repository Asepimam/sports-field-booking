package org.sports.field.booking.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

// Entity untuk gambar
@Entity
@Table(name = "ground_images")
@Data
public class GroundImageEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public UUID id;

    @Column(name = "ground_id", nullable = false)
    public UUID groundId;

    @Column(name = "image_url", length = 500, nullable = false)
    public String imageUrl;

    @Column(name = "is_primary")
    public Boolean isPrimary = false; // Jadi gambar utama

    @Column(name = "sort_order")
    public Integer sortOrder = 0; // Urutan tampil

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}