package com.mecaps.ridingBookingSystem.config;

import com.razorpay.RazorpayClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RazorpayConfig {

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpaySecretKey;

    @Bean
    public RazorpayClient razorpayClient() {
        try {
            log.info("Initializing Razorpay Client...");
            return new RazorpayClient(razorpayKeyId, razorpaySecretKey);
        } catch (Exception e) {
            log.error("Failed to initialize Razorpay Client: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to initialize Razorpay Client", e);
        }
    }
}