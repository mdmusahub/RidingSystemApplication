package com.mecaps.ridingBookingSystem.response;

import com.mecaps.ridingBookingSystem.entity.Role;
import com.mecaps.ridingBookingSystem.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private Role role;

    public UserResponse(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
