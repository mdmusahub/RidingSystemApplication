package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.request.ForgotPasswordDTO;
import com.mecaps.ridingBookingSystem.request.ResetPasswordTokenDTO;
import com.mecaps.ridingBookingSystem.entity.User;
import com.mecaps.ridingBookingSystem.exception.InvalidCredentialsException;
import com.mecaps.ridingBookingSystem.exception.UserNotFoundException;
import com.mecaps.ridingBookingSystem.repository.UserRepository;
import com.mecaps.ridingBookingSystem.request.AuthDTO;
import com.mecaps.ridingBookingSystem.security.JwtService;
import com.mecaps.ridingBookingSystem.serviceImpl.EmailServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private EmailServiceImpl emailService;


    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtService jwtService, EmailServiceImpl emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;

    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthDTO request){

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User with email " + request.getEmail() + " does not exist. Please sign up first."));

        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new InvalidCredentialsException("Invalid Credentials.");
        }

        String token = jwtService.generateAccessToken(user.getEmail(),String.valueOf(user.getRole()));

        Map<String, String> authResponse = new HashMap<>();

        authResponse.put("Token", token);
        authResponse.put("Role", String.valueOf(user.getRole()));
        authResponse.put("Email", user.getEmail());

        return authResponse;
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
    public  ResponseEntity<String> resetPassword(@RequestBody ResetPasswordTokenDTO request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        // 1️. Token se email nikaalo
        String  email = jwtService.extractEmail(token);

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
}



