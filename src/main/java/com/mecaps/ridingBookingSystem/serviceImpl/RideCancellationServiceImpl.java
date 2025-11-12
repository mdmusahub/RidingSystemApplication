package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.RideCancellation;
import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.entity.RideStatus;
import com.mecaps.ridingBookingSystem.exception.RideRequestNotFoundException;
import com.mecaps.ridingBookingSystem.repository.RideCancellationRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.request.RideCancellationRequest;
import com.mecaps.ridingBookingSystem.service.RideCancellationService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RideCancellationServiceImpl implements RideCancellationService {
    private final RideCancellationRepository rideCancellationRepository;
    private final RideRequestsRepository rideRequestsRepository;
    public RideCancellationServiceImpl(RideCancellationRepository rideCancellationRepository, RideRequestsRepository rideRequestsRepository) {
        this.rideCancellationRepository = rideCancellationRepository;
        this.rideRequestsRepository = rideRequestsRepository;
    }

    @Override
    public ResponseEntity<?> cancelRide(RideCancellationRequest rideCancellationRequest) {
        RideRequests ride = rideRequestsRepository.findById(rideCancellationRequest.getRideRequestId()
        ).orElseThrow(() -> new RideRequestNotFoundException("Ride not found"));

        if ("ONGOING".equalsIgnoreCase(String.valueOf(ride.getStatus())) || "COMPLETED".equalsIgnoreCase(String.valueOf(ride.getStatus()))) {
            return ResponseEntity.badRequest().body("Ride cannot be cancelled");
        }
        ride.setStatus(RideStatus.valueOf("CANCELLED"));
        rideRequestsRepository.save(ride);
        RideCancellation rideCancellation = new RideCancellation();
        rideCancellation.setCancelledBy(rideCancellationRequest.getCancelledBy());
        rideCancellation.setReason(rideCancellationRequest.getReason());
        rideCancellation.setCancelledAt(LocalDateTime.now());

        return ResponseEntity.ok("Ride cancelled successfully and logged");

    }
}
