package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.RideHistory;
import com.mecaps.ridingBookingSystem.entity.Rides;
import com.mecaps.ridingBookingSystem.exception.RideHistoryNotFoundException;
import com.mecaps.ridingBookingSystem.repository.RideHistoryRepository;
import com.mecaps.ridingBookingSystem.service.RideHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

@Service
public class RideHistoryServiceImpl implements RideHistoryService {
    private final RideHistoryRepository rideHistoryRepository;

    public RideHistoryServiceImpl(RideHistoryRepository rideHistoryRepository) {
        this.rideHistoryRepository = rideHistoryRepository;
    }

    @Override
    public ResponseEntity<?> getRideHistoryById(Long id){
        RideHistory rideHistory = rideHistoryRepository.findById(id)
                .orElseThrow(() -> new RideHistoryNotFoundException("Ride History not found for the given rideId=" + id));
        return ResponseEntity.ok().body(rideHistory);
    }

    @Override
    public ResponseEntity<?> getAllRidesHistory(){
        return ResponseEntity.ok().body(rideHistoryRepository.findAll());
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
