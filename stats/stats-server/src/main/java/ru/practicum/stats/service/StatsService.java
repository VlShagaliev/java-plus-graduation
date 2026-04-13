package ru.practicum.stats.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class StatsService {
    public static void main(String[] args) {
        SpringApplication.run(StatsService.class, args);
    }
}