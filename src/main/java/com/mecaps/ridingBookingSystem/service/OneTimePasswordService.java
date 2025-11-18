package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.OneTimePassword;
import com.mecaps.ridingBookingSystem.entity.RideRequests;

public interface OneTimePasswordService {

    OneTimePassword createOtp(RideRequests rideRequest);

    boolean validateOtp(String enteredOtp, OneTimePassword otp);


    void deleteOtp(Long id);


}

