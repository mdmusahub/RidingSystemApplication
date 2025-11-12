package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @OneToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Rides rideId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private String transactionId;

    @DateTimeFormat
    @CreationTimestamp
    private LocalDateTime createdAt;

}
