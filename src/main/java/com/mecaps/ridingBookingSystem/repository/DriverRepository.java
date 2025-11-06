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

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    Optional<Driver> findByVehicleNumber(String vehicleNumber);

    List<Driver> findByIsAvailableTrue();
}
