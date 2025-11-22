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
/**
 * Service implementation for handling ride history records.
 * When Driver mark ride COMPLETED a ride history will be saved in Database.
 * Supports retrieving, creating, filtering, and deleting ride history entries for both riders and drivers.
 */
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


// get ride history from Db by its Id.
    @Override
    public ResponseEntity<?> getRideHistoryById(Long id){
        RideHistory rideHistory = rideHistoryRepository.findById(id)
                .orElseThrow(() -> new RideHistoryNotFoundException("Ride History not found for the given rideId=" + id));
        return ResponseEntity.ok().body(rideHistory);
    }
// All Rides Data from DataBase. Admin Usage Only.
    @Override
    public ResponseEntity<?> getAllRideHistory(){
        return ResponseEntity.ok().body(rideHistoryRepository.findAll());
    }
    /**
     * Retrieves all ride history entries for a specific rider.
     *
     * @param riderId ID of the rider
     * @return filtered list of ride history records for that rider
     * @throws RiderNotFoundException if rider does not exist
     */
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

    /**
     * Retrieves all ride history entries for a specific driver.
     *
     * @param driverId ID of the driver
     * @return filtered list of ride history records for that driver
     * @throws DriverNotFoundException if driver does not exist
     */

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
    /**
     * Creates and saves a new ride history entry for a completed ride.
     * Generates a  history summary containing completion date/time,distance covered,total ride duration
     * @param newRide the completed ride entity
     * @return saved RideHistory entity
     */
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
    /**
     * Deletes a ride history entry by its ID.
     * @param id ride history ID
     * @return 204 NO CONTENT with success message
     */
    @Override
    public ResponseEntity<?> deleteRideHistoryById(Long id){
        rideHistoryRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Ride History deleted successfully for the given ID:" + id);
    }
}
