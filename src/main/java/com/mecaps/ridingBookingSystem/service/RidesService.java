package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.Rides;
import com.mecaps.ridingBookingSystem.request.CompleteRideRequest;
import com.mecaps.ridingBookingSystem.request.StartRideRequest;
import org.springframework.http.ResponseEntity;

public interface RidesService {
    ResponseEntity<?> startRide(StartRideRequest startRideRequest);

    ResponseEntity<?> completeRide(CompleteRideRequest completeRideRequest);

    Rides getRideById(Long rideId);
}