package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.RideHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RideHistoryRepository extends JpaRepository<RideHistory,Long> {
    Optional<RideHistory> findByRideId(Long rideId);
}
