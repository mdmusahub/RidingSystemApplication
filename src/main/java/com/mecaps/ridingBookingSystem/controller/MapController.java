package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.serviceImpl.GoogleMapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/map")
public class MapController {
    private final GoogleMapService googleMapService;

    public MapController(GoogleMapService googleMapService) {
        this.googleMapService = googleMapService;
    }

    @GetMapping("/distance")
    public Double getDistance(@RequestParam String origin, @RequestParam String destination) {
        return googleMapService.getDistanceInKm(origin, destination);
    }
    @GetMapping("/fare")
    public double calculateFare(@RequestParam String origin, @RequestParam String destination){
        return googleMapService.calculateFare(origin, destination);

    }
}
