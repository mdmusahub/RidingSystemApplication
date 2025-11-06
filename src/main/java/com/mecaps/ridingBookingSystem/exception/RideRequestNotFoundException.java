package com.mecaps.ridingBookingSystem.exception;

public class RideRequestNotFoundException extends RuntimeException{
    public RideRequestNotFoundException(String message) {
        super(message);
    }
}
