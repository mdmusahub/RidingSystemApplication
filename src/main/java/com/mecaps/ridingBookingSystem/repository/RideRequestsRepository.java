package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.entity.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRequestsRepository extends JpaRepository<RideRequests, Long> {
    List<RideRequests> findByStatus(RideStatus status);
}
