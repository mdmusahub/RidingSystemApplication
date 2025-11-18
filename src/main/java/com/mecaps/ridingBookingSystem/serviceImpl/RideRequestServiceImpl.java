package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.*;
import com.mecaps.ridingBookingSystem.exception.DriverNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RideRequestNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.repository.DriverRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import com.mecaps.ridingBookingSystem.service.RideRequestService;
import com.mecaps.ridingBookingSystem.util.DistanceFareUtil;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RideRequestServiceImpl implements RideRequestService {
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
    @PermitAll
    public Map<String, Object> getRideFareAndDistance(RideRequestsDTO request) {

        Double distance = DistanceFareUtil.calculateDistance(
                request.getPickupLat(),
                request.getPickupLng(),
                request.getDropLat(),
                request.getDropLng()
        );
        Double fare = DistanceFareUtil.calculateFare(distance);

        Map<String, Object> response = new HashMap<>();

        response.put("estimatedFare", fare);
        response.put("distanceInKM", distance);
        response.put("DriversAvailableNearby", driverService.findNearestAvailableDrivers(request, 3).size());

        return response;
    }

    @Override
    @PreAuthorize("#request.getRiderId() == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<?> createRideRequest(RideRequestsDTO request) {
        Rider rider = riderRepository.findById(request.getRiderId())
                .orElseThrow(() -> new RiderNotFoundException
                        ("Rider not found with ID: " + request.getRiderId()));

        RideRequests rideRequest = RideRequests.builder()
                .riderId(rider)
                .pickupLat(request.getPickupLat())
                .pickupLng(request.getPickupLng())
                .dropLat(request.getDropLat())
                .dropLng(request.getDropLng())
                .status(RideStatus.REQUESTED)
                .requestedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(3))
                .build();

        rideRequestsRepository.save(rideRequest);

        OneTimePassword otp = oneTimePasswordService.createOtp(rider.getId(), rideRequest.getId());

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Rider Confirmed Pickup. RideRequest created successfully");
        response.put("rideRequestId", rideRequest.getId());
        response.put("startRideOTP", otp.getOtpCode());
        response.put("distanceInKM", this.getRideFareAndDistance(request).get("distance"));
        response.put("estimatedFare", this.getRideFareAndDistance(request).get("fare"));
        response.put("DriversAvailableNearby", driverService.findNearestAvailableDrivers(request, 3));
        response.put("success", true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or (#driverId == authentication.principal.id and hasRole('DRIVER'))")
    public ResponseEntity<?> driverRideRequestConfirmation(Long rideRequestId, Long driverId, Boolean isAccepted) {
        RideRequests rideRequest = rideRequestsRepository.findById(rideRequestId)
                .orElseThrow(() -> new RideRequestNotFoundException("No Such Ride Request Found"));

        if (!rideRequest.getStatus().equals(RideStatus.REQUESTED)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "Ride Request is not in REQUESTED state",
                            "currentRideStatus", rideRequest.getStatus().toString(),
                            "success", false
                    ));
        }

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
