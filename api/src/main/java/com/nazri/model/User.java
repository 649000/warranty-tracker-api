package com.nazri.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(unique = true, nullable = false)
    public String firebaseUid;
    
    @Column(nullable = false)
    public String email;
    
    @Column(name = "display_name")
    public String displayName;
    
    @Column
    public String photoUrl;
    
    @Column(name = "created_at")
    public java.time.LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    public java.time.LocalDateTime updatedAt;
    
    // Default constructor
    public User() {}
    
    // Constructor with required fields
    public User(String firebaseUid, String email) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = java.time.LocalDateTime.now();
    }
    
    // Constructor with all fields
    public User(String firebaseUid, String email, String displayName, String photoUrl) {
        this(firebaseUid, email);
        this.displayName = displayName;
        this.photoUrl = photoUrl;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firebaseUid='" + firebaseUid + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
