package com.example.economicssimulatorserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class EconomicsSimulatorServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EconomicsSimulatorServerApplication.class, args);
    }
}
