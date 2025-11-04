package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.time.LocalDateTime;

@Entity
@Data
public class DriverStatus {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driverId;

    private Boolean isOnline;

    private Boolean isAvailable;

    @CreationTimestamp
    private LocalDateTime lastActive;
}
