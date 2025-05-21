package com.example.economicssimulatorserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Главный класс — точка входа Spring Boot-приложения Economics Simulator Server.
 */
@EnableCaching
@SpringBootApplication
public class EconomicsSimulatorServerApplication {

    /**
     * Запуск Spring Boot-приложения.
     *
     * @param args параметры командной строки (опционально)
     */
    public static void main(String[] args) {
        SpringApplication.run(EconomicsSimulatorServerApplication.class, args);
    }
}
