package com.example.r6siegestats.controllers;

import com.example.r6siegestats.models.UserSignInRequest;
import com.example.r6siegestats.services.interaces.IStatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private final IStatisticsService service;

    public StatisticsController(IStatisticsService service) {
        this.service = service;
    }

    @PostMapping("/queue-stats")
    public ResponseEntity getQueueStats(@RequestBody UserSignInRequest model) {
        try {
            var response = service.getQueueStats(model).get();

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch(Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
