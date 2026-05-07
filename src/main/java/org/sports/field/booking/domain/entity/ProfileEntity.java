package org.sports.field.booking.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "profiles")
@Data
public class ProfileEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue
    public UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    public UserEntity user;

    @Column(name = "full_name")
    public String fullName;

    @Column(name = "phone_number")
    public String phoneNumber;

    @Column(length = 1000)
    public String address;

    @Column(name = "avatar_url", length = 1000)
    public String avatarUrl;

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

    public String getUsername() {
        return user != null ? user.userName : null;
    }

    public String getUserEmail() {
        return user != null ? user.email : null;
    }

    public Role getUserRole() {
        return user != null ? user.role : null;
    }
}
