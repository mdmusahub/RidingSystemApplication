package com.mecaps.ridingBookingSystem.exception;

public class RideHistoryNotFoundException extends RuntimeException {
    public RideHistoryNotFoundException(String message) {
        super(message);
    }
}
