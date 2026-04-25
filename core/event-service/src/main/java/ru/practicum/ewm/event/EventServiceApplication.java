package ru.practicum.ewm.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"ru.practicum.ewm", "ru.practicum.stats"})
@EnableFeignClients(basePackages = {"ru.practicum.ewm", "ru.practicum.stats"})
@EntityScan(basePackages = "ru.practicum.ewm")
@EnableJpaRepositories(basePackages = "ru.practicum.ewm")
public class EventServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventServiceApplication.class, args);
    }
}