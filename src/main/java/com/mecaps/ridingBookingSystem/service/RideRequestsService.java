package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import org.springframework.http.ResponseEntity;

public interface RideRequestsService {

    ResponseEntity<?> createRideRequest(RideRequestsDTO request);
}
