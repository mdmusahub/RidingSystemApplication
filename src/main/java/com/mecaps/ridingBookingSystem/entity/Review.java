package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "rideId", nullable = false)
    private Rides rideId;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewerId;

    @ManyToOne
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User revieweeId;

    @Column(nullable = false)
    private Integer rating;

    private String comment;

    @DateTimeFormat
    @CreationTimestamp
    private LocalDateTime createdAt;
}
