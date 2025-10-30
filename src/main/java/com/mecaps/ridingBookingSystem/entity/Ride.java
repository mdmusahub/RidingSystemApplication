package com.mecaps.ridingBookingSystem.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Rider riderId;

    private Driver driverId;

    private String pickupLocation;

    private String dropoffLocation;


}
