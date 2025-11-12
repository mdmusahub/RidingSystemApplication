package com.mecaps.ridingBookingSystem.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteRideRequest {
    private Long rideId;
    private Long driverId;
}
