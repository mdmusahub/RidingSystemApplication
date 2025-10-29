package com.mecaps.ridingBookingSystem.exception;

public class DriverAlreadyExistsException extends RuntimeException{

    public DriverAlreadyExistsException(String message){
        super(message);
    }
}
