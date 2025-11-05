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
<<<<<<< HEAD
=======

>>>>>>> aea2ff395cbb328eddb5b786af2766e0c0962fc0
    @ManyToOne
    private Rides rideId;

    @Column(nullable = false)
    private String cancelledBy;

    private String reason;

    @DateTimeFormat
    @Column(nullable = false)
    private LocalDateTime cancelledAt;


}
