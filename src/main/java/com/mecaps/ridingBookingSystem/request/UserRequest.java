package com.mecaps.ridingBookingSystem.request;

import com.mecaps.ridingBookingSystem.entity.Role;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.Optional;

@Data
public class UserRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private Role role;

    @Nullable
    private DriverRequest driverRequest;
    @Nullable
    private RiderRequest riderRequest;
}
