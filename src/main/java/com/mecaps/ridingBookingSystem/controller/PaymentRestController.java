package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.entity.Payment;
import com.mecaps.ridingBookingSystem.entity.PaymentStatus;
import com.mecaps.ridingBookingSystem.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentRestController {

    private final PaymentService paymentService;

    public PaymentRestController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Frontend calls this to create a Razorpay order ID
    @PostMapping("/createOrder")
    public ResponseEntity<String> createOrder(@RequestParam Long rideId) throws RazorpayException {
        String razorpayOrderId = paymentService.initiatePayment(rideId);
        return ResponseEntity.ok(razorpayOrderId);
    }

    // complete by cash
    @PostMapping("/completePayment")
    public ResponseEntity<String> completePayment(@RequestParam Long rideId, @RequestParam String paymentMethod) {
        String response = paymentService.completePayment(rideId, paymentMethod);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/success")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, String> payload)
            throws RazorpayException {

        Long rideId = payload.get("rideId") != null ? Long.valueOf(payload.get("rideId")) : null;

        String orderId = payload.getOrDefault("orderId", payload.get("razorpay_order_id"));
        String paymentId = payload.getOrDefault("paymentId", payload.get("razorpay_payment_id"));
        String signature = payload.getOrDefault("signature", payload.get("razorpay_signature"));

        boolean isValid = paymentService.verifyPayment(orderId, paymentId, signature);

        Map<String, Object> response = new HashMap<>();
        response.put("status", isValid);
        response.put("message", isValid ? "Payment Verified Successfully!" : "Verification Failed!");
        response.put("razorpay_order_id", orderId);
        response.put("razorpay_payment_id", paymentId);
        response.put("rideId", rideId);


        if (rideId != null) {
            Payment payment = paymentService.getPaymentByRideId(rideId);
            if (payment != null) {
                response.put("paymentStatus", payment.getPaymentStatus());
            }
        }

        return ResponseEntity.ok(response);
    }

    // Small helper for test page to return amount and key
    @GetMapping("/get-ride/{rideId}")
    public ResponseEntity<Map<String, Object>> getRideForPayment(@PathVariable Long rideId) {
        Map<String, Object> map = new HashMap<>();
        Payment payment = paymentService.getPaymentByRideId(rideId);
        if (payment != null) {
            map.put("amount", payment.getAmount());
            map.put("paymentStatus", payment.getPaymentStatus());
        } else {
            map.put("amount", 0);
            map.put("paymentStatus", PaymentStatus.PENDING);
        }
        map.put("key", paymentService.getRazorpayKey());
        return ResponseEntity.ok(map);
    }
}




