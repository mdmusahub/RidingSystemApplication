package com.mecaps.ridingBookingSystem.service;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String body);
}
