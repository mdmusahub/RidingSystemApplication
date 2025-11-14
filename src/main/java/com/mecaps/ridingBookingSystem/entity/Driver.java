package com.mecaps.ridingBookingSystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "userId", nullable = false)
    private User userId;

    @Column(nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private String vehicleNumber;

    private String vehicleModel;

    private Float rating;

    @JsonIgnore
    @OneToMany(mappedBy = "driverId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rides> rides = new ArrayList<>();

    @JsonIgnore
    @OneToOne (mappedBy = "driverId", cascade = CascadeType.ALL, orphanRemoval = true)
    private DriverStatus driverStatus;

    @JsonIgnore
    @OneToOne(mappedBy = "driverId", cascade = CascadeType.ALL, orphanRemoval = true)
    private Location location;

}
