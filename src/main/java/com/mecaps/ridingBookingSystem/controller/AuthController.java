package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.entity.User;
import com.mecaps.ridingBookingSystem.exception.InvalidCredentialsException;
import com.mecaps.ridingBookingSystem.exception.UserNotFoundException;
import com.mecaps.ridingBookingSystem.repository.UserRepository;
import com.mecaps.ridingBookingSystem.request.AuthDTO;
import com.mecaps.ridingBookingSystem.request.RefreshTokenRequest;
import com.mecaps.ridingBookingSystem.response.TokenResponse;
import com.mecaps.ridingBookingSystem.security.JwtService;
import com.mecaps.ridingBookingSystem.security.TokenBlackListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlackListService tokenBlackListService;


    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, TokenBlackListService tokenBlackListService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenBlackListService = tokenBlackListService;
    }

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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);
        tokenBlackListService.blackListToken(token);
        return ResponseEntity.ok("Logged out successfully");
    }
}
