package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.request.StartRideRequest;
import org.springframework.http.ResponseEntity;

public interface RideService {
    ResponseEntity<?> startRide(StartRideRequest startRideRequest);
}
