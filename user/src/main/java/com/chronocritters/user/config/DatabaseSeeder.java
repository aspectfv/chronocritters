package com.chronocritters.user.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chronocritters.lib.Ability;
import com.chronocritters.lib.AbilityType;
import com.chronocritters.user.repository.AbilityRepository;

@Configuration
public class DatabaseSeeder {
    @Bean
    public CommandLineRunner seedDatabase(AbilityRepository abilityRepository) {
        return args -> {
            Ability ability = new Ability(null, "Fireball", 100, AbilityType.ATTACK);
            abilityRepository.save(ability);
        };
    }
}
