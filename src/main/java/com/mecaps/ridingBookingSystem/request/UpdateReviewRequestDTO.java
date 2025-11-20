package com.mecaps.ridingBookingSystem.request;

import lombok.Data;

@Data
public class UpdateReviewRequestDTO {
    private Long reviewId;
    private Integer rating;
    private String comment;
}
