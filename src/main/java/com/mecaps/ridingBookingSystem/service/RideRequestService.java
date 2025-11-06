package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import org.springframework.http.ResponseEntity;

<<<<<<< HEAD:src/main/java/com/mecaps/ridingBookingSystem/service/RideRequestsService.java
public interface RideRequestsService {
=======
import java.util.List;

public interface RideRequestService {
>>>>>>> 28bda28803c4689f2c8971a17ee015a7a4d1124b:src/main/java/com/mecaps/ridingBookingSystem/service/RideRequestService.java

    ResponseEntity<?> createRideRequest(RideRequestsDTO request);

    List<Driver> findNearestAvailableDrivers(RideRequestsDTO request, Integer limit);

    ResponseEntity<?> confirmRideRequest(Long id, Boolean confirmation);
}
