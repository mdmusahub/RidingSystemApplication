package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.request.RideAcceptanceRequestDTO;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface RideRequestService {

    Map<String,Object> getRideFareAndDistance(RideRequestsDTO request);

    ResponseEntity<?> createRideRequest(RideRequestsDTO request);

    ResponseEntity<?> acceptRideRequest(RideAcceptanceRequestDTO requestDTO);
}
