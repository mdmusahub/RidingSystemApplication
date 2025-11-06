package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.entity.RideStatus;
import com.mecaps.ridingBookingSystem.entity.Rider;
import com.mecaps.ridingBookingSystem.exception.RideRequestNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.repository.DriverRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import com.mecaps.ridingBookingSystem.service.RideRequestService;
import com.mecaps.ridingBookingSystem.util.DistanceCalculator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RideRequestServiceImpl implements RideRequestService {

    private final static Double FARE_PER_KM = 10.00;

    private final RideRequestsRepository rideRequestsRepository;
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;

    public RideRequestServiceImpl(RideRequestsRepository rideRequestsRepository,
                                  RiderRepository riderRepository,
                                  DriverRepository driverRepository) {
        this.rideRequestsRepository = rideRequestsRepository;
        this.riderRepository = riderRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public ResponseEntity<?> createRideRequest(RideRequestsDTO request) {
        Rider rider = riderRepository.findById(request.getRiderId())
                .orElseThrow(() -> new RiderNotFoundException
                        ("Rider not found with ID: " + request.getRiderId()));

        RideRequests rideRequest = new RideRequests();

        rideRequest.setRiderId(rider);
        rideRequest.setPickupLat(request.getPickupLat());
        rideRequest.setPickupLng(request.getPickupLng());
        rideRequest.setDropLat(request.getDropLat());
        rideRequest.setDropLng(request.getDropLng());


//        rideRequest.setRequestedAt(LocalDateTime.now().toString());
//        rideRequest.setExpiresAt(LocalDateTime.now().plusMinutes(3).toString());

        rideRequestsRepository.save(rideRequest);

        Double distance = DistanceCalculator.calculateDistance(request.getPickupLat(), request.getPickupLng(),
                request.getDropLat(), request.getDropLng());
        Double fare = distance * FARE_PER_KM;
//
//        List<Driver> nearestDrivers = findNearestDrivers
//                (request.getPickupLat(), request.getPickupLng());

        Map<String, Object> response = new HashMap<>();

        response.put("Message", "Ride request created successfully");
        response.put("rideRequestId", rideRequest.getId());
        response.put("distanceInKM", distance);
        response.put("estimatedFare", fare);
//        response.put("nearestDriversCount", nearestDrivers.size());
        response.put("success", true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<?> confirmRideRequest(Long rideRequestId, Boolean confirmation) {
        RideRequests rideRequest = rideRequestsRepository.findById(rideRequestId)
                .orElseThrow(() -> new RideRequestNotFoundException("No Such Ride Request Found"));

        if (confirmation) {
            rideRequest.setStatus(RideStatus.REQUESTED);
            rideRequest.setRequestedAt(LocalDateTime.now().toString());
            rideRequest.setExpiresAt(LocalDateTime.now().plusMinutes(3).toString());

            rideRequestsRepository.save(rideRequest);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                    "message","Ride Request Confirmed",
                    "body",rideRequest,
                    "success",true
            ));
        } else {
            rideRequestsRepository.deleteById(rideRequestId);
            return ResponseEntity.ok("Ride Request Avoided");
        }
    }

    @Override
    public List<Driver> findNearestAvailableDrivers(RideRequestsDTO request, Integer limit) {
        List<Driver> availableDrivers = driverRepository.findByIsAvailableTrue();

        return availableDrivers.stream().sorted(Comparator.comparingDouble(driver ->
                        DistanceCalculator.calculateDistance(
                                request.getPickupLat(),
                                request.getPickupLng(),
                                driver.getLocation().getLatitude(),
                                driver.getLocation().getLongitude())))
                .limit(limit)
                .toList();
    }
}
