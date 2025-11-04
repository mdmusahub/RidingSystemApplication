package com.mecaps.ridingBookingSystem.response;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverResponse {

    private Long id;
    private String vehicleNumber;
    private String vehicleModel;
    private UserResponse userResponse;
    private Float rating;
    private Boolean isAvailable;

    public DriverResponse(Driver driver) {
        this.id = driver.getId();
        this.vehicleNumber = driver.getVehicleNumber();
        this.vehicleModel = driver.getVehicleModel();
        this.userResponse = new UserResponse(driver.getUserId());
        this.rating = driver.getRating();
        this.isAvailable = driver.getIsAvailable();
    }
}
