package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.RideCancellation;
import com.mecaps.ridingBookingSystem.request.RideCancellationRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RideCancellationService {
    ResponseEntity<?> cancelRide(RideCancellationRequest rideCancellationRequest);

    RideCancellation getRideCancellationById(Long id);

    List<RideCancellation> getAllRideCancellation();

    ResponseEntity<?> deleteRideCancellationById(Long id);
}
