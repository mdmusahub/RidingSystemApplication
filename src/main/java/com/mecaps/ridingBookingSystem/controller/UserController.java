package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.request.ChangePasswordRequest;
import com.mecaps.ridingBookingSystem.request.UserRequest;
import com.mecaps.ridingBookingSystem.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    final private UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @PreAuthorize("hasRole('ADMIN') or (#id == authentication.principal.id)")
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllUser() {
        return userService.getAllUser();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request
            , Authentication authentication) {
        String email = authentication.getName();
        return userService.changePassword(email, request);
    }

    @PreAuthorize("hasRole('ADMIN') or (#id == authentication.principal.id)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

}