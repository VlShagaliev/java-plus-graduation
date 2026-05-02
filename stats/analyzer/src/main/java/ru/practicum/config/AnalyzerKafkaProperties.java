package ru.practicum.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "analyzer.kafka.topic")
public class AnalyzerKafkaProperties {

    @NotBlank
    private String userActions;

    @NotBlank
    private String eventsSimilarity;
}