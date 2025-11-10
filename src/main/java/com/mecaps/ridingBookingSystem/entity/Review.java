package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "rideId", nullable = false)
    private Rides rideId;

    @OneToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewerId;

    @OneToOne
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User revieweeId;

    @Column(nullable = false)
    private Integer rating;

    private String comment;

    @DateTimeFormat
    @CreationTimestamp
    private LocalDateTime createdAt;
}
