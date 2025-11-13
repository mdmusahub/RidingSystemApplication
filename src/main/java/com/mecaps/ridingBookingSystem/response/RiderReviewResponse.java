package com.mecaps.ridingBookingSystem.response;

import com.mecaps.ridingBookingSystem.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RiderReviewResponse {
    private Long id;
    private Long rideId;
    private RiderResponse riderResponse;
    private DriverResponse driverResponse;
    private Integer rating;
    private LocalDateTime createdAt;

    public RiderReviewResponse(Review riderReview){
        this.id = riderReview.getId();
        this.rideId = riderReview.getRideId().getId();
        this.riderResponse = new RiderResponse(riderReview.getRideId().getRiderId());
        this.driverResponse = new DriverResponse(riderReview.getRideId().getDriverId());
        this.rating = riderReview.getRating();
        this.createdAt = riderReview.getCreatedAt();
    }
}
