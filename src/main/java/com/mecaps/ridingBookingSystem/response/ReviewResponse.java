package com.mecaps.ridingBookingSystem.response;

import com.mecaps.ridingBookingSystem.entity.Review;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {
    private Long id;
    private Long rideId;
    private UserResponse reviewer;
    private UserResponse reviewee;
    private Integer rating;
    private LocalDateTime createdAt;

    public ReviewResponse(Review review){
        this.id = review.getId();
        this.rideId = review.getRideId().getId();
        this.reviewer = new UserResponse(review.getReviewerId());
        this.reviewee = new UserResponse(review.getRevieweeId());
        this.rating = review.getRating();
        this.createdAt = review.getCreatedAt();
    }
}
