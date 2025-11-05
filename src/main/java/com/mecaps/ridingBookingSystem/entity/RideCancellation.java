package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
public class RideCancellation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Rides rideId;

    @Column(nullable = false)
    private String cancelledBy;

    private String reason;

    @DateTimeFormat
    @Column(nullable = false)
    private LocalDateTime cancelledAt;


}
