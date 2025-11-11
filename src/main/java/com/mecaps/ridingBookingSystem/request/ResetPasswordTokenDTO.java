package com.mecaps.ridingBookingSystem.request;

import com.mecaps.ridingBookingSystem.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ResetPasswordTokenDTO {
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private String newPassword;

}
