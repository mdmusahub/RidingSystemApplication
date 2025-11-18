package com.mecaps.ridingBookingSystem.serviceImpl;

import com.mecaps.ridingBookingSystem.entity.*;
import com.mecaps.ridingBookingSystem.request.ChangePasswordRequest;
import com.mecaps.ridingBookingSystem.request.UserRequest;
import com.mecaps.ridingBookingSystem.response.UserResponse;
import com.mecaps.ridingBookingSystem.exception.UserAlreadyExistsException;
import com.mecaps.ridingBookingSystem.exception.UserNotFoundException;
import com.mecaps.ridingBookingSystem.repository.UserRepository;
import com.mecaps.ridingBookingSystem.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<?> createUser(UserRequest request) {
        Optional<User> existingEmail = userRepository.findByEmail(request.getEmail());
        if (existingEmail.isPresent()) {
            throw new UserAlreadyExistsException("User with this email is already exists");
        }

        Optional<User> existingPhone = userRepository.findByPhone(request.getPhone());
        if (existingPhone.isPresent()) {
            throw new UserAlreadyExistsException("User with this phone number is already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        if (request.getDriverRequest() != null || request.getRole().equals(Role.DRIVER)) {
            Driver driver = Driver.builder()
                    .userId(user)
                    .licenseNumber(request.getDriverRequest().getLicenseNumber())
                    .vehicleNumber(request.getDriverRequest().getVehicleNumber())
                    .vehicleModel(request.getDriverRequest().getVehicleModel())
                    .build();

            DriverStatus driverStatus = DriverStatus.builder()
                    .driverId(driver)
                    .isAvailable(false)
                    .isOnline(false)
                    .build();

            Location driverLocation = Location.builder()
                    .driverId(driver)
                    .latitude(23.25540149924644)
                    .longitude(77.40025710299598)
                    .build();

            driver.setDriverStatus(driverStatus);
            driver.setLocation(driverLocation);
            user.setDriver(driver);
        }

        if (request.getRole().name().equals("RIDER")) {
            Rider rider = new Rider();
            rider.setUserId(user);
            user.setRider(rider);
        }

        User save = userRepository.save(user);

        UserResponse userResponse = new UserResponse(save);

        log.info("New User Created Successfully : {}", user.getFullName());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "User created successfully",
                        "body", userResponse,
                        "success", "true"
                ));
    }

    @Override
    public ResponseEntity<?> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserResponse userResponse = new UserResponse(user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "User found successfully",
                        "body", userResponse,
                        "success", "true"));
    }

    @Override
    public ResponseEntity<?> getAllUser() {
        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponseList = userList.stream()
                .map(UserResponse::new).toList();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Found List of users successfully",
                        "body", userResponseList,
                        "success", "true"));
    }

    @Override
    public ResponseEntity<?> updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User save = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "User updated successfully",
                        "body", save,
                        "success", "true"));
    }

    @Override
    public ResponseEntity<?> changePassword(String email, ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with given email."));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false,
                            "message", "Old password is incorrect"));
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "New password and confirm password do not match"
                    ));
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", true
                            , "message", "New password cannot be the same as the old password"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("success", "true"
                , "message", "Password updated successfully"));
    }


    @Override
    public ResponseEntity<?> deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(user);

        return ResponseEntity.ok("DELETED");
    }

    public ResponseEntity<?> forgotPassword(UserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email does not exist"));
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return ResponseEntity.ok("Password forgot successfully!");
    }


}
