package com.example.r6siegestats.controllers;

import com.example.r6siegestats.models.UserSignInRequest;
import com.example.r6siegestats.services.interaces.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    private final IUserService service;

    public UserController(IUserService service) {
        this.service = service;
    }

    @PostMapping("/sign-in")
    public ResponseEntity signUserInUbi(@RequestBody UserSignInRequest model) {
        try {
            service.signInUser(model);

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch(Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
