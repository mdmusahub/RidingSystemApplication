package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.request.DriverRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DriverService {

    ResponseEntity<?> createDriver(DriverRequest request);

    ResponseEntity<?> getDriverById(Long id);

    ResponseEntity<?> getAllDrivers();

    ResponseEntity<?> updateDriver(Long id, DriverRequest request);

    ResponseEntity<?> deleteDriver(Long id);

    ResponseEntity<List<RideRequests>> getAvailableRideRequests();
}
