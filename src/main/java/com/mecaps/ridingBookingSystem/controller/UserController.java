package com.mecaps.ridingBookingSystem.controller;

import com.mecaps.ridingBookingSystem.request.UserRequest;
import com.mecaps.ridingBookingSystem.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("/user")
public class UserController {

    @Autowired
    final private UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequest request){
        return userService.createUser(request);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllUser(){
        return userService.getAllUser();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest request){
        return userService.updateUser(id,request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        return userService.deleteUser(id);
    }

}
