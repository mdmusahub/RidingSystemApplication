package com.mecaps.ridingBookingSystem.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpGenerateRequest {
    private Long riderId;
    private Long rideRequestId;
}
