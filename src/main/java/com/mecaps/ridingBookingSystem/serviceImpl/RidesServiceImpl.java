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
