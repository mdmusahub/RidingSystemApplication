package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.request.*;
import com.mecaps.ridingBookingSystem.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
public class DriverController {

    private final DriverService driverService;
    private final RideRequestsService rideRequestsService;
    private final RidesService ridesService;
    private final RideHistoryService rideHistoryService;
    private final RideCancellationService rideCancellationService;
    private final ReviewService reviewService;

    public DriverController(DriverService driverService, RideRequestsService rideRequestsService, RidesService ridesService, RideHistoryService rideHistoryService, RideCancellationService rideCancellationService, ReviewService reviewService) {
        this.driverService = driverService;
        this.rideRequestsService = rideRequestsService;
        this.ridesService = ridesService;
        this.rideHistoryService = rideHistoryService;
        this.rideCancellationService = rideCancellationService;
        this.reviewService = reviewService;
    }
    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDriver(@PathVariable Long id, @RequestBody DriverRequest request) {
        return driverService.updateDriver(id, request);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id) {
        return driverService.deleteDriver(id);
    }

    @PostMapping("/accept-ride-request")
    public ResponseEntity<?> acceptRideRequest(@RequestBody RideAcceptanceRequestDTO rideAcceptanceRequestDTO) {
        return rideRequestsService.acceptRideRequest(rideAcceptanceRequestDTO);
    }

    @PostMapping("/start-ride")
    public ResponseEntity<?> startRide(@RequestBody StartRideRequest startRideRequest){
        return ridesService.startRide(startRideRequest);
    }

    @PostMapping("/complete-ride")
    public ResponseEntity<?> completeRide(@RequestBody CompleteRideRequest completeRideRequest){
        return ridesService.completeRide(completeRideRequest);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DRIVER')")
    @PostMapping("/rides-history/{driverId}")
    public ResponseEntity<?> getAllRidesHistoryForDriver(@PathVariable Long driverId){
        return rideHistoryService.getAllRidesHistoryForDriver(driverId);
    }

    @PostMapping("/cancel-ride-request")
    public ResponseEntity<?> cancelRide(@RequestBody RideCancellationRequest rideCancellationRequest){
        return rideCancellationService.cancelRide(rideCancellationRequest);
    }

    @PostMapping("/submit-review")
    public ResponseEntity<?> submitReview(@RequestBody ReviewRequestDTO reviewRequestDTO){
        return reviewService.submitReview(reviewRequestDTO);
    }

    @PutMapping("/update-review")
    public ResponseEntity<?> updateReview(@RequestBody UpdateReviewRequestDTO updateReviewRequestDTO){
        return reviewService.updateReview(updateReviewRequestDTO);
    }
}
