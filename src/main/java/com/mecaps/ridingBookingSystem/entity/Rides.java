package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity
public class Rides {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Driver driverId;

    @ManyToOne
    private Rider riderId;

    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private String dropoffLocation;

    private RideStatus status;

    private Double fare;

    @DateTimeFormat
    @CreationTimestamp
    private String requestedAt;

    @DateTimeFormat
    private String completedAt;
}
