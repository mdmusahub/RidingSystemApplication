package com.mecaps.ridingBookingSystem.service;

public interface GoogleMapService {
    public Double getDistanceInKm(String origin, String destination);
    public double calculateFare(String origin, String destination);
}
