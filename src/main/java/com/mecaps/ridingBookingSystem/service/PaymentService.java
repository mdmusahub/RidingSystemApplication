package com.mecaps.ridingBookingSystem.service;

import com.mecaps.ridingBookingSystem.entity.Payment;
import com.mecaps.ridingBookingSystem.request.PaymentRequestDTO;
import com.razorpay.RazorpayException;
import netscape.javascript.JSObject;
import org.json.JSONObject;

public interface PaymentService {

    JSONObject createPaymentOrder(PaymentRequestDTO request) throws RazorpayException;
    Payment verifyPayment(String paymentId, String orderId, String signature);
}
