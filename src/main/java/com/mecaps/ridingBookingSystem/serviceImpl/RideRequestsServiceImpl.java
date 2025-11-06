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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.http11.Constants.a;

@Service
public class RideRequestsServiceImpl implements RideRequestService {


    private final RideRequestsRepository rideRequestsRepository;
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;

    public RideRequestsServiceImpl(RideRequestsRepository rideRequestsRepository,
                                   RiderRepository riderRepository,
                                   DriverRepository driverRepository){
        this.rideRequestsRepository = rideRequestsRepository;
        this.riderRepository = riderRepository;
        this.driverRepository = driverRepository;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2){

        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private List<Driver> findNearestDrivers(double pickupLat, double pickupLng){

        List<Driver> availableDriver = driverRepository.findByIsAvailableTrue();

        return availableDriver.stream().filter(driver -> driver.getLocation() != null)
                .sorted(Comparator.comparingDouble(driver -> calculateDistance(pickupLat, pickupLng,
                        driver.getLocation().getLatitude(),
                        driver.getLocation().getLongitude()))).limit(3)
                .collect(Collectors.toList());
    }

    @Override
   public ResponseEntity<?> createRideRequest(RideRequestsDTO request){
        Rider rider = riderRepository.findById(request.getRiderId())
                .orElseThrow(() -> new RiderNotFoundException
                        ("Rider not found with ID: " + request.getRiderId()));

        RideRequests rideRequests = new RideRequests();

        rideRequests.setRiderId(rider);
        rideRequests.setPickupLat(request.getPickupLat());
        rideRequests.setPickupLng(request.getPickupLng());
        rideRequests.setDropLat(request.getDropLat());
        rideRequests.setDropLng(request.getDropLng());

        rideRequests.setRequestedAt(LocalDateTime.now().toString());
        rideRequests.setExpiresAt(LocalDateTime.now().plusMinutes(3).toString());



        rideRequestsRepository.save(rideRequests);

        double distance = calculateDistance(request.getPickupLat(), request.getPickupLng(),
        request.getDropLat(), request.getDropLng());
        double fare = distance * 10;

        List<Driver>  nearestDrivers = findNearestDrivers
                (request.getPickupLat(), request.getPickupLng());

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("Message", "Ride request created successfully");
        response.put("rideRequestId", rideRequests.getId());
        response.put("distanceInKM", distance);
        response.put("estimatedFare", fare);
        response.put("nearestDriversCount", nearestDrivers.size());
        response.put("success", true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
