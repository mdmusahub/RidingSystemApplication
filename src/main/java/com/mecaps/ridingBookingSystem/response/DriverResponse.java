package com.mecaps.ridingBookingSystem.response;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.entity.User;

public class DriverResponse {

    private Long id;
    private String licenseNumber;
    private String vehicleNumber;
    private String vehicleModel;
    private User userId;

    public DriverResponse(Driver driver) {
        this.id = driver.getId();
        this.licenseNumber = driver.getLicenseNumber();
        this.vehicleNumber = driver.getVehicleNumber();
        this.vehicleModel = driver.getVehicleModel();
        this.userId = driver.getUserId();
    }
}
