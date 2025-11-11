package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.repository.RideCancellationRepository;
import com.mecaps.ridingBookingSystem.request.RideCancellationRequest;
import com.mecaps.ridingBookingSystem.service.RideCancellationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RideCancellationServiceImpl implements RideCancellationService {
    private final RideCancellationRepository rideCancellationRepository;

    public RideCancellationServiceImpl(RideCancellationRepository rideCancellationRepository) {
        this.rideCancellationRepository = rideCancellationRepository;
    }

    @Override
    public ResponseEntity<?> cancelRide(RideCancellationRequest rideCancellationRequest){

        return null;
    }
}
