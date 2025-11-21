package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.entity.RideHistory;
import com.mecaps.ridingBookingSystem.entity.Rider;
import com.mecaps.ridingBookingSystem.entity.Rides;
import com.mecaps.ridingBookingSystem.exception.DriverNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RideHistoryNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.repository.DriverRepository;
import com.mecaps.ridingBookingSystem.repository.RideHistoryRepository;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.service.RideHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class RideHistoryServiceImpl implements RideHistoryService {
    private final RideHistoryRepository rideHistoryRepository;
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;

    public RideHistoryServiceImpl(RideHistoryRepository rideHistoryRepository, RiderRepository riderRepository, DriverRepository driverRepository) {
        this.rideHistoryRepository = rideHistoryRepository;
        this.riderRepository = riderRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public ResponseEntity<?> getRideHistoryById(Long id){
        RideHistory rideHistory = rideHistoryRepository.findById(id)
                .orElseThrow(() -> new RideHistoryNotFoundException("Ride History not found for the given rideId=" + id));
        return ResponseEntity.ok().body(rideHistory);
    }

    @Override
    public ResponseEntity<?> getAllRideHistory(){
        return ResponseEntity.ok().body(rideHistoryRepository.findAll());
    }

    @Override
    public ResponseEntity<?> getAllRidesHistoryForRider(Long riderId) {
        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new RiderNotFoundException("Rider not found for the given ID: " + riderId));

        List<RideHistory> rideHistoryList = rideHistoryRepository.findAll();
        List<RideHistory> ridesHistoryForRider = rideHistoryList.stream()
                .filter(rideHistory -> rideHistory.getRide().getRiderId().equals(rider)).toList();

        return ResponseEntity.ok().body(Map.of(
                "message","Ride History for rider with id: " + riderId,
                "history",ridesHistoryForRider,
                "success",true
        ));
    }

    @Override
    public ResponseEntity<?> getAllRidesHistoryForDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found for the given ID: " + driverId));

        List<RideHistory> rideHistoryList = rideHistoryRepository.findAll();
        List<RideHistory> ridesHistoryForDriver = rideHistoryList.stream()
                .filter(rideHistory -> rideHistory.getRide().getDriverId().equals(driver)).toList();

        return ResponseEntity.ok().body(Map.of(
                "message","Ride History for driver with id: " + driverId,
                "history",ridesHistoryForDriver,
                "success",true
        ));
    }

    @Override
    public RideHistory createRideHistory(Rides newRide){
        RideHistory rideHistory = RideHistory.builder()
                .ride(newRide)
                .summary(String.format(
                        "Ride completed on %s, covering %.1f km in %d mins. Total fare: Rs.%.2f. Driven by %s.",
                        newRide.getEndTime().format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' hh:mm a")),
                        newRide.getDistanceKm(),
                        Duration.between(newRide.getStartTime(),newRide.getEndTime()).toMinutes(),
                        newRide.getFare(),
                        newRide.getDriverId().getUserId().getFullName()
                ))
                .build();

        return rideHistoryRepository.save(rideHistory);
    }

    @Override
    public ResponseEntity<?> deleteRideHistoryById(Long id){
        rideHistoryRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Ride History deleted successfully for the given ID:" + id);
    }
}
