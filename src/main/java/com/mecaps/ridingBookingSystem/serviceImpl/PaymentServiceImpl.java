package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.*;
import com.mecaps.ridingBookingSystem.exception.PaymentNotFoundException;
import com.mecaps.ridingBookingSystem.exception.RideNotFoundException;
import com.mecaps.ridingBookingSystem.repository.PaymentRepository;
import com.mecaps.ridingBookingSystem.repository.RideRepository;
import com.mecaps.ridingBookingSystem.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String secretKey;

    private final PaymentRepository paymentRepository;
    private final RideRepository rideRepository;
    private final RazorpayClient razorpayClient;  // RazorpayClint razorpay inbuilt class
                                                    // help to use payment API

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              RideRepository rideRepository,
                              RazorpayClient razorpayClient) {
        this.paymentRepository = paymentRepository;
        this.rideRepository = rideRepository;
        this.razorpayClient = razorpayClient;
    }

    @Override
    public String initiatePayment(Long rideId) {
        Rides ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id " + rideId));

        // For payment initiation payment record must be created on starting time of ride
        Payment payment = paymentRepository.findByRideId_Id(rideId);
        if (payment == null) {
            throw new PaymentNotFoundException("Payment record not found for ride " + rideId);
        }

        try {  //JSONObject stores in key-value pare
            JSONObject options = new JSONObject();
            int amountPaise = (int) Math.round(ride.getFare() * 100);
            options.put("amount", amountPaise);
            options.put("currency", "INR");
            options.put("receipt", "txn_" + rideId);

            Order order = razorpayClient.orders.create(options);

            // STORE in dedicated field (do not overwrite later)
            payment.setRazorpayOrderId(order.get("id"));
            payment.setPaymentMethod(PaymentMethod.ONLINE);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            paymentRepository.save(payment);

            return order.get("id");

        } catch (RazorpayException e) {
            log.error("Razorpay error while creating order", e);
            throw new RuntimeException("Payment Gateway Error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unknown error while creating order", e);
            throw new RuntimeException("Payment Gateway Error: " + e.getMessage());
        }
    }

    @Override
    public String completePayment(Long rideId, String paymentMethod) {

        // Handel cash payment
        Payment payment = paymentRepository.findByRideId_Id(rideId);
        if (payment == null) throw new PaymentNotFoundException("Payment record not found");

        if (paymentMethod.equalsIgnoreCase("cash")) {
            payment.setPaymentMethod(PaymentMethod.CASH);
            payment.setAmount(payment.getAmount());
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId("CASH_" + UUID.randomUUID());
            paymentRepository.save(payment);
            return "Cash payment completed successfully";
        }

        return "Redirecting to online payment...";
    }


    @Override
    public boolean verifyPayment(String razorpayOrderId,
                                 String razorpayPaymentId,
                                 String razorpaySignature) {

        if (razorpayOrderId == null || razorpayPaymentId == null || razorpaySignature == null) {
            log.warn("verifyPayment called with null values orderId={} paymentId={}", razorpayOrderId, razorpayPaymentId);
            return false;
        }

        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);

            boolean isValid = Utils.verifyPaymentSignature(attributes, secretKey);

            if (isValid) {
                Payment payment = paymentRepository.findAll().stream()
                        .filter(p -> razorpayOrderId.equals(p.getRazorpayOrderId()))
                        .findFirst()
                        .orElseThrow(() -> new PaymentNotFoundException("Payment not found for orderId: " + razorpayOrderId));

                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                payment.setRazorpayPaymentId(razorpayPaymentId);
                payment.setTransactionId(razorpayPaymentId);
                paymentRepository.save(payment);
            }

            return isValid;

        } catch (RazorpayException re) {
            log.error("Razorpay verification exception", re);
            throw new RuntimeException("Signature verification failed: " + re.getMessage());
        } catch (Exception e) {
            log.error("Signature verify error", e);
            throw new RuntimeException("Signature verification failed: " + e.getMessage());
        }
    }


    @Override
    public Payment getPaymentByRideId(Long rideId) {
        return paymentRepository.findByRideId_Id(rideId);
    }

    @Override
    public String getRazorpayKey() {
        return razorpayKeyId;
    }

    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

}
