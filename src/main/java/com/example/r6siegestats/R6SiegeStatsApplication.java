package com.example.r6siegestats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication()
@EnableAsync
public class R6SiegeStatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(R6SiegeStatsApplication.class, args);
    }

}
