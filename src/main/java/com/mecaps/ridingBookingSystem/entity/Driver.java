package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    @Column(nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private String vehicleNumber;

    private String vehicleModel;

    private Float rating;

    private Boolean isAvailable;

    @OneToMany(mappedBy = "driverId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rides> rides;

    @OneToOne (mappedBy = "driverId", cascade = CascadeType.ALL, orphanRemoval = true)
    private DriverStatus driverStatus;

    @OneToOne(mappedBy = "driverId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Location location;


    @OneToOne(mappedBy = "revieweeId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;
}
