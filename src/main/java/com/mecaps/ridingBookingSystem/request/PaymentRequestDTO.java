package com.mecaps.ridingBookingSystem.request;

import com.mecaps.ridingBookingSystem.entity.PaymentMethod;
import lombok.Data;

@Data
public class PaymentRequestDTO {

    private Long rideId;
    private Double amount;
    private PaymentMethod paymentMethod;

}
