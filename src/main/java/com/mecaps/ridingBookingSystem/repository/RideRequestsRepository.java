package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.entity.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRequestsRepository extends JpaRepository<RideRequests, Long> {
    List<RideRequests> findByStatus(RideStatus status);

    @Query("SELECT r FROM RideRequests r WHERE r.status = : status AND r.expiresAt < :currentTime")
    List<RideRequests> findExpiredRequests (RideStatus status, String currentTime);
}
