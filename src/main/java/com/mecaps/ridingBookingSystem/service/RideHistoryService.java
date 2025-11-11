package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.RideHistory;
import com.mecaps.ridingBookingSystem.entity.Rides;
import org.springframework.http.ResponseEntity;

public interface RideHistoryService {

    ResponseEntity<?> getRideHistoryById(Long id);

    ResponseEntity<?> getAllRidesHistory();

    RideHistory createRideHistory(Rides ride);

    ResponseEntity<?> deleteRideHistoryById(Long id);
}
