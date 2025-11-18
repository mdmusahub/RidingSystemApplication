package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.OneTimePassword;
import com.mecaps.ridingBookingSystem.exception.RideRequestNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.repository.OneTimePasswordRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.service.OneTimePasswordService;
import com.mecaps.ridingBookingSystem.util.OtpUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
public class OneTimePasswordServiceImpl implements OneTimePasswordService {

    private final OneTimePasswordRepository oneTimePasswordRepository;
    private final RiderRepository riderRepository;
    private final RideRequestsRepository rideRequestsRepository;

    public OneTimePasswordServiceImpl(OneTimePasswordRepository oneTimePasswordRepository, RiderRepository riderRepository, RideRequestsRepository rideRequestsRepository) {
        this.oneTimePasswordRepository = oneTimePasswordRepository;
        this.riderRepository = riderRepository;
        this.rideRequestsRepository = rideRequestsRepository;
    }

    @Override
    @PreAuthorize("#riderId == authentication.principal.id or hasRole('ADMIN')")
// Yahan check hoga ki jo riderId pass kiya gaya hai, woh logged-in user ki ID se match karta hai.
    public OneTimePassword createOtp(Long riderId, Long rideRequestId) {
        OneTimePassword otp = OneTimePassword.builder()
                .otpCode(OtpUtil.generateOtp())
                .riderId(riderRepository.findById(riderId)
                        .orElseThrow(() -> new RiderNotFoundException("RIDER NOT FOUND")))
                .rideRequestId(rideRequestsRepository.findById(rideRequestId)
                        .orElseThrow(() -> new RideRequestNotFoundException("No Such Ride Request Found")))
                .build();

        return oneTimePasswordRepository.save(otp);
    }

    @Override
    @PreAuthorize("hasAnyRole('DRIVER','ADMIN')")
// OTP validation primarily driver ya admin karta hai
    public boolean validateOtp(String enteredOtp, OneTimePassword otp) {
        if (enteredOtp.equals(otp.getOtpCode())) {
            deleteOtp(otp.getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOtp(Long id) {
        oneTimePasswordRepository.deleteById(id);
    }
}
