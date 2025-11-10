package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.*;
import com.mecaps.ridingBookingSystem.exception.DriverNotFoundException;
import com.mecaps.ridingBookingSystem.exception.OneTimePasswordNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RideRequestNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.repository.*;
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
        Driver driver =  driverRepository.findById(startRideRequest.getDriverId())
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
        Double distanceKm = DistanceFareUtil.calculateDistance(rideRequest.getPickupLat(),rideRequest.getPickupLng(),rideRequest.getDropLat(),rideRequest.getDropLng());
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
           "message","Ride Started. Ride created successfully",
           "currentRideStatus",""
        ));
    }
}
