package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.entity.RideStatus;
import com.mecaps.ridingBookingSystem.entity.Rider;
import com.mecaps.ridingBookingSystem.exception.DriverNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RideRequestNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.repository.DriverRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.request.OtpGenerateRequest;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import com.mecaps.ridingBookingSystem.response.DriverResponse;
import com.mecaps.ridingBookingSystem.service.RideRequestService;
import com.mecaps.ridingBookingSystem.util.DistanceCalculator;
import com.mecaps.ridingBookingSystem.util.OtpUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RideRequestServiceImpl implements RideRequestService {

    private final static Double FARE_PER_KM = 10.00;

    private final RideRequestsRepository rideRequestsRepository;
    private final RiderRepository riderRepository;
    private final DriverServiceImpl driverService;
    private final DriverRepository driverRepository;
    private final OneTimePasswordServiceImpl oneTimePasswordService;

    public RideRequestServiceImpl(RideRequestsRepository rideRequestsRepository,
                                  RiderRepository riderRepository,
                                  DriverRepository driverRepository, DriverServiceImpl driverService, DriverRepository driverRepository1, OneTimePasswordServiceImpl oneTimePasswordService) {
        this.rideRequestsRepository = rideRequestsRepository;
        this.riderRepository = riderRepository;
        this.driverService = driverService;
        this.driverRepository = driverRepository;
        this.oneTimePasswordService = oneTimePasswordService;
    }

    @Override
    public ResponseEntity<?> createRideRequest(RideRequestsDTO request) {
        Rider rider = riderRepository.findById(request.getRiderId())
                .orElseThrow(() -> new RiderNotFoundException
                        ("Rider not found with ID: " + request.getRiderId()));

        RideRequests rideRequest = new RideRequests();

        rideRequest.setRiderId(rider);
        rideRequest.setPickupLat(request.getPickupLat());
        rideRequest.setPickupLng(request.getPickupLng());
        rideRequest.setDropLat(request.getDropLat());
        rideRequest.setDropLng(request.getDropLng());

        rideRequestsRepository.save(rideRequest);

        Double distance = DistanceCalculator.calculateDistance(request.getPickupLat(), request.getPickupLng(),
                request.getDropLat(), request.getDropLng());
        Double fare = distance * FARE_PER_KM;

        Map<String, Object> response = new HashMap<>();

        response.put("Message", "Ride request created successfully");
        response.put("rideRequestId", rideRequest.getId());
        response.put("distanceInKM", distance);
        response.put("estimatedFare", fare);
        response.put("DriversAvailableNearby", driverService.findNearestAvailableDrivers(rideRequest, 3).size());
        response.put("success", true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<?> confirmRideRequestPickup(Long rideRequestId, Boolean isConfirmed) {
        RideRequests rideRequest = rideRequestsRepository.findById(rideRequestId)
                .orElseThrow(() -> new RideRequestNotFoundException("No Such Ride Request Found"));

        if (!isConfirmed) {
            rideRequestsRepository.deleteById(rideRequestId);
            return ResponseEntity.ok("Ride Request Avoided by Rider");
        }
        rideRequest.setStatus(RideStatus.REQUESTED);
        rideRequest.setRequestedAt(LocalDateTime.now());
        rideRequest.setExpiresAt(LocalDateTime.now().plusMinutes(3));

        oneTimePasswordService.createOtp(rideRequest.getRiderId().getId(), rideRequestId);

        rideRequestsRepository.save(rideRequest);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "message", "Ride Request Confirmed",
                "body", rideRequest,
                "DriversAvailableNearby", driverService.findNearestAvailableDrivers(rideRequest, 3),
                "success", true
        ));
    }

    @Override
    public ResponseEntity<?> driverRideRequestConfirmation(Long rideRequestId, Long driverId, Boolean isAccepted) {
        RideRequests rideRequest = rideRequestsRepository.findById(rideRequestId)
                .orElseThrow(() -> new RideRequestNotFoundException("No Such Ride Request Found"));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("DRIVER NOT FOUND"));

        if (LocalDateTime.now().isAfter(rideRequest.getExpiresAt())) {
            rideRequest.setStatus(RideStatus.EXPIRED);
            rideRequestsRepository.save(rideRequest);
            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .body("Ride Request has expired");
        }

        if (!isAccepted) {
            rideRequest.setStatus(RideStatus.CANCELLED);
            rideRequestsRepository.save(rideRequest);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Ride Request Cancelled by Driver.");
        }

        rideRequest.setStatus(RideStatus.ACCEPTED);
        driver.setIsAvailable(false);

        rideRequestsRepository.save(rideRequest);
        driverRepository.save(driver);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "message", "Driver Accepted Ride Request"
                ));
    }
}
