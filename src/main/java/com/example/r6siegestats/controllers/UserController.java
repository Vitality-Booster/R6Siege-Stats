package com.example.r6siegestats.controllers;

import com.example.r6siegestats.services.interaces.IUserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/")
public class UserController {
    private final IUserService service;

    public UserController(IUserService service) {
        this.service = service;
    }
}
