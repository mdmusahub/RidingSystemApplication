package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.Payment;
import com.razorpay.RazorpayException;

public interface PaymentService {



    String initiatePayment(Long rideId);
    String completePayment(Long rideId, String paymentMethod);
    public boolean verifyPayment(String razorpayOrderId,
                                 String razorpayPaymentId,
                                 String razorpaySignature)
            throws RazorpayException;

    Payment getPaymentByRideId(Long rideId);
    String getRazorpayKey();

    Payment save(Payment payment);

}
