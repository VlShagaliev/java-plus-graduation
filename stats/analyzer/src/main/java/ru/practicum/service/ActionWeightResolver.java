package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.config.RecommendationWeightsProperties;
import ru.practicum.avro.ActionTypeAvro;

@Component
@RequiredArgsConstructor
public class ActionWeightResolver {

    private final RecommendationWeightsProperties weightsProperties;

    public double resolve(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> weightsProperties.getView();
            case REGISTER -> weightsProperties.getRegister();
            case LIKE -> weightsProperties.getLike();
        };
    }
}