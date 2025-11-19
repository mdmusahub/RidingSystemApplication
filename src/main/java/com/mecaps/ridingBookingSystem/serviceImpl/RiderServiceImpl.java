package com.mecaps.ridingBookingSystem.serviceImpl;


import com.mecaps.ridingBookingSystem.entity.Rider;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.exception.UserNotFoundException;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.repository.UserRepository;
import com.mecaps.ridingBookingSystem.request.RiderRequest;
import com.mecaps.ridingBookingSystem.response.RiderResponse;
import com.mecaps.ridingBookingSystem.service.RiderService;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RiderServiceImpl implements RiderService {

    private final UserRepository userRepository;
    private final RiderRepository riderRepository;

    public RiderServiceImpl(RiderRepository riderRepository,UserRepository userRepository){
        this.riderRepository = riderRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('USER')")
    @Override
    public ResponseEntity<?> createRider(RiderRequest request) {
        Rider rider = new Rider();

        rider.setUserId(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("USER NOT FOUND")));

        Rider save = riderRepository.save(rider);
        RiderResponse riderResponse = new RiderResponse(save);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Rider created successfully",
                        "body", riderResponse,
                        "status","true"
                )
        );
    }
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Override
    public ResponseEntity<?> getRiderById(Long id) {
        Rider rider = riderRepository.findById(id)
                .orElseThrow(() -> new RiderNotFoundException("RIDER NOT FOUND"));

        RiderResponse riderResponse = new RiderResponse(rider);
        return ResponseEntity.ok(Map.of(
                "message","Rider found successfully",
                "body",riderResponse,
                "success",true
        ));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<?> getAllRiders() {
        List<Rider> riders = riderRepository.findAll();
        List<RiderResponse> riderResponseList = riders.stream().map(RiderResponse::new).toList();

        return ResponseEntity.ok(riderResponseList);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ResponseEntity<?> deleteRider(Long id) {
        Rider rider = riderRepository.findById(id)
                .orElseThrow(()-> new RiderNotFoundException("Rider not found with given id : " + id));
        riderRepository.delete(rider);

        return ResponseEntity.ok("DELETED");
    }
}
