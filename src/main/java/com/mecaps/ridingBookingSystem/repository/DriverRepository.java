package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@Repository
public interface DriverRepository extends JpaRepository <Driver, Long>{

    @Query("SELECT d FROM Driver d WHERE d.id = ?1")
    Optional<Driver> findById(Long id);

    @Query("SELECT d FROM Driver d WHERE d.licenseNumber = ?1")
    Optional<Driver> findByLicenseNumber(String licenseNumber);

    @Query("SELECT d FROM Driver d WHERE d.vehicleNumber = ?1")
    Optional<Driver> findByVehicleNumber(String vehicleNumber);

    List<Driver> findByIsAvailableTrue();
}
