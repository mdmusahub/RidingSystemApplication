package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverStatusRepository extends JpaRepository<DriverStatus,Long> {
    Optional<DriverStatus> findByIsOnlineTrueAndIsAvailableTrue();
}