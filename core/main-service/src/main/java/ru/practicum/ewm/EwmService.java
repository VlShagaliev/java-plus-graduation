package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {
        "ru.practicum.ewm",
        "ru.practicum.stats",
        "ru.practicum"
})
@ConfigurationPropertiesScan
@EnableDiscoveryClient
public class EwmService {

    public static void main(String[] args) {
        SpringApplication.run(EwmService.class, args);
    }

}