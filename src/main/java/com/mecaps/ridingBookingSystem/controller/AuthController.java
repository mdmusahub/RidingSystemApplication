package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.entity.User;
import com.mecaps.ridingBookingSystem.exception.InvalidCredentialsException;
import com.mecaps.ridingBookingSystem.exception.UserNotFoundException;
import com.mecaps.ridingBookingSystem.repository.UserRepository;
import com.mecaps.ridingBookingSystem.request.AuthDTO;
import com.mecaps.ridingBookingSystem.request.RefreshTokenRequest;
import com.mecaps.ridingBookingSystem.response.TokenResponse;
import com.mecaps.ridingBookingSystem.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthDTO request){

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User with email " + request.getEmail() + " does not exist. Please sign up first."));

        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new InvalidCredentialsException("Invalid Credentials.");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail(),String.valueOf(user.getRole()));
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(),String.valueOf(user.getRole()));

        Map<String, String> authResponse = new HashMap<>();
        authResponse.put("AccessToken : ",accessToken);
        authResponse.put("RefreshToken : ",refreshToken);
        authResponse.put("Role : ", String.valueOf(user.getRole()));
        authResponse.put("Email : ", user.getEmail());
        return authResponse;
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request){
        String oldRefreshToken = request.getRefreshToken();

        String email = jwtService.extractEmail(oldRefreshToken);
        String role = jwtService.extractRole(oldRefreshToken);

        String newAccessToken = jwtService.generateAccessToken(email,role);
        String newRefreshToken = jwtService.generateRefreshToken(email,role);

        TokenResponse tokenResponse = new TokenResponse(newAccessToken,newRefreshToken);
        return  ResponseEntity.ok(Map.of(
                "NewAccessToken",newAccessToken,
                "NewRefreshToken",newRefreshToken));


    }


}
