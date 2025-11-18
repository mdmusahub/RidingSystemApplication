package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.Review;
import com.mecaps.ridingBookingSystem.entity.Rides;
import com.mecaps.ridingBookingSystem.entity.User;
import com.mecaps.ridingBookingSystem.exception.RideNotFoundException;
import com.mecaps.ridingBookingSystem.repository.ReviewRepository;
import com.mecaps.ridingBookingSystem.repository.RideRepository;
import com.mecaps.ridingBookingSystem.repository.UserRepository;
import com.mecaps.ridingBookingSystem.request.ReviewRequestDTO;
import com.mecaps.ridingBookingSystem.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;





@Service
public class ReviewServiceImp implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private  final RideRepository rideRepository;

    public ReviewServiceImp(ReviewRepository reviewRepository, UserRepository userRepository, RideRepository rideRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
    }

    public ResponseEntity<?> submitReview(ReviewRequestDTO requestDTO) {
        Long rideId = requestDTO.getRideId();

        Rides ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        User reviewer = userRepository.findById(requestDTO.getReviewerId()).orElseThrow(() -> new RideNotFoundException("Ride not found"));

        User reviewee = userRepository.findById(requestDTO.getRevieweeId()).orElseThrow(() -> new RuntimeException(" Reviewee not found"));

        Long riderId = ride.getRiderId().getId();
        Long driverId = ride.getDriverId().getId();
        Integer rating = requestDTO.getRating();
        String comment = requestDTO.getComment();

        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body(Map.of("error", "rating must be between 1 to 5"));
        }

        if (reviewer.getId().equals(riderId)){
            reviewee = ride.getDriverId().getUserId(); // rider reviews driver
        } else if (reviewer.getId().equals(driverId)) {
            reviewee = ride.getRiderId().getUserId(); // driver reviews rider
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only rider or driver of this ride can submit a review"));
        }


        Long reviewerId = requestDTO.getReviewerId();



        Review review = new Review();
        review.setRideId(ride);
        review.setReviewerId(reviewer);
        review.setRevieweeId(reviewee);
        review.setRating(rating);
        review.setComment(comment);
        Review saved = reviewRepository.save(review);


        return ResponseEntity.ok("Review Submitted Succesfully");
    }



}
