package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.request.DriverRequest;
import com.mecaps.ridingBookingSystem.serviceImpl.DriverServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/driver")
public class DriverController {

    final private DriverServiceImpl driverService;

    @Autowired

    public DriverController(DriverServiceImpl driverService) {
        this.driverService = driverService;
    }
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public ResponseEntity<?> createDriver(@RequestBody DriverRequest request) {
        return driverService.createDriver(request);
    }
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getDriverById(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDriver(@PathVariable Long id, @RequestBody DriverRequest request) {
        return driverService.updateDriver(id, request);
    }
    @PreAuthorize("hasRole('ADMIN') AND hasRole('USER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long id) {
        return driverService.deleteDriver(id);
    }
}
