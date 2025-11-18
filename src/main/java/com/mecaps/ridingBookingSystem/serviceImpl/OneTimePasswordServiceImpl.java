
package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.OneTimePassword;
import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.exception.RideRequestNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.repository.OneTimePasswordRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.service.OneTimePasswordService;
import com.mecaps.ridingBookingSystem.util.OtpUtil;
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
    public OneTimePassword createOtp(RideRequests newRideRequest) {
        OneTimePassword otp = OneTimePassword.builder()
                .otpCode(OtpUtil.generateOtp())
                .riderId(riderRepository.findById(newRideRequest.getRiderId().getId())
                        .orElseThrow(() -> new RiderNotFoundException("RIDER NOT FOUND")))
                .rideRequest(rideRequestsRepository.findById(newRideRequest.getId())
                        .orElseThrow(() -> new RideRequestNotFoundException("No Such Ride Request Found")))
                .build();

        return oneTimePasswordRepository.save(otp);
    }

    @Override
    public boolean validateOtp(String enteredOtp, OneTimePassword otp) {
        if (enteredOtp.equals(otp.getOtpCode())) {
            deleteOtp(otp.getId());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteOtp(Long id) {
        oneTimePasswordRepository.deleteById(id);
    }
}