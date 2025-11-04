package com.mecaps.ridingBookingSystem.request;

import com.mecaps.ridingBookingSystem.entity.Driver;
import com.mecaps.ridingBookingSystem.entity.Rider;
import com.mecaps.ridingBookingSystem.entity.Role;
import lombok.Data;

import java.util.Optional;

@Data
public class UserRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private Role role;
//    private Optional<Driver> driverDetails;
//    private Optional<Rider> riderDetails;
}
