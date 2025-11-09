package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class RideRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rider_id", nullable = false)
    private Rider riderId;

    @OneToOne
    @JoinColumn(name = "assigned_driver_id")
    private Driver assignedDriver;

    private Double pickupLat;

    private Double pickupLng;

    private Double dropLat;

    private Double dropLng;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @DateTimeFormat
    @CreationTimestamp
    private String requestedAt;

    @DateTimeFormat
    private String expiresAt;

    @Transient
    private List<Driver> availableDriver = new ArrayList<>();

    @OneToOne(mappedBy = "requestsId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Rides requestId;
}
