package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.entity.User;
import com.mecaps.ridingBookingSystem.exception.InvalidCredentialsException;
import com.mecaps.ridingBookingSystem.exception.UserNotFoundException;
import com.mecaps.ridingBookingSystem.repository.UserRepository;
import com.mecaps.ridingBookingSystem.request.AuthDTO;
import com.mecaps.ridingBookingSystem.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

        authResponse.put("Token : ",token);
        authResponse.put("Role : ", String.valueOf(user.getRole()));
        authResponse.put("Email : ", user.getEmail());

        return authResponse;
    }
}
