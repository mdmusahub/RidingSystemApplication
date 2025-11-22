package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.*;
import com.mecaps.ridingBookingSystem.exception.*;
import com.mecaps.ridingBookingSystem.repository.*;
import com.mecaps.ridingBookingSystem.request.CompleteRideRequest;
import com.mecaps.ridingBookingSystem.request.StartRideRequest;
import com.mecaps.ridingBookingSystem.response.RidesResponse;
import com.mecaps.ridingBookingSystem.service.RidesService;
import com.mecaps.ridingBookingSystem.util.DistanceFareUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service Implementation of Ride Service
 * Whenever A rider request for ride he will get fare and distance from data.
 * Driver will Accept ride and Start ride after validating OTP.
 * Driver will mark Completed Ride after reaching its destination.
 */
@Service
public class RidesServiceImpl implements RidesService {
    private final RideRepository rideRepository;
    private final OneTimePasswordServiceImpl oneTimePasswordService;
    private final OneTimePasswordRepository oneTimePasswordRepository;
    private final DriverRepository driverRepository;
    private final RideRequestsRepository rideRequestsRepository;
    private final RiderRepository riderRepository;
    private final RideHistoryServiceImpl rideHistoryService;

    public RidesServiceImpl(RideRepository rideRepository, OneTimePasswordServiceImpl oneTimePasswordService, OneTimePasswordRepository oneTimePasswordRepository, DriverRepository driverRepository, RideRequestsRepository rideRequestsRepository, RiderRepository riderRepository, RideHistoryServiceImpl rideHistoryService) {
        this.rideRepository = rideRepository;
        this.oneTimePasswordService = oneTimePasswordService;
        this.oneTimePasswordRepository = oneTimePasswordRepository;
        this.driverRepository = driverRepository;
        this.rideRequestsRepository = rideRequestsRepository;
        this.riderRepository = riderRepository;
        this.rideHistoryService = rideHistoryService;
    }

    /**
     * Starts a new ride.
     * <p>
     * Steps:
     * <ul>
     *   <li>Fetches driver, ride request and rider from database</li>
     *   <li>Validates OTP for the ride request</li>
     *   <li>Calculates distance and fare using pickup and drop coordinates</li>
     *   <li>Creates a new {@link Rides} entry with status {@link RideStatus#ONGOING}</li>
     * </ul>
     * @param startRideRequest request object containing driver id, ride request id and OTP
     * @return {@link ResponseEntity} containing ride details and status.
     * @throws DriverNotFoundException       if the driver is not found
     * @throws RideRequestNotFoundException  if the ride request is not found
     */
    @Override
    public ResponseEntity<?> startRide(StartRideRequest startRideRequest) {
        Driver driver = driverRepository.findById(startRideRequest.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver Not Found"));

        RideRequests newRideRequest = rideRequestsRepository.findById(startRideRequest.getRideRequestId())
                .orElseThrow(() -> new RideRequestNotFoundException("No such Ride Request Found"));

        Rider rider = riderRepository.findById(newRideRequest.getRiderId().getId())
                .orElseThrow(() -> new RiderNotFoundException("Rider Not Found"));

        OneTimePassword otp = oneTimePasswordRepository.findByRideRequestId(startRideRequest.getRideRequestId())
                .orElseThrow(() -> new OneTimePasswordNotFoundException("Otp not found"));

        // OTP Validation before starting the ride
        if (!oneTimePasswordService.validateOtp(startRideRequest.getOtp(), otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid OTP");
        }

        // Distance & Fare for the Ride
        Double distanceKm = DistanceFareUtil.calculateDistance(newRideRequest.getPickupLat(), newRideRequest.getPickupLng(), newRideRequest.getDropLat(), newRideRequest.getDropLng());
        Double fare = DistanceFareUtil.calculateFare(distanceKm);

        // Start Ride
        Rides ride = Rides.builder()
                .riderId(rider)
                .driverId(driver)
                .rideRequestId(newRideRequest)
                .fare(fare)
                .distanceKm(distanceKm)
                .driverRating(driver.getRating())
                .riderRating(rider.getRating())
                .status(RideStatus.ONGOING)
                .startTime(LocalDateTime.now())
                .build();

        Rides save = rideRepository.save(ride);

        RidesResponse ridesResponse = new RidesResponse(save);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Ride Started. Ride created successfully",
                "ride", ridesResponse,
                "currentRideStatus", ridesResponse.getStatus(),
                "success", true
        ));
    }
    /**
     * Marks a ride as completed by Driver after reaching its Location.
     * <p>
     * Steps:
     * <ul>
     *   <li>Fetches ride, ride request and driver from database</li>
     *   <li>Checks if the provided driver is actually assigned to this ride</li>
     *   <li>Updates ride status to {@link RideStatus#COMPLETED} and sets end time</li>
     *   <li>Marks driver as available again</li>
     *   <li>Creates ride history entry</li>
     * </ul>
     * @param completeRideRequest request object containing ride id and driver id
     * @return {@link ResponseEntity} with updated ride details and status.
     * @throws RideNotFoundException         if the ride is not found
     * @throws RideRequestNotFoundException  if the ride request is not found
     * @throws DriverNotFoundException       if the driver is not found
     */
    @Override
    public ResponseEntity<?> completeRide(CompleteRideRequest completeRideRequest){
        Rides ride = rideRepository.findById(completeRideRequest.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found for the given ID: " + completeRideRequest.getRideId()));

        RideRequests rideRequest = rideRequestsRepository.findById(ride.getRideRequestId().getId())
                .orElseThrow(() -> new RideRequestNotFoundException("No such Ride Request Found"));

        Driver driver = driverRepository.findById(completeRideRequest.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found for the given ID: " + completeRideRequest.getDriverId()));

        if(!ride.getDriverId().equals(driver)){
           return ResponseEntity.status(HttpStatus.FORBIDDEN)
                   .body(Map.of(
                           "message","Driver is not assigned to this ride",
                           "success",false
                   ));
        }
        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());

        DriverStatus driverStatus = driver.getDriverStatus();
        driverStatus.setIsAvailable(true);

        driver.setDriverStatus(driverStatus);
        driverRepository.save(driver);

        Rides save = rideRepository.save(ride);
        RidesResponse ridesResponse = new RidesResponse(save);

        rideHistoryService.createRideHistory(ride);

        return ResponseEntity.ok().body(Map.of(
                "message","Ride completed successfully",
                "ride",ridesResponse,
                "currentRideStatus",ridesResponse.getStatus(),
                "success","true"
        ));
    }
}
