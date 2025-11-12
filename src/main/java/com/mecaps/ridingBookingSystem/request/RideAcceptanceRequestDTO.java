package com.mecaps.ridingBookingSystem.request;

import lombok.Data;

@Data
public class RideAcceptanceRequestDTO {
    private Long rideRequestId;
    private Long driverId;
}
