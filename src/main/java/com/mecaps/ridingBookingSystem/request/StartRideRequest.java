package com.mecaps.ridingBookingSystem.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartRideRequest {
    private Long rideRequestId;
    private String otp;
    private Long driverId;
}
