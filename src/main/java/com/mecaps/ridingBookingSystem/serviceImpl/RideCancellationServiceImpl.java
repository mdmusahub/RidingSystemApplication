package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.*;
import com.mecaps.ridingBookingSystem.exception.DriverNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RideCancellationNotFound;
import com.mecaps.ridingBookingSystem.exception.RideRequestNotFoundException;
import com.mecaps.ridingBookingSystem.repository.DriverRepository;
import com.mecaps.ridingBookingSystem.repository.OneTimePasswordRepository;
import com.mecaps.ridingBookingSystem.repository.RideCancellationRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.request.RideCancellationRequest;
import com.mecaps.ridingBookingSystem.response.RideCancellationResponse;
import com.mecaps.ridingBookingSystem.service.OneTimePasswordService;
import com.mecaps.ridingBookingSystem.service.RideCancellationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
/**
 * Service implementation for handling ride cancellation operations.
 * Ride can be cancel from both ends driver and rider until its status marks as ONGOING.
 * Supports cancelling a ride, fetching cancellation details,
 * retrieving all cancellations, and deleting cancellation records.
 */

@Service
public class RideCancellationServiceImpl implements RideCancellationService {
    private final RideCancellationRepository rideCancellationRepository;
    private final RideRequestsRepository rideRequestsRepository;
    private final DriverRepository driverRepository;
    private final OneTimePasswordService oneTimePasswordService;
    private final OneTimePasswordRepository oneTimePasswordRepository;

    public RideCancellationServiceImpl(RideCancellationRepository rideCancellationRepository, RideRequestsRepository rideRequestsRepository, DriverRepository driverRepository, OneTimePasswordService oneTimePasswordService, OneTimePasswordRepository oneTimePasswordRepository) {
        this.rideCancellationRepository = rideCancellationRepository;
        this.rideRequestsRepository = rideRequestsRepository;
        this.driverRepository = driverRepository;
        this.oneTimePasswordService = oneTimePasswordService;
        this.oneTimePasswordRepository = oneTimePasswordRepository;
    }
/**
 * Ride Cancellation method for riders and drivers.
 * Both  can cancel ride  anytime until its Status is not marked as ONGOING or COMPLETED.
 **/
    @Override
    public ResponseEntity<?> cancelRide(RideCancellationRequest rideCancellationRequest) {
        RideRequests cancelRideRequest = rideRequestsRepository.findById(rideCancellationRequest.getRideRequestId())
                .orElseThrow(() -> new RideRequestNotFoundException("Ride Request not found for the given ID: " + rideCancellationRequest.getRideRequestId()));

        Driver driver = driverRepository.findById(rideCancellationRequest.getDriverId())
                        .orElseThrow(() -> new DriverNotFoundException("Driver not found with the given ID: " + rideCancellationRequest.getDriverId()));


        cancelRideRequest.setStatus(RideStatus.CANCELLED);

        Optional<OneTimePassword> otp = oneTimePasswordRepository.findByRideRequestId(rideCancellationRequest.getRideRequestId());
        otp.ifPresent(oneTimePassword -> oneTimePasswordRepository.deleteById(oneTimePassword.getId()));

        RideCancellation rideCancellation = RideCancellation.builder()
                .rideRequest(cancelRideRequest)
                .cancelledBy(rideCancellationRequest.getCancelledBy())
                .reason(rideCancellationRequest.getReason())
                .cancelledAt(LocalDateTime.now())
                .build();
        // Mark driver available again

        DriverStatus driverStatus = driver.getDriverStatus();
        driverStatus.setIsAvailable(true);

        driver.setDriverStatus(driverStatus);

        driverRepository.save(driver);
        rideRequestsRepository.save(cancelRideRequest);
        rideCancellationRepository.save(rideCancellation);

        return ResponseEntity.ok().body(Map.of(
                "message", "Ride cancelled successfully",
                "body", new RideCancellationResponse(rideCancellation),
                "success", true
        ));
    }
    /**
     * Fetches a ride cancellation record by its ID.
     * User can get ride cancellation data by its ID.
     * @param id cancellation ID
     * @return cancellation details wrapped in response body
     * @throws RideCancellationNotFound if no record exists for the given ID
     */

    @Override
    public ResponseEntity<?> getRideCancellationById(Long id) {
        RideCancellation rideCancellation = rideCancellationRepository.findById(id)
                .orElseThrow(() -> new RideCancellationNotFound("Ride Cancellation not found for the given ID: " + id));
        return ResponseEntity.ok().body(new RideCancellationResponse(rideCancellation));
    }
    /**
     * Retrieves all ride cancellation records in the system.
     * Mainly for Admin Usage where ADMIN can get All ride cancellation data.
     * @return list of all cancellation responses
     */

    @Override
    public ResponseEntity<?> getAllRideCancellation() {
        List<RideCancellation> rideCancellationList = rideCancellationRepository.findAll();
        List<RideCancellationResponse> rideCancellationResponseList = rideCancellationList.stream().map(RideCancellationResponse::new).toList();
        return ResponseEntity.ok().body(rideCancellationResponseList);
    }

    // method for delte ride cancel data by Id. Admin Usage Only.

    @Override
    public ResponseEntity<?> deleteRideCancellationById(Long id) {
        rideCancellationRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Ride Cancellation deleted successfully");
    }
}
