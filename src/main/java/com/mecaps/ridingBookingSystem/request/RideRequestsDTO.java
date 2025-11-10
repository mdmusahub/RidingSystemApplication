package com.mecaps.ridingBookingSystem.request;

import lombok.Data;

@Data
public class RideRequestsDTO {

    private Long riderId;

    private Double pickupLat;

    private Double pickupLng;

    private Double dropLat;

    private Double dropLng;
}
