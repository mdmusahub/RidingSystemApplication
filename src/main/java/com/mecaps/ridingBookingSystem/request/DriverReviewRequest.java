package com.mecaps.ridingBookingSystem.request;

import lombok.Data;

@Data
public class DriverReviewRequest {
    private Long rideId;
    private Long driverId;
    private Long riderId;
    private Integer rating;
    private String comment;
}
