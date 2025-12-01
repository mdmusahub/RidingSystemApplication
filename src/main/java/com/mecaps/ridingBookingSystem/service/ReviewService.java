package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.request.ReviewRequestDTO;
import com.mecaps.ridingBookingSystem.request.UpdateReviewRequestDTO;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    ResponseEntity<?> submitReview(ReviewRequestDTO reviewRequestDTO);

    ResponseEntity<?> updateReview(UpdateReviewRequestDTO updateReviewRequestDTO);
}
