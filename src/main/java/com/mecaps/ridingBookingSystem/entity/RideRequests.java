package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Data
public class RideRequests {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Rider riderId;

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

    @OneToOne(mappedBy = "requestsId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Rides requestId;
}
