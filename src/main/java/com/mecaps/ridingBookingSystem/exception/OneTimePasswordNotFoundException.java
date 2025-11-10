package com.mecaps.ridingBookingSystem.exception;

public class OneTimePasswordNotFoundException extends RuntimeException {
    public OneTimePasswordNotFoundException(String message) {
        super(message);
    }
}
