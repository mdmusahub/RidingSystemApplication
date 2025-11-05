package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class RideHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Rides rideId;

    private String summary;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
