package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import org.springframework.http.ResponseEntity;

public interface RideRequestService {

    ResponseEntity<?> createRideRequest(RideRequestsDTO request);
}
