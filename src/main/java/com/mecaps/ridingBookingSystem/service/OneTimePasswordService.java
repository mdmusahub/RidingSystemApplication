package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.OneTimePassword;
import com.mecaps.ridingBookingSystem.request.OtpGenerateRequest;

public interface OneTimePasswordService {
    OneTimePassword createOtp(Long riderId, Long rideRequestId);

    void deleteOtp(Long id);
}
