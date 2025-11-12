package com.mecaps.ridingBookingSystem.request;

import lombok.Data;

@Data
public class RideCancellationRequest {
    private Long rideRequestId;
    private String cancelledBy;
    private String reason;
}
