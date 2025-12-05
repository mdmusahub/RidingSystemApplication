package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import com.mecaps.ridingBookingSystem.response.DriverResponse;
import com.mecaps.ridingBookingSystem.serviceImpl.DriverServiceImpl;
import com.mecaps.ridingBookingSystem.serviceImpl.RideRequestsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class MapUIController {

    private final RideRequestsServiceImpl rideRequestsService;

    private final DriverServiceImpl driverService;

    public MapUIController(RideRequestsServiceImpl rideRequestsService, DriverServiceImpl driverService) {
        this.rideRequestsService = rideRequestsService;
        this.driverService = driverService;
    }

    @GetMapping("/map")
    public String getMapPage(){
        return "map-page";
    }

    @PostMapping("/map-api/get-fare-distance")
    @ResponseBody
    public ResponseEntity<?> getFareAndDistance(@RequestBody RideRequestsDTO rideRequestsDTO){

        Map<String, Object> result = rideRequestsService.getRideFareAndDistance(rideRequestsDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/map-api/nearby-drivers")
    @ResponseBody
    public ResponseEntity<?> getNearestDriver(@RequestParam("lat") Double lat,
                                              @RequestParam("lng") Double lng,
                                              @RequestParam(value = "limit", required = false, defaultValue = "3") Integer limit){

        RideRequestsDTO requestsDTO = new RideRequestsDTO();
        requestsDTO.setPickupLat(lat);
        requestsDTO.setPickupLng(lng);
        List<DriverResponse> result = driverService.findNearestAvailableDrivers(requestsDTO,limit);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/map-api/request-ride")
    @ResponseBody
    public ResponseEntity<?> createRideRequest(@RequestBody RideRequestsDTO rideRequestsDTO) {
        return rideRequestsService.createRideRequest(rideRequestsDTO);
    }
}
