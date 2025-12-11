package com.mecaps.ridingBookingSystem.request;

import com.mecaps.ridingBookingSystem.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class DriverRequest {
//    private Long userId;
    private String licenseNumber;
    private String vehicleNumber;
    private String vehicleModel;

}
