package com.mecaps.ridingBookingSystem.request;

import com.mecaps.ridingBookingSystem.entity.Role;
import lombok.Data;

@Data
public class UserRequest {
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private Role role;
}
