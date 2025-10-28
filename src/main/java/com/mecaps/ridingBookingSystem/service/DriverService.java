package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.request.DriverRequest;
import org.springframework.http.ResponseEntity;

public interface DriverService {

    ResponseEntity<?> createDriver(DriverRequest request);

    ResponseEntity<?> getDriverById(Long id);

    ResponseEntity<?> getAllDrivers();

    ResponseEntity<?> updateDriver(Long id, DriverRequest request);

    ResponseEntity<?> deleteDriver(Long id);
}
