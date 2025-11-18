package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.repository.RideHistoryRepository;
import com.mecaps.ridingBookingSystem.service.RideCancellationService;
import com.mecaps.ridingBookingSystem.service.RideHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final RideHistoryService rideHistoryService;
    private final RideCancellationService rideCancellationService;

    public AdminController(RideHistoryService rideHistoryService, RideCancellationService rideCancellationService) {
        this.rideHistoryService = rideHistoryService;
        this.rideCancellationService = rideCancellationService;
    }

    @GetMapping("/get-all-rides-history")
    public ResponseEntity<?> getAllRideHistory(){
        return rideHistoryService.getAllRideHistory();
    }

    @GetMapping("/get-ride-history/{rideHistoryId}")
    public ResponseEntity<?> getRideHistoryById(@PathVariable Long rideHistoryId){
        return rideHistoryService.getRideHistoryById(rideHistoryId);
    }

    @GetMapping("/get-all-rides-cancellation")
    public ResponseEntity<?> getAllRideCancellation(){
        return rideCancellationService.getAllRideCancellation();
    }

    @GetMapping("/get-ride-cancellation/{rideCancellationId}")
    public ResponseEntity<?> getRideCancellationById(@PathVariable Long rideCancellationId){
        return rideCancellationService.getRideCancellationById(rideCancellationId);
    }
}
