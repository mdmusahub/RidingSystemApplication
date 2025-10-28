package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.request.UserRequest;
import com.mecaps.ridingBookingSystem.response.UserResponse;
import com.mecaps.ridingBookingSystem.entity.User;
import com.mecaps.ridingBookingSystem.exception.UserAlreadyExistsExseption;
import com.mecaps.ridingBookingSystem.exception.UserNotFoundException;
import com.mecaps.ridingBookingSystem.repository.UserRepository;
import com.mecaps.ridingBookingSystem.service.UserService;
import jakarta.persistence.EntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EntityManager entityManager;


    public UserServiceImpl(UserRepository userRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Override
    public ResponseEntity<?> createUser(UserRequest request) {
        Optional<User> existingEmail = userRepository.findByEmail(request.getEmail());
        if (existingEmail.isPresent()) {
            throw new UserAlreadyExistsExseption("User with this email is already exists");
        }

        Optional<User> existingPhone = userRepository.findByPhone(request.getPhone());
        if (existingPhone.isPresent()){
            throw new UserAlreadyExistsExseption("User with this phone number is already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        User save = userRepository.save(user);

        UserResponse userResponse = new UserResponse(save);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message","User created successfully",
                        "body",userResponse,
                        "success","true"
                ));

    }

    @Override
    public ResponseEntity<?> getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message","User found successfully",
                        "body", user,
                        "success", "true"));
    }

    @Override
    public ResponseEntity<?> getAllUser(){
        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponseList = userList.stream()
                .map(UserResponse::new).toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message","Found List of users successfully",
                        "body",userResponseList,
                        "success","true"));
    }

    @Override
    public ResponseEntity<?> updateUser(Long id, UserRequest request){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());

        User save = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "User updated successfully",
                        "body",save,
                        "success","true"));
    }

    @Override
    public ResponseEntity<?> deleteUser(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found"));
        userRepository.delete(user);

        return ResponseEntity.ok("DELETED");
    }

}
