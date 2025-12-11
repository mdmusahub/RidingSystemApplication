package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.RideHistory;
import com.mecaps.ridingBookingSystem.entity.Rides;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;

public interface RideHistoryService {

    ResponseEntity<?> getRideHistoryById(Long id);

    ResponseEntity<?> getAllRideHistory();

    ResponseEntity<?> getAllRidesHistoryForRider(Long riderId);

    ResponseEntity<?> getAllRidesHistoryForDriver(Long driverId);

    RideHistory createRideHistory(Rides ride);

    ResponseEntity<?> deleteRideHistoryById(Long id);
}
