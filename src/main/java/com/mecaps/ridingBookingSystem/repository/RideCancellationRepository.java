package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.RideCancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideCancellationRepository extends JpaRepository<RideCancellation,Long> {
}
