package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.RideCancellation;
import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.entity.RideStatus;
import com.mecaps.ridingBookingSystem.exception.RideCancellationNotFound;
import com.mecaps.ridingBookingSystem.exception.RideRequestNotFoundException;
import com.mecaps.ridingBookingSystem.repository.RideCancellationRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.request.RideCancellationRequest;
import com.mecaps.ridingBookingSystem.service.RideCancellationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
        RideRequests cancelRideRequest = rideRequestsRepository.findById(rideCancellationRequest.getRideRequestId())
                .orElseThrow(() -> new RideRequestNotFoundException("Ride Request not found for the given ID: " + rideCancellationRequest.getRideRequestId()));

        cancelRideRequest.setStatus(RideStatus.CANCELLED);
        rideRequestsRepository.save(cancelRideRequest);

        RideCancellation rideCancellation = RideCancellation.builder()
                .rideRequest(cancelRideRequest)
                .cancelledBy(rideCancellationRequest.getCancelledBy())
                .reason(rideCancellationRequest.getReason())
                .cancelledAt(LocalDateTime.now())
                .build();

        rideCancellationRepository.save(rideCancellation);

        return ResponseEntity.ok().body(Map.of(
                "message", "Ride cancelled successfully",
                "body", rideCancellation,
                "success", true
        ));
    }

    @Override
    public RideCancellation getRideCancellationById(Long id) {
        return rideCancellationRepository.findById(id)
                .orElseThrow(() -> new RideCancellationNotFound("Ride Cancellation not found for the given ID: " + id));

    }

    @Override
    public List<RideCancellation> getAllRideCancellation() {
        return rideCancellationRepository.findAll();
    }

    @Override
    public ResponseEntity<?> deleteRideCancellationById(Long id) {
        rideCancellationRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Ride Cancellation deleted successfully");
    }
}
