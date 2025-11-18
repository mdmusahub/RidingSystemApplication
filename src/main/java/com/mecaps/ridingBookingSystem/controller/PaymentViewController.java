package com.mecaps.ridingBookingSystem.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
public class PaymentViewController {

    @Value("${razorpay.key_id}")
    private String razorpayKey;

    @GetMapping("/pay")
    public String showPaymentPage(@RequestParam Long rideId,
                                  @RequestParam Double amount,
                                  Model model) {

        model.addAttribute("rideId", rideId);
        model.addAttribute("amount", amount);
        model.addAttribute("razorpayKey", razorpayKey);

        return "payment";
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String paymentId,
                                 @RequestParam String orderId,
                                 @RequestParam String signature,
                                 Model model) {

        model.addAttribute("paymentId", paymentId);
        model.addAttribute("orderId", orderId);

        return "payment-success";
    }
}