package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.*;
import com.mecaps.ridingBookingSystem.exception.ReviewNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RideNotFoundException;
import com.mecaps.ridingBookingSystem.exception.UserNotFoundException;
import com.mecaps.ridingBookingSystem.repository.*;
import com.mecaps.ridingBookingSystem.request.ReviewRequestDTO;
import com.mecaps.ridingBookingSystem.request.UpdateReviewRequestDTO;
import com.mecaps.ridingBookingSystem.response.ReviewResponse;
import com.mecaps.ridingBookingSystem.security.model.CustomUserDetails;
import com.mecaps.ridingBookingSystem.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
public class ReviewServiceImp implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;

    public ReviewServiceImp(ReviewRepository reviewRepository, UserRepository userRepository, RideRepository rideRepository, DriverRepository driverRepository, RiderRepository riderRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.riderRepository = riderRepository;
    }

    @Override
    public ResponseEntity<?> submitReview(ReviewRequestDTO reviewRequestDTO) {
        Rides ride = rideRepository.findById(reviewRequestDTO.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found"));

        // Ride completion validation
        if (!ride.getStatus().equals(RideStatus.COMPLETED)) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Cannot submit review before ride completion",
                            "currentRideStatus", ride.getStatus(),
                            "success", false
                    ));
        }

        User reviewer = userRepository.findById(reviewRequestDTO.getReviewerId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        User reviewee = userRepository.findById(reviewRequestDTO.getRevieweeId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Rider rider = ride.getRiderId();
        Driver driver = ride.getDriverId();

        // Extracting the logged-in user details
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails userDetails = (CustomUserDetails) principal;

        // validating if the review is given by the user that is logged in
        if (!Objects.equals(reviewer.getId(), userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "Reviewer and current logged-in User doesn't match.",
                            "currentUser", userDetails.getUsername(),
                            "success", false
                    ));
        }

        // Rating value range validation
        if (!(reviewRequestDTO.getRating() >= 1 && reviewRequestDTO.getRating() <= 5)) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "rating must be between 1 to 5",
                            "success", false
                    ));
        }

        Review review = Review.builder()
                .rideId(ride)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .rating(reviewRequestDTO.getRating())
                .comment(reviewRequestDTO.getComment())
                .build();
        Review savedReview = reviewRepository.save(review);

        // update reviewee average rating
        this.updateRevieweeAverageRating(savedReview);

        return ResponseEntity.ok()
                .body(Map.of(
                        "message", "Review submitted successfully",
                        "review", new ReviewResponse(savedReview),
                        "success", true
                ));

    }

    @Override
    public ResponseEntity<?> updateReview(UpdateReviewRequestDTO updateReviewRequestDTO) {
        Review review = reviewRepository.findById(updateReviewRequestDTO.getReviewId())
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        Rides ride = review.getRideId();
        Driver driver = ride.getDriverId();
        Rider rider = ride.getRiderId();
        User reviewer = review.getReviewer();
        User reviewee = review.getReviewee();

        // Extracting the logged-in user details
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails userDetails = (CustomUserDetails) principal;

        // validating if the review is given by the user that is logged in
        if (!Objects.equals(review.getReviewer().getId(), userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "This review was not given by "
                                    + userDetails.getUsername()
                                    + ". Only the actual reviewer can update this review.",
                            "success", false
                    ));
        } else {
            review.setRating(updateReviewRequestDTO.getRating());
            review.setComment(updateReviewRequestDTO.getComment());
            Review savedReview = reviewRepository.save(review);

            // update reviewee average rating
            this.updateRevieweeAverageRating(savedReview);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "message", "Review updated successfully",
                            "reviewResponse", new ReviewResponse(savedReview),
                            "success", true
                    ));
        }
    }

    private void updateRevieweeAverageRating(Review review) {
        Rides ride = review.getRideId();
        Driver driver = ride.getDriverId();
        Rider rider = ride.getRiderId();
        User reviewer = review.getReviewer();
        User reviewee = review.getReviewee();

        log.info("Calculating average rating...");
        // updating reviewee average rating according to the updated rating by reviewer
        Double avg = reviewRepository.getAverageRatingByRevieweeId(review.getReviewee().getId());
        Float avgRating = avg == null ? 0f : Math.round(avg.floatValue() * 10f) / 10f;

        if (reviewer.equals(rider.getUserId())) {
            // Rider reviews Driver
            driver.setRating(avgRating);
            driverRepository.save(driver);
        } else {
            // Driver reviews Rider
            rider.setRating(avgRating);
            riderRepository.save(rider);
        }
    }
}
