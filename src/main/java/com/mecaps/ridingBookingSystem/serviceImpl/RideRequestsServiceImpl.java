package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.*;
import com.mecaps.ridingBookingSystem.exception.DriverNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RideRequestNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.repository.DriverRepository;
import com.mecaps.ridingBookingSystem.repository.OneTimePasswordRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.request.RideAcceptanceRequestDTO;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import com.mecaps.ridingBookingSystem.service.DriverService;
import com.mecaps.ridingBookingSystem.service.OneTimePasswordService;
import com.mecaps.ridingBookingSystem.service.RideRequestsService;
import com.mecaps.ridingBookingSystem.util.DistanceFareUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class RideRequestsServiceImpl implements RideRequestsService {
    private final RideRequestsRepository rideRequestsRepository;
    private final RiderRepository riderRepository;
    private final DriverService driverService;
    private final DriverRepository driverRepository;
    private final OneTimePasswordService oneTimePasswordService;
    private final OneTimePasswordRepository oneTimePasswordRepository;

    public RideRequestsServiceImpl(RideRequestsRepository rideRequestsRepository,
                                   RiderRepository riderRepository,
                                   DriverRepository driverRepository,
                                   DriverService driverService,
                                   OneTimePasswordService oneTimePasswordService,
                                   OneTimePasswordRepository oneTimePasswordRepository) {
        this.rideRequestsRepository = rideRequestsRepository;
        this.riderRepository = riderRepository;
        this.driverService = driverService;
        this.driverRepository = driverRepository;
        this.oneTimePasswordService = oneTimePasswordService;
        this.oneTimePasswordRepository = oneTimePasswordRepository;
    }

    /**
     * Calculates the estimated fare and distance between the pickup and drop locations
     * provided in the ride request. Also retrieves the number of nearby available drivers.
     *
     * <p>This method performs the following steps:</p>
     * <ul>
     *   <li>Computes the total distance (in kilometers) between pickup and drop coordinates.</li>
     *   <li>Calculates the estimated fare based on the computed distance.</li>
     *   <li>Finds nearby available drivers within a given radius (default: 3 drivers).</li>
     * </ul>
     *
     * @param request the {@link RideRequestsDTO} containing pickup and drop coordinates
     * @return a map containing estimated fare, distance (in KM), and nearby driver count
     */
    @Override
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

    /**
     * Creates a new ride request after the rider confirms the fare and distance.
     *
     * <p>This method performs the following steps:</p>
     * <ul>
     *   <li>Validates the rider.</li>
     *   <li>Creates a new {@link RideRequests} record.</li>
     *   <li>Generates an OTP and calculates fare/distance.</li>
     *   <li>Finds nearby available drivers.</li>
     * </ul>
     *
     * @param request the request body containing rider and location details
     * @return response containing ride ID, fare, distance, and nearby drivers
     */
    @Override
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

        OneTimePassword otp = oneTimePasswordService.createOtp(rideRequest);
        log.info("OTP Generated successfully : {}",otp.getOtpCode());

        Map<String, Object> fareAndDistance = this.getRideFareAndDistance(request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "RideRequest created successfully after rider confirmation");
        response.put("rideRequestId", rideRequest.getId());
        response.put("startRideOTP", otp.getOtpCode());
        response.put("distanceInKM", fareAndDistance.get("distanceInKM"));
        response.put("estimatedFare", fareAndDistance.get("estimatedFare"));
        response.put("driversAvailableNearby", driverService.findNearestAvailableDrivers(request, 3));
        response.put("success", true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Allows a driver to accept a ride request after validating its current status and expiry time.
     *
     * <p>This method performs the following steps:</p>
     * <ul>
     *   <li>Validates that the ride request exists and is in {@link RideStatus#REQUESTED} state.</li>
     *   <li>Ensures the ride request has not expired.</li>
     *   <li>Updates the ride status to {@link RideStatus#ACCEPTED} and marks the driver as unavailable.</li>
     * </ul>
     *
     * @param requestDTO the {@link RideAcceptanceRequestDTO} containing driver and ride request details
     * @return a {@link ResponseEntity} with the result of the acceptance operation and status message
     */
    @Override
    public ResponseEntity<?> acceptRideRequest(RideAcceptanceRequestDTO requestDTO) {
        RideRequests rideRequest = rideRequestsRepository.findById(requestDTO.getRideRequestId())
                .orElseThrow(() -> new RideRequestNotFoundException("No Such Ride Request Found"));

        if (!rideRequest.getStatus().equals(RideStatus.REQUESTED)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "message", "Ride Request is not in REQUESTED state",
                            "currentRideStatus", rideRequest.getStatus().toString(),
                            "success", false
                    ));
        }

        Driver driver = driverRepository.findById(requestDTO.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("DRIVER NOT FOUND"));

        if (LocalDateTime.now().isAfter(rideRequest.getExpiresAt())) {
            rideRequest.setStatus(RideStatus.EXPIRED);

            Optional<OneTimePassword> otp = oneTimePasswordRepository.findByRideRequestId(rideRequest.getId());
            otp.ifPresent(oneTimePassword -> oneTimePasswordRepository.deleteById(otp.get().getId()));

            rideRequestsRepository.save(rideRequest);
            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .body("Ride Request has expired");
        }

        rideRequest.setStatus(RideStatus.ACCEPTED);
        DriverStatus driverStatus = driver.getDriverStatus();
        driverStatus.setIsAvailable(false);

        driver.setDriverStatus(driverStatus);

        driverRepository.save(driver);
        rideRequestsRepository.save(rideRequest);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "message", "Driver Accepted Ride Request"
                ));
    }
}