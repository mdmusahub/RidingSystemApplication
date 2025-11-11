package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.request.RideCancellationRequest;
import org.springframework.http.ResponseEntity;

public interface RideCancellationService {
    ResponseEntity<?> cancelRide(RideCancellationRequest rideCancellationRequest);
}
