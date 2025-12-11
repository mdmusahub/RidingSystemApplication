package com.mecaps.ridingBookingSystem.response;

import com.mecaps.ridingBookingSystem.entity.RideCancellation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideCancellationResponse {
    private Long id;
    private Long rideRequestId;
    private Long driverId;
    private String cancelledBy;
    private String reason;

    public RideCancellationResponse(RideCancellation rideCancellation){
        this.id = rideCancellation.getId();
        this.rideRequestId = rideCancellation.getRideRequest().getId();
        this.driverId = rideCancellation.getDriver().getId();
        this.cancelledBy = rideCancellation.getCancelledBy();
        this.reason = rideCancellation.getReason();
    }
}
