package com.foodwaste;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FoodWasteApplication {
    public static void main(String[] args) {
        SpringApplication.run(FoodWasteApplication.class, args);
    }
}
