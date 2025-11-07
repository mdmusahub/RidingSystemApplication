package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RideRequestService {

    ResponseEntity<?> createRideRequest(RideRequestsDTO request);

    ResponseEntity<?> confirmRideRequestPickup(Long rideRequestId, Boolean isConfirmed);

    ResponseEntity<?> driverRideRequestConfirmation(Long rideRequestId, Long driverId, Boolean isAccepted);
}
