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
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Rides rideId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;


    @DateTimeFormat
    @CreationTimestamp
    private LocalDateTime createdAt;

    // optional legacy field if you used transactionId earlier
    private String transactionId;


    // store razorpay order id (created when order is created) - DO NOT overwrite
    private String razorpayOrderId;

    // store razorpay payment id (set after successful payment/verification)
    private String razorpayPaymentId;


}
