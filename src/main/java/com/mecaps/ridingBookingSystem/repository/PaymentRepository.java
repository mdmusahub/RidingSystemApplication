package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentRepository extends JpaRepository<Payment, Long> {


    Payment findByRideId_Id(Long rideId);

}
