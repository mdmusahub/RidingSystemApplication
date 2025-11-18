package com.mecaps.ridingBookingSystem.response;

import com.mecaps.ridingBookingSystem.entity.Rides;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RidesResponse {
    private Long driverId;
    private Long riderId;
    private Long rideRequestId;
    private Double fare;
    private Double distanceInKm;
    private Float driverRating;
    private Float riderRating;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public RidesResponse(Rides ride){
        this.driverId = ride.getId();
        this.riderId = ride.getRiderId().getId();
        this.rideRequestId = ride.getRideRequestId().getId();
        this.fare = ride.getFare();
        this.distanceInKm = ride.getDistanceKm();
        this.driverRating = ride.getDriverRating();
        this.riderRating = ride.getRiderRating();
        this.status = ride.getStatus().name();
        this.createdAt = ride.getCreatedAt();
        this.startTime = ride.getStartTime();
        this.endTime = ride.getEndTime();
    }
}
