package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.RideRequests;
import com.mecaps.ridingBookingSystem.entity.Rider;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.repository.DriverRepository;
import com.mecaps.ridingBookingSystem.repository.RideRequestsRepository;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.request.RideRequestsDTO;
import com.mecaps.ridingBookingSystem.service.RideRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RideRequestServiceImpl implements RideRequestService {


    private final RideRequestsRepository rideRequestsRepository;
    private final RiderRepository riderRepository;
    private final DriverRepository driverRepository;

    public RideRequestServiceImpl(RideRequestsRepository rideRequestsRepository,
                                  RiderRepository riderRepository,
                                  DriverRepository driverRepository){
        this.rideRequestsRepository = rideRequestsRepository;
        this.riderRepository = riderRepository;
        this.driverRepository = driverRepository;
    }

    @Override
   public ResponseEntity<?> createRiderequest(RideRequestsDTO request){
        Rider rider = riderRepository.findById(request.getRiderId())
                .orElseThrow(() -> new RiderNotFoundException
                        ("Rider not found with ID: " + request.getRiderId()));

        RideRequests rideRequests = new RideRequests();

        rideRequests.setRiderId(rider);
        rideRequests.setPickupLat(request.getPickupLat());
        rideRequests.setPickupLng(request.getPickupLng());
        rideRequests.setDropLat(request.getDropLat());
        rideRequests.setDropLng(request.getDropLng());


    }
}
