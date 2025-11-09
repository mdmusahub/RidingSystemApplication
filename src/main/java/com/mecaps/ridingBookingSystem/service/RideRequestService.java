package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface RideRequestService {

    ResponseEntity<?> createRideRequest(RideRequestsDTO request);

    ResponseEntity<?> findNearestAvailableDrivers(RideRequestsDTO request, Integer limit);

    ResponseEntity<?> confirmRideRequest(Long id, Boolean confirmation);
}
