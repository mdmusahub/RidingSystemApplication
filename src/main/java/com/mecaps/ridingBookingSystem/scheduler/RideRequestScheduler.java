package com.mecaps.ridingBookingSystem.scheduler;

import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.entity.RideStatus;
import com.mecaps.ridingBookingSystem.repository.LocationRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import static com.mecaps.ridingBookingSystem.util.DistanceFareUtil.calculateDistance;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RideRequestScheduler {

    private final RideRequestsRepository rideRequestsRepository;
    private final LocationRepository locationRepository;


    public RideRequestScheduler(RideRequestsRepository rideRequestsRepository,
                                LocationRepository locationRepository) {
        this.rideRequestsRepository = rideRequestsRepository;
        this.locationRepository = locationRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void cancelExpiredRequest(){

        LocalDateTime now = LocalDateTime.now();
        List<RideRequests> pendingRequestsList = rideRequestsRepository.findAll();

        for (RideRequests rideRequests : pendingRequestsList){
            if (rideRequests.getStatus() == RideStatus.REQUESTED){
                LocalDateTime expiryTime = LocalDateTime.parse(rideRequests.getExpiresAt());

                boolean driverFound = isDriverAvailableWithin1Km(rideRequests.getPickupLat(),
                        rideRequests.getPickupLng());

                if (driverFound){
                    rideRequests.setStatus(RideStatus.ASSIGNED);
                    System.out.println
                            ("Driver assigned automatically for Ride ID: " + rideRequests.getId());
                }else if(now.isAfter(expiryTime)){
                    rideRequests.setStatus(RideStatus.DENIED);
                    System.out.println("No driver found within 1KM — Ride expired: " + rideRequests.getId());
                }

                rideRequestsRepository.save(rideRequests);

            }
        }

    }


    public boolean isDriverAvailableWithin1Km (double riderLat, double riderLng){
        return locationRepository.findAll().stream().anyMatch(location -> {
            double distance = calculateDistance(riderLat, riderLng,
                    location.getLatitude(), location.getLongitude());
            return distance <= 1.0;
        });
    }
}
