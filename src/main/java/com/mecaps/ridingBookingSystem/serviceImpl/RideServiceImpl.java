package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.*;
import com.mecaps.ridingBookingSystem.exception.*;
import com.mecaps.ridingBookingSystem.repository.*;
import com.mecaps.ridingBookingSystem.request.CompleteRideRequest;
import com.mecaps.ridingBookingSystem.request.StartRideRequest;
import com.mecaps.ridingBookingSystem.service.RideService;
import com.mecaps.ridingBookingSystem.util.DistanceFareUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class RideServiceImpl implements RideService {
    private final RideRepository rideRepository;
    private final OneTimePasswordServiceImpl oneTimePasswordService;
    private final OneTimePasswordRepository oneTimePasswordRepository;
    private final DriverRepository driverRepository;
    private final RideRequestsRepository rideRequestsRepository;
    private final RiderRepository riderRepository;

    public RideServiceImpl(RideRepository rideRepository, OneTimePasswordServiceImpl oneTimePasswordService, OneTimePasswordRepository oneTimePasswordRepository, DriverRepository driverRepository, RideRequestsRepository rideRequestsRepository, RiderRepository riderRepository) {
        this.rideRepository = rideRepository;
        this.oneTimePasswordService = oneTimePasswordService;
        this.oneTimePasswordRepository = oneTimePasswordRepository;
        this.driverRepository = driverRepository;
        this.rideRequestsRepository = rideRequestsRepository;
        this.riderRepository = riderRepository;
    }

    @Override
    public ResponseEntity<?> startRide(StartRideRequest startRideRequest) {
        Driver driver = driverRepository.findById(startRideRequest.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver Not Found"));

        RideRequests rideRequest = rideRequestsRepository.findById(startRideRequest.getRideRequestId())
                .orElseThrow(() -> new RideRequestNotFoundException("No such Ride Request Found"));

        Rider rider = riderRepository.findById(rideRequest.getRiderId().getId())
                .orElseThrow(() -> new RiderNotFoundException("Rider Not Found"));


        OneTimePassword otp = oneTimePasswordRepository.findByRideRequestId(startRideRequest.getRideRequestId())
                .orElseThrow(() -> new OneTimePasswordNotFoundException("Otp not found"));

        // OTP Validation before starting the ride
        if (!oneTimePasswordService.validateOtp(startRideRequest.getOtp(), otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid OTP");
        }

        // Distance & Fare for the Ride
        Double distanceKm = DistanceFareUtil.calculateDistance(rideRequest.getPickupLat(), rideRequest.getPickupLng(), rideRequest.getDropLat(), rideRequest.getDropLng());
        Double fare = DistanceFareUtil.calculateFare(distanceKm);

        // Start Ride
        Rides ride = Rides.builder()
                .riderId(rider)
                .driverId(driver)
                .requestsId(rideRequest)
                .fare(fare)
                .distanceKm(distanceKm)
                .driverRating(driver.getRating())
                .riderRating(rider.getRating())
                .status(RideStatus.ONGOING)
                .startTime(LocalDateTime.now())
                .build();

        rideRepository.save(ride);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Ride Started. Ride created successfully",
                "ride", ride,
                "currentRideStatus", ride.getStatus(),
                "success", true
        ));
    }

    @Override
    public ResponseEntity<?> completeRide(CompleteRideRequest completeRideRequest){
        Rides ride = rideRepository.findById(completeRideRequest.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found for the given ID: " + completeRideRequest.getRideId()));

        RideRequests rideRequest = rideRequestsRepository.findById(ride.getRequestsId().getId())
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

        driver.setIsAvailable(true);

        rideRepository.save(ride);
        driverRepository.save(driver);

        // Distance & Fare for the Ride
        Double distanceKm = DistanceFareUtil.calculateDistance(rideRequest.getPickupLat(), rideRequest.getPickupLng(), rideRequest.getDropLat(), rideRequest.getDropLng());
        Double fare = DistanceFareUtil.calculateFare(distanceKm);

        return ResponseEntity.ok().body(Map.of(
                "message","Ride completed successfully",
                "rideId",ride.getId(),
                "fare",fare,
                "distance",distanceKm,
                "success","true"
        ));
    }
}
