package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
public class Rides {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driverId;

    @ManyToOne
    @JoinColumn (name = "rider_id", nullable = false)
    private Rider riderId;

    @OneToOne
    @JoinColumn(name = "request_id", nullable = false)
    private RideRequests requestsId;

    private Double fare;

    private Double distanceKm;

    private Integer driverRating;

    private Integer riderRating;

    @Column(length = 50)
    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime startTime;

    private LocalDateTime endTime;


    @OneToOne(mappedBy = "rideId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

}
