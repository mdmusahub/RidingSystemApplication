package com.mecaps.ridingBookingSystem.response;

import com.mecaps.ridingBookingSystem.entity.Rider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiderResponse {
    private Long id;
    private UserResponse userResponse;
    private Float rating;

    public RiderResponse(Rider rider){
        this.id = rider.getId();
        this.userResponse = new UserResponse(rider.getUserId());
        this.rating = rider.getRating();
    }
}
