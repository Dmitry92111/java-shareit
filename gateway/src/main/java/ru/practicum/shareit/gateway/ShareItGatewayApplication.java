package ru.practicum.shareit.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.practicum")
public class ShareItGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShareItGatewayApplication.class, args);
    }
}