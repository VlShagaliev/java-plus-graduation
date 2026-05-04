package ru.practicum.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "recommendation.weights")
public class RecommendationWeightsProperties {

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double view;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double register;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double like;
}