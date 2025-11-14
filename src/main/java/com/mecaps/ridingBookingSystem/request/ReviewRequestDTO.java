package com.mecaps.ridingBookingSystem.request;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private Long rideId;
    private Long reviewerId;
    private Long revieweeId;
    private Integer rating;
    private String comment;

}
