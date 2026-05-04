package ru.practicum.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "aggregator.kafka.topic")
public class AggregatorKafkaProperties {

    @NotBlank
    private String userActions;

    @NotBlank
    private String eventsSimilarity;
}