package com.mecaps.ridingBookingSystem.request;

import lombok.Data;

@Data
public class RiderReviewRequest {
    private Long rideId;
    private Long riderId;
    private Long driverId;
    private Integer rating;
    private String comment;
}
