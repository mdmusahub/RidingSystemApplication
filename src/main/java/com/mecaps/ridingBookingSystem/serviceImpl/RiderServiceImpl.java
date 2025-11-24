package com.mecaps.ridingBookingSystem.serviceImpl;


import com.mecaps.ridingBookingSystem.entity.Rider;
import com.mecaps.ridingBookingSystem.exception.RiderNotFoundException;
import com.mecaps.ridingBookingSystem.exception.UserNotFoundException;
import com.mecaps.ridingBookingSystem.repository.RiderRepository;
import com.mecaps.ridingBookingSystem.repository.UserRepository;
import com.mecaps.ridingBookingSystem.request.RiderRequest;
import com.mecaps.ridingBookingSystem.response.RiderResponse;
import com.mecaps.ridingBookingSystem.service.RiderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/**
 * Service implementation for managing Rider-related operations.
 * Supports creating a rider profile, fetching rider details,
 * retrieving all riders, and deleting a rider.
 */

@Service
public class RiderServiceImpl implements RiderService {

    private final UserRepository userRepository;
    private final RiderRepository riderRepository;

    public RiderServiceImpl(RiderRepository riderRepository, UserRepository userRepository) {
        this.riderRepository = riderRepository;
        this.userRepository = userRepository;
    }
    /**
     * Creates a new rider linked to an existing user.
     * A User can be RIDER , DRIVER OR ADMIN.
     * When USer Select ROLE Rider a rider is create in DB.
     * @param request contains the userId to map with the rider
     * @return success response with created Rider details
     * @throws UserNotFoundException if the user does not exist
     */
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
                        "status", "true"
                )
        );
    }
    /**
     * Fetches a rider by their ID.
     * @param id rider ID
     * @return rider details wrapped in a response entity
     * @throws RiderNotFoundException if no rider exists with the given ID
     */
    @Override
    public ResponseEntity<?> getRiderById(Long id) {
        Rider rider = riderRepository.findById(id)
                .orElseThrow(() -> new RiderNotFoundException("RIDER NOT FOUND"));

        RiderResponse riderResponse = new RiderResponse(rider);
        return ResponseEntity.ok(Map.of(
                "message", "Rider found successfully",
                "body", riderResponse,
                "success", true
        ));
    }
    /**
     * Retrieves all riders from Database.
     * Admin Usage Only
     * @return list of RiderResponse objects
     */
    @Override
    public ResponseEntity<?> getAllRiders() {
        List<Rider> riders = riderRepository.findAll();
        List<RiderResponse> riderResponseList = riders.stream().map(RiderResponse::new).toList();

        return ResponseEntity.ok(riderResponseList);
    }
    /**
     * Deletes a rider by ID.
     * @param id rider ID
     * @return success message on deletion
     * @throws RiderNotFoundException if rider does not exist
     */

    @Override
    public ResponseEntity<?> deleteRider(Long id) {
        Rider rider = riderRepository.findById(id)
                .orElseThrow(() -> new RiderNotFoundException("Rider not found with given id : " + id));
        riderRepository.delete(rider);

        return ResponseEntity.ok("DELETED");
    }
}
