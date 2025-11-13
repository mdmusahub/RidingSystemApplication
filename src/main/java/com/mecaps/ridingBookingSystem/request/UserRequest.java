package com.mecaps.ridingBookingSystem.request;

import com.mecaps.ridingBookingSystem.entity.Role;

import jakarta.annotation.Nullable;
import lombok.Data;



@Data
public class UserRequest {
    private String fullName;
    private String email;
    private Long phone;
    private String password;
    private Role role;

    @Nullable
    private DriverRequest driverRequest;
    @Nullable
    private RiderRequest riderRequest;
}
