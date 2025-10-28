package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User userId;

    @Column(nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private String vehicleNumber;

    private String vehicleModel;

    private Float rating;

    private Boolean isAvailable;
}
