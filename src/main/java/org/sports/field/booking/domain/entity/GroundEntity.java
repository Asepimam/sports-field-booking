package org.sports.field.booking.domain.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "grounds", indexes = {
        @Index(name = "idx_ground_name", columnList = "name_ground"),
        @Index(name = "idx_sport_type", columnList = "sport_type"),
        @Index(name = "idx_rating", columnList = "rating"), // Index untuk rating
        @Index(name = "idx_operating_hours", columnList = "open_time, close_time") // Index untuk operating hours
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

    @Column(nullable = false, name = "sport_type")
    @Enumerated(EnumType.STRING)
    public SportType sportType;

    @Column(name = "open_time", nullable = false)
    public LocalTime openTime; // Jam buka (contoh: 08:00)

    @Column(name = "close_time", nullable = false)
    public LocalTime closeTime; // Jam tutup (contoh: 22:00)

    @Column(name = "rating", columnDefinition = "DECIMAL(3,2) DEFAULT 0.00")
    public Double rating; // Rating rata-rata (0.00 - 5.00)

    @Column(name = "total_reviews", columnDefinition = "INTEGER DEFAULT 0")
    public Integer totalReviews; // Total jumlah review

    @Column(name = "rating_sum", columnDefinition = "DECIMAL(10,2) DEFAULT 0.00")
    public Double ratingSum; // Jumlah semua rating (untuk perhitungan rata-rata)

    @Column(name = "cover_image_url", length = 500)
    public String coverImageUrl;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    public UserEntity owner;

    @OneToMany(mappedBy = "ground", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    public List<FacilityEntity> facilities = new ArrayList<>();

    @Column(nullable = false, updatable = false, name = "created_at")
    public LocalDateTime createdAt;

    @Column(nullable = false, name = "updated_at")
    public LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;

        // Set default values
        if (rating == null)
            rating = 0.0;
        if (totalReviews == null)
            totalReviews = 0;
        if (ratingSum == null)
            ratingSum = 0.0;
        if (openTime == null)
            openTime = LocalTime.of(8, 0); // Default 08:00
        if (closeTime == null)
            closeTime = LocalTime.of(22, 0); // Default 22:00
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Method untuk menambahkan rating baru
    public void addRating(Double newRating) {
        if (newRating == null || newRating < 0 || newRating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        if (this.ratingSum == null)
            this.ratingSum = 0.0;
        if (this.totalReviews == null)
            this.totalReviews = 0;

        this.ratingSum += newRating;
        this.totalReviews++;
        this.rating = this.ratingSum / this.totalReviews;
    }

    // Method untuk update rating (jika review di-edit)
    public void updateRating(Double oldRating, Double newRating) {
        if (oldRating == null || newRating == null)
            return;

        if (this.ratingSum == null)
            this.ratingSum = 0.0;
        if (this.totalReviews == null)
            this.totalReviews = 0;

        this.ratingSum = this.ratingSum - oldRating + newRating;
        this.rating = this.ratingSum / this.totalReviews;
    }

    // Method untuk menghapus rating
    public void removeRating(Double oldRating) {
        if (oldRating == null)
            return;

        if (this.ratingSum == null)
            this.ratingSum = 0.0;
        if (this.totalReviews == null)
            this.totalReviews = 0;

        this.ratingSum -= oldRating;
        this.totalReviews--;

        if (this.totalReviews > 0) {
            this.rating = this.ratingSum / this.totalReviews;
        } else {
            this.rating = 0.0;
            this.ratingSum = 0.0;
        }
    }

    // Method untuk mengecek apakah ground buka di jam tertentu
    public boolean isOpenAt(LocalTime time) {
        if (openTime == null || closeTime == null || time == null)
            return false;

        // Handle jika buka melewati tengah malam (misal: 20:00 - 02:00)
        if (closeTime.isBefore(openTime)) {
            return time.isAfter(openTime) || time.isBefore(closeTime);
        } else {
            return !time.isBefore(openTime) && !time.isAfter(closeTime);
        }
    }

    // Method untuk mengecek apakah ground buka sekarang
    public boolean isOpenNow() {
        return isOpenAt(LocalTime.now());
    }
}