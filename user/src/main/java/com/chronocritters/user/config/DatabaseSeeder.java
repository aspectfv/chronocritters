package com.chronocritters.user.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.AbilityType;
import com.chronocritters.lib.model.BaseStats;
import com.chronocritters.lib.model.Critter;
import com.chronocritters.lib.model.CritterType;
import com.chronocritters.lib.model.Player;
import com.chronocritters.lib.model.PlayerStats;
import com.chronocritters.user.repository.AbilityRepository;
import com.chronocritters.user.repository.CritterRepository;
import com.chronocritters.user.repository.PlayerRepository;

@Configuration
public class DatabaseSeeder {
    @Bean
    public CommandLineRunner seedDatabase(AbilityRepository abilityRepository, CritterRepository critterRepository, PlayerRepository playerRepository) {
        return args -> {
            // Abilities
            Ability tidalWave = Ability.builder()
                .name("Tidal Wave")
                .power(80)
                .type(AbilityType.ATTACK)
                .build();
            Ability thunderStrike = Ability.builder()
                .name("Thunder Strike")
                .power(90)
                .type(AbilityType.ATTACK)
                .build();
            Ability healPulse = Ability.builder()
                .name("Heal Pulse")
                .power(50)
                .type(AbilityType.SUPPORT)
                .build();
            abilityRepository.save(tidalWave);
            abilityRepository.save(thunderStrike);
            abilityRepository.save(healPulse);

            // Critters
            Critter aquaLing = Critter.builder()
                .name("Aqua-ling")
                .type(CritterType.WATER)
                .baseStats(new BaseStats(120, 60, 70))
                .abilities(List.of(tidalWave, healPulse))
                .build();
            Critter voltHound = Critter.builder()
                .name("Volt-hound")
                .type(CritterType.ELECTRIC)
                .baseStats(new BaseStats(100, 90, 60))
                .abilities(List.of(thunderStrike))
                .build();

            // Player Stats
            PlayerStats blueOakStats = PlayerStats.builder()
                .wins(10)
                .losses(2)
                .build();
            PlayerStats redAshStats = PlayerStats.builder()
                .wins(8)
                .losses(4)
                .build();

            // Players
            Player blueOak = Player.builder()
                .username("BlueOak")
                .password("password1")
                .stats(blueOakStats)
                .roster(List.of(aquaLing))
                .build();
            Player redAsh = Player.builder()
                .username("RedAsh")
                .password("password2")
                .stats(redAshStats)
                .roster(List.of(voltHound))
                .build();
            
            critterRepository.save(aquaLing);
            critterRepository.save(voltHound);
            playerRepository.save(blueOak);
            playerRepository.save(redAsh);
        };
    }
}
