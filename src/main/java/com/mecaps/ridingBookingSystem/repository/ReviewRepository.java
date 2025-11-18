package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
