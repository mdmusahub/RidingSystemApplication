package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.entity.User;
import com.mecaps.ridingBookingSystem.exception.InvalidCredentialsException;
import com.mecaps.ridingBookingSystem.exception.UserNotFoundException;
import com.mecaps.ridingBookingSystem.repository.UserRepository;
import com.mecaps.ridingBookingSystem.request.AuthDTO;
<<<<<<< HEAD
import com.mecaps.ridingBookingSystem.request.RefreshTokenRequest;
import com.mecaps.ridingBookingSystem.response.TokenResponse;
import com.mecaps.ridingBookingSystem.security.JwtService;
import com.mecaps.ridingBookingSystem.security.TokenBlackListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
=======
import com.mecaps.ridingBookingSystem.request.ForgotPasswordDTO;
import com.mecaps.ridingBookingSystem.request.RefreshTokenRequest;
import com.mecaps.ridingBookingSystem.request.ResetPasswordTokenDTO;
import com.mecaps.ridingBookingSystem.response.TokenResponse;
import com.mecaps.ridingBookingSystem.security.JwtService;
import com.mecaps.ridingBookingSystem.security.TokenBlackListService;
import com.mecaps.ridingBookingSystem.serviceImpl.EmailServiceImpl;
import org.springframework.http.ResponseEntity;
>>>>>>> c4b4f52786c7b306a37824f7225c2c404c10d043
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlackListService tokenBlackListService;
<<<<<<< HEAD


    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TokenBlackListService tokenBlackListService) {
=======
    private final EmailServiceImpl emailService;


    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TokenBlackListService tokenBlackListService, EmailServiceImpl emailService) {
>>>>>>> c4b4f52786c7b306a37824f7225c2c404c10d043
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenBlackListService = tokenBlackListService;
<<<<<<< HEAD
=======
        this.emailService = emailService;
>>>>>>> c4b4f52786c7b306a37824f7225c2c404c10d043
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User with email " + request.getEmail() + " does not exist. Please sign up first."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Credentials.");
        }

<<<<<<< HEAD
        String accessToken = jwtService.generateAccessToken(user.getEmail(),String.valueOf(user.getRole()));
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(),String.valueOf(user.getRole()));

        Map<String, String> authResponse = new HashMap<>();
        authResponse.put("AccessToken : ",accessToken);
        authResponse.put("RefreshToken : ",refreshToken);
=======
        String accessToken = jwtService.generateAccessToken(user.getEmail(), String.valueOf(user.getRole()));
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), String.valueOf(user.getRole()));

        Map<String, String> authResponse = new HashMap<>();
        authResponse.put("AccessToken : ", accessToken);
        authResponse.put("RefreshToken : ", refreshToken);
>>>>>>> c4b4f52786c7b306a37824f7225c2c404c10d043
        authResponse.put("Role : ", String.valueOf(user.getRole()));
        authResponse.put("Email : ", user.getEmail());
        return authResponse;
    }
<<<<<<< HEAD
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request){
=======

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
>>>>>>> c4b4f52786c7b306a37824f7225c2c404c10d043
        String oldRefreshToken = request.getRefreshToken();

        String email = jwtService.extractEmail(oldRefreshToken);
        String role = jwtService.extractRole(oldRefreshToken);

<<<<<<< HEAD
        String newAccessToken = jwtService.generateAccessToken(email,role);
        String newRefreshToken = jwtService.generateRefreshToken(email,role);

        TokenResponse tokenResponse = new TokenResponse(newAccessToken,newRefreshToken);
        return  ResponseEntity.ok(Map.of(

                "NewAccessToken",newAccessToken,
                "NewRefreshToken",newRefreshToken));

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);
        tokenBlackListService.blackListToken(token);
        return ResponseEntity.ok("Logged out successfully");
    }
}
=======
        String newAccessToken = jwtService.generateAccessToken(email, role);
        String newRefreshToken = jwtService.generateRefreshToken(email, role);

        TokenResponse tokenResponse = new TokenResponse(newAccessToken, newRefreshToken);
        return ResponseEntity.ok(Map.of(

                "NewAccessToken", newAccessToken,
                "NewRefreshToken", newRefreshToken));

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDTO request) {
        String email = request.getEmail();
        Optional<User> userOpt = userRepository.findByEmail(email);

        // Security ke liye: email exist ho ya na ho, same response dena
        if (userOpt.isPresent()) {
            // Token banao
            String token = jwtService.createResetPasswordToken(email);
            // Link banao
            String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;
            // Email bhejo
            emailService.sendSimpleEmail(
                    email,
                    "Password Reset Request",
                    "Click this link to reset your password: " + resetLink
            );
        }

        return ResponseEntity.ok("If the email exists in our system, you will receive a password reset link.");
    }


    // 2. Reset password — user email+ newPassword dega
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordTokenDTO request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        // 1️. Token se email nikaalo
        String email = jwtService.extractEmail(token);

        // 2️. Token valid hai kya?
        if (!jwtService.isTokenValid(token) || email == null) {
            return ResponseEntity.badRequest().body("Invalid or expired reset token.");
        }

        // 3️. UserRepository ke through DB me user find karo
        Optional<User> userOtp = userRepository.findByEmail(email);

        if (userOtp.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found!");
        }

        // 4️. Password update karo aur encode karo
        User user = userOtp.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Password successfully changed.");
    }

        @PostMapping("/logout")
        public ResponseEntity<?> logout (@RequestHeader("Authorization") String authHeader){
            String token = authHeader.substring(7);
            tokenBlackListService.blackListToken(token);
            return ResponseEntity.ok("Logged out successfully");
        }
    }
>>>>>>> c4b4f52786c7b306a37824f7225c2c404c10d043
