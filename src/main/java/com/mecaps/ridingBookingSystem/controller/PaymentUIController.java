
package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.entity.Payment;
import com.mecaps.ridingBookingSystem.exception.PaymentNotFoundException;
import com.mecaps.ridingBookingSystem.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PaymentUIController {


    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;


    public PaymentUIController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/payment/payNow")
    public String paymentPage(@RequestParam Long rideId, Model model) {
        Payment payment = paymentRepository.findByRideId_Id(rideId);
        if (payment == null) {
            throw new PaymentNotFoundException("Payment not created for ride: " + rideId);
        }
        model.addAttribute("payment", payment);
        model.addAttribute("key", razorpayKeyId);
        return "paymentPage";
    }

    @GetMapping("/payment/successPage")
    public String successPage() {
        return "successPage";
    }

}

