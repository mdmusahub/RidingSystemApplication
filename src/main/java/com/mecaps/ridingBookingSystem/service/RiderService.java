package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.request.RiderRequest;
import org.springframework.http.ResponseEntity;

public interface RiderService {

    ResponseEntity<?> createRider(RiderRequest request);

    ResponseEntity<?> getRiderById(Long id);

    ResponseEntity<?> getAllRiders();

    ResponseEntity<?> deleteRider(Long id);
}
