package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.entity.Rider;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.repository.DriverRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import com.mecaps.ridingBookingSystem.service.RideRequestService;
import com.mecaps.ridingBookingSystem.util.DistanceCalculator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideRequestServiceImpl implements RideRequestService {


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

        RideRequests rideRequests = new RideRequests();

        rideRequests.setRiderId(rider);
        rideRequests.setPickupLat(request.getPickupLat());
        rideRequests.setPickupLng(request.getPickupLng());
        rideRequests.setDropLat(request.getDropLat());
        rideRequests.setDropLng(request.getDropLng());

        return ResponseEntity.ok("");
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
