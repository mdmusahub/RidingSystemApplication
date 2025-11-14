package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.serviceImpl.GoogleMapServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
public class MapController {
    private final GoogleMapServiceImpl googleMapServiceImpl;

    public MapController(GoogleMapServiceImpl googleMapServiceImpl) {
        this.googleMapServiceImpl = googleMapServiceImpl;
    }

    @GetMapping("/distance")
    public Double getDistance(@RequestParam String origin, @RequestParam String destination) {
        return googleMapServiceImpl.getDistanceInKm(origin, destination);
    }
    @GetMapping("/fare")
    public double calculateFare(@RequestParam String origin, @RequestParam String destination){
        return googleMapServiceImpl.calculateFare(origin, destination);

    }
}
