package org.sports.field.booking.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "users",indexes = {
    @Index(name = "idx_users_email", columnList = "email")
})
public class UserEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue()
    public UUID id;

    @Column(nullable = false, unique = true)
    public String userName;

    @Column(nullable = false, unique = true)
    public String email;


    @Column(nullable = false)
    public String passwordHash;

    @Column(nullable = false, updatable = false)
    public LocalDateTime createdAt;
   
    @Column(nullable = false)
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
