package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.request.RiderRequest;
import com.mecaps.ridingBookingSystem.serviceImpl.RiderServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rider")
public class RiderController {

    private final RiderServiceImpl riderService;

    public RiderController(RiderServiceImpl riderService){
        this.riderService = riderService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRider(@RequestBody RiderRequest request){
        return riderService.createRider(request);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getRiderById(@PathVariable Long id){
        return riderService.getRiderById(id);
    }
    
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllRiders() {
        return riderService.getAllRiders();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRider(@PathVariable Long id) {
        return riderService.deleteRider(id);
    }

    // Starting ride request process
}
