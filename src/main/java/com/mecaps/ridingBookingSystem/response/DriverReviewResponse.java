package com.mecaps.ridingBookingSystem.response;

import com.mecaps.ridingBookingSystem.entity.Review;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DriverReviewResponse {
    private Long id;
    private Long rideId;
    private DriverResponse driverResponse;
    private RiderResponse riderResponse;
    private Integer rating;
    private LocalDateTime createdAt;

    public DriverReviewResponse(Review driverReview){
        this.id = driverReview.getId();
        this.rideId = driverReview.getRideId().getId();
        this.driverResponse = new DriverResponse(driverReview.getRideId().getDriverId());
        this.riderResponse = new RiderResponse(driverReview.getRideId().getRiderId());
        this.rating = driverReview.getRating();
        this.createdAt = driverReview.getCreatedAt();
    }
}
