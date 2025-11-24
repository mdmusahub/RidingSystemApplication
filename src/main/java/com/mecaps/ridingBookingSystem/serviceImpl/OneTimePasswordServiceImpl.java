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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
/**
 * Service implementation for generating and validating One-Time Passwords (OTP) .
 * OTP Will be Used to Start Ride from driver End.
 * Handles OTP creation for ride requests and validating user-entered OTPs.
 */
@Service
@Slf4j
public class OneTimePasswordServiceImpl implements OneTimePasswordService {

    private final OneTimePasswordRepository oneTimePasswordRepository;
    private final RiderRepository riderRepository;
    private final RideRequestsRepository rideRequestsRepository;

    public OneTimePasswordServiceImpl(OneTimePasswordRepository oneTimePasswordRepository, RiderRepository riderRepository, RideRequestsRepository rideRequestsRepository) {
        this.oneTimePasswordRepository = oneTimePasswordRepository;
        this.riderRepository = riderRepository;
        this.rideRequestsRepository = rideRequestsRepository;
    }
    /**
     * Creates a new OTP for a given ride request.
     * Fetches the associated rider and ride request,
     * generates a random OTP and saves it in the database.
     *
     * @param newRideRequest the ride request for which OTP is being generated
     * @return saved OneTimePassword entity
     * @throws RiderNotFoundException if the rider does not exist
     * @throws RideRequestNotFoundException if the ride request does not exist
     */
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
    /**
     * Validates the OTP entered by the user.
     * If the OTP matches, it deletes the OTP entry and returns true.
     *
     * @param enteredOtp the OTP entered by the user
     * @param otp the saved OTP record from database
     * @return true if OTP is valid, false otherwise
     */
    @Override
    public boolean validateOtp(String enteredOtp, OneTimePassword otp) {
        if (enteredOtp.equals(otp.getOtpCode())) {
            oneTimePasswordRepository.deleteById(otp.getId());
            log.info("OTP Validated and Deleted Successfully");
            return true;
        } else {
            return false;
        }
    }
}
