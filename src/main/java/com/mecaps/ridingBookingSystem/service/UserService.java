package com.mecaps.ridingBookingSystem.service;


import com.mecaps.ridingBookingSystem.request.UserRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?>createUser(UserRequest request);

    ResponseEntity<?> getUserById(Long id);

    ResponseEntity<?> getAllUser();

    ResponseEntity<?> updateUser(Long  id, UserRequest userRequest);

    ResponseEntity<?> deleteUser(Long id);
}
