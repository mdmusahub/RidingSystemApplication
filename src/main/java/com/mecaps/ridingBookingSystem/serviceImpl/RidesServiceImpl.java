package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.*;
import com.mecaps.ridingBookingSystem.exception.*;
import com.mecaps.ridingBookingSystem.repository.*;
import com.mecaps.ridingBookingSystem.request.CompleteRideRequest;
import com.mecaps.ridingBookingSystem.request.StartRideRequest;
import com.mecaps.ridingBookingSystem.response.RidesResponse;
import com.mecaps.ridingBookingSystem.service.RidesService;
import com.mecaps.ridingBookingSystem.util.DistanceFareUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class RidesServiceImpl implements RidesService {

    private final RideRepository rideRepository;
    private final OneTimePasswordServiceImpl oneTimePasswordService;
    private final OneTimePasswordRepository oneTimePasswordRepository;
    private final DriverRepository driverRepository;
    private final RideRequestsRepository rideRequestsRepository;
    private final RiderRepository riderRepository;
    private final RideHistoryServiceImpl rideHistoryService;
    private final PaymentRepository paymentRepository;

    public RidesServiceImpl(
            RideRepository rideRepository,
            OneTimePasswordServiceImpl oneTimePasswordService,
            OneTimePasswordRepository oneTimePasswordRepository,
            DriverRepository driverRepository,
            RideRequestsRepository rideRequestsRepository,
            RiderRepository riderRepository,
            RideHistoryServiceImpl rideHistoryService,
            PaymentRepository paymentRepository
    ) {
        this.rideRepository = rideRepository;
        this.oneTimePasswordService = oneTimePasswordService;
        this.oneTimePasswordRepository = oneTimePasswordRepository;
        this.driverRepository = driverRepository;
        this.rideRequestsRepository = rideRequestsRepository;
        this.riderRepository = riderRepository;
        this.rideHistoryService = rideHistoryService;
        this.paymentRepository = paymentRepository;
    }

    // ⬇ START RIDE PROCESS
    @Override
    public ResponseEntity<?> startRide(StartRideRequest startRideRequest) {

        Driver driver = driverRepository.findById(startRideRequest.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver Not Found"));

        RideRequests newRideRequest = rideRequestsRepository.findById(startRideRequest.getRideRequestId())
                .orElseThrow(() -> new RideRequestNotFoundException("No such Ride Request Found"));

        Rider rider = riderRepository.findById(newRideRequest.getRiderId().getId())
                .orElseThrow(() -> new RiderNotFoundException("Rider Not Found"));

        OneTimePassword otp = oneTimePasswordRepository.findByRideRequestId(startRideRequest.getRideRequestId())
                .orElseThrow(() -> new OneTimePasswordNotFoundException("Otp not found"));

        // OTP Validation
        if (!oneTimePasswordService.validateOtp(startRideRequest.getOtp(), otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }

        // Distance & Fare
        Double distanceKm = DistanceFareUtil.calculateDistance(
                newRideRequest.getPickupLat(), newRideRequest.getPickupLng(),
                newRideRequest.getDropLat(), newRideRequest.getDropLng()
        );
        Double fare = DistanceFareUtil.calculateFare(distanceKm);

        // CREATE RIDE
        Rides ride = Rides.builder()
                .riderId(rider)
                .driverId(driver)
                .rideRequestId(newRideRequest)
                .fare(fare)
                .distanceKm(distanceKm)
                .driverRating(driver.getRating())
                .riderRating(rider.getRating())
                .status(RideStatus.ONGOING)
                .startTime(LocalDateTime.now())
                .build();

        Rides saveRide = rideRepository.save(ride);

        // CREATE PAYMENT (PENDING)
        Payment payment = Payment.builder()
                .rideId(saveRide)
                .amount(fare)
                .paymentMethod(PaymentMethod.ONLINE)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        saveRide.setPayment(payment);
        rideRepository.save(saveRide);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Ride Started Successfully",
                "ride", new RidesResponse(saveRide),
                "amountToPay", fare,
                "paymentStatus", payment.getPaymentStatus(),
                "success", true
        ));
    }

    // ⬇ COMPLETE RIDE PROCESS
    @Override
    public ResponseEntity<?> completeRide(CompleteRideRequest completeRideRequest) {

        Rides ride = rideRepository.findById(completeRideRequest.getRideId())
                .orElseThrow(() -> new RideNotFoundException("Ride not found: " + completeRideRequest.getRideId()));

        Driver driver = driverRepository.findById(completeRideRequest.getDriverId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found: " + completeRideRequest.getDriverId()));

        // Only assigned driver can close the ride
        if (!ride.getDriverId().getId().equals(driver.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of("success",
                            false, "message",
                            "Driver not assigned to this ride!"
                    ));

        Payment payment = paymentRepository.findByRideId_Id(ride.getId());
        if (payment == null) throw new PaymentNotFoundException("Payment Not Found");

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS)
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(Map.of(
                    "success", false,
                    "message", "Payment not completed!",
                    "paymentStatus", payment.getPaymentStatus()
            ));

        // PAYMENT DONE → FINISH RIDE
        ride.setStatus(RideStatus.COMPLETED);
        ride.setEndTime(LocalDateTime.now());

        // SET DRIVER AVAILABLE AGAIN
        driver.getDriverStatus().setIsAvailable(true);
        driverRepository.save(driver);

        rideRepository.save(ride);
        rideHistoryService.createRideHistory(ride);

        return ResponseEntity.ok(Map.of(
                "message", "Ride Completed Successfully",
                "ride", new RidesResponse(ride),
                "success", true
        ));
    }

    @Override
    public Rides getRideById(Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + rideId));
    }
}
