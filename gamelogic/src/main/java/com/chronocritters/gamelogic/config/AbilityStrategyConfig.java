package com.chronocritters.gamelogic.config;

import com.chronocritters.lib.interfaces.AbilityStrategy;
import com.chronocritters.lib.model.AbilityType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class AbilityStrategyConfig {

    @Bean
    Map<AbilityType, AbilityStrategy> abilityStrategyMap(List<AbilityStrategy> strategies) {
        return strategies.stream()
                .collect(Collectors.toMap(
                        AbilityStrategy::getAbilityType, // key mapper
                        Function.identity(), // value mapper
                        (existing, replacement) -> existing, // merge function (in case of duplicates)
                        () -> new EnumMap<>(AbilityType.class)
                ));
    }
}