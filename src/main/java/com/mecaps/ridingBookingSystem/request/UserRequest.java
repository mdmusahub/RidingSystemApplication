package com.mecaps.ridingBookingSystem.request;

import com.mecaps.ridingBookingSystem.entity.Role;
<<<<<<< HEAD
import jakarta.persistence.ManyToOne;
=======
import jakarta.annotation.Nullable;
>>>>>>> 464504f5b5625b11d62389637831f1f1f688e794
import lombok.Data;

import java.util.Optional;

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
