package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.entity.Payment;
import com.mecaps.ridingBookingSystem.request.PaymentRequestDTO;
import com.mecaps.ridingBookingSystem.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequestDTO request) {
        try {
            return ResponseEntity.ok(paymentService.createPaymentOrder(request));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating Razorpay order: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> body) {

        Payment payment = paymentService.verifyPayment(
                body.get("paymentId"),
                body.get("orderId"),
                body.get("signature")
        );

        return ResponseEntity.ok(payment);
    }
}