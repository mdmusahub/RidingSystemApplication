package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.OneTimePassword;

public interface OneTimePasswordService {
    OneTimePassword createOtp(Long riderId, Long rideRequestId);

    boolean validateOtp(String enteredOtp, OneTimePassword otp);

    void deleteOtp(Long id);
}
