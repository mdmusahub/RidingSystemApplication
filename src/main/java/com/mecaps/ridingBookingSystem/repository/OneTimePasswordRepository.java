package com.mecaps.ridingBookingSystem.repository;

import com.mecaps.ridingBookingSystem.entity.OneTimePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OneTimePasswordRepository extends JpaRepository<OneTimePassword,Long> {
}
