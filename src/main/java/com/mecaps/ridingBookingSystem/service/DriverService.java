package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.request.DriverRequest;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import com.mecaps.ridingBookingSystem.response.DriverResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DriverService {

    ResponseEntity<?> createDriver(DriverRequest request);

    ResponseEntity<?> getDriverById(Long id);

    ResponseEntity<?> getAllDrivers();

    ResponseEntity<?> updateDriver(Long id, DriverRequest request);

    ResponseEntity<?> deleteDriver(Long id);

    List<DriverResponse> findNearestAvailableDrivers(RideRequestsDTO request, Integer limit);
}
