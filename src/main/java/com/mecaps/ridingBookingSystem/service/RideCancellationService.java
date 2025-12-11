package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.RideCancellation;
import com.mecaps.ridingBookingSystem.request.RideCancellationRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RideCancellationService {
    ResponseEntity<?> cancelRide(RideCancellationRequest rideCancellationRequest);

    ResponseEntity<?> getRideCancellationById(Long id);

    ResponseEntity<?> getAllRideCancellation();

    ResponseEntity<?> deleteRideCancellationById(Long id);
}
