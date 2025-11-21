package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.request.*;
import com.mecaps.ridingBookingSystem.service.*;
import com.mecaps.ridingBookingSystem.service.RiderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rider")
public class RiderController {

    private final RiderService riderService;
    private final RideRequestsService rideRequestsService;
    private final RideHistoryService rideHistoryService;
    private final RideCancellationService rideCancellationService;
    private final ReviewService reviewService;

    public RiderController(RiderService riderService, RideRequestsService rideRequestsService, RideHistoryService rideHistoryService, RideCancellationService rideCancellationService, ReviewService reviewService){
        this.riderService = riderService;
        this.rideRequestsService = rideRequestsService;
        this.rideHistoryService = rideHistoryService;
        this.rideCancellationService = rideCancellationService;
        this.reviewService = reviewService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RIDER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getRiderById(@PathVariable Long id){
        return riderService.getRiderById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllRiders() {
        return riderService.getAllRiders();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('RIDER'))")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRider(@PathVariable Long id) {
        return riderService.deleteRider(id);
    }

    @PostMapping("/get-fare-distance")
    public Map<String, Object> getFareAndDistance(@RequestBody RideRequestsDTO rideRequestsDTO){
        return rideRequestsService.getRideFareAndDistance(rideRequestsDTO);
    }

    @PostMapping("/request-ride")
    public ResponseEntity<?> createRideRequest(@RequestBody RideRequestsDTO rideRequestsDTO){
        return rideRequestsService.createRideRequest(rideRequestsDTO);
    }

    @PostMapping("/rides-history/{riderId}")
    public ResponseEntity<?> getAllRidesHistoryForRider(@PathVariable Long riderId){
        return rideHistoryService.getAllRidesHistoryForRider(riderId);
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
