package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(nullable = false)
    private Double fare;

    @Column(nullable = false)
    private Double distanceKm;

    private Float driverRating;

    private Float riderRating;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private RideStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @OneToOne(mappedBy = "rideId")
    private Review review;

    @OneToOne(mappedBy = "rideId")
    private Payment payment;

}
