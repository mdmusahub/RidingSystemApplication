package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.request.*;
import com.mecaps.ridingBookingSystem.service.RideCancellationService;
import com.mecaps.ridingBookingSystem.service.RideHistoryService;
import com.mecaps.ridingBookingSystem.service.RideRequestsService;
import com.mecaps.ridingBookingSystem.service.RidesService;
import com.mecaps.ridingBookingSystem.serviceImpl.DriverServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
public class DriverController {

    private final DriverServiceImpl driverService;
    private final RideRequestsService rideRequestsService;
    private final RidesService ridesService;
    private final RideHistoryService rideHistoryService;
    private final RideCancellationService rideCancellationService;

    public DriverController(DriverServiceImpl driverService, RideRequestsService rideRequestsService, RidesService ridesService, RideHistoryService rideHistoryService, RideCancellationService rideCancellationService) {
        this.driverService = driverService;
        this.rideRequestsService = rideRequestsService;
        this.ridesService = ridesService;
        this.rideHistoryService = rideHistoryService;
        this.rideCancellationService = rideCancellationService;
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
}
