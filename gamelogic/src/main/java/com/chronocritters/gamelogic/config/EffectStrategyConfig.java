package com.chronocritters.gamelogic.config;

import com.chronocritters.lib.interfaces.EffectStrategy;
import com.chronocritters.lib.model.EffectType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class EffectStrategyConfig {

    @Bean
    Map<EffectType, EffectStrategy> effectStrategyMap(List<EffectStrategy> strategies) {
        return strategies.stream()
                .collect(Collectors.toMap(
                        EffectStrategy::getEffectType, // key mapper
                        Function.identity(), // value mapper
                        (existing, replacement) -> existing, // merge function
                        () -> new EnumMap<>(EffectType.class)
                ));
    }
}