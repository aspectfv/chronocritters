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
import com.chronocritters.lib.util.PasswordUtil;
import com.chronocritters.user.repository.AbilityRepository;
import com.chronocritters.user.repository.CritterRepository;
import com.chronocritters.user.repository.PlayerRepository;

@Configuration
public class DatabaseSeeder {
    @Bean
    public CommandLineRunner seedDatabase(AbilityRepository abilityRepository, CritterRepository critterRepository, PlayerRepository playerRepository) {
        return args -> {
            // Reset database state
            abilityRepository.deleteAll();
            critterRepository.deleteAll();
            playerRepository.deleteAll();
            // Abilities
            Ability tidalWave = Ability.builder()
                .id("atk-tidalwave")
                .name("Tidal Wave")
                .power(3)
                .type(AbilityType.ATTACK)
                .build();
            Ability healPulse = Ability.builder()
                .id("heal-healpulse")
                .name("Heal Pulse")
                .power(3)
                .type(AbilityType.HEAL)
                .build();

            Ability thunderStrike = Ability.builder()
                .id("atk-thunderstrike")
                .name("Thunder Strike")
                .power(4)
                .type(AbilityType.ATTACK)
                .build();


            Ability gearGrind = Ability.builder()
                .id("atk-geargrind")
                .name("Gear Grind")
                .power(3)
                .type(AbilityType.ATTACK)
                .build();
            Ability fortifyPlating = Ability.builder()
                .id("def-fortifyplating")
                .name("Fortify Plating")
                .power(2)
                .type(AbilityType.DEFENSE)
                .build();

            abilityRepository.save(tidalWave);
            abilityRepository.save(thunderStrike);
            abilityRepository.save(healPulse);
            abilityRepository.save(gearGrind);
            abilityRepository.save(fortifyPlating);

            // Critters
            Critter aquaLing = Critter.builder()
                .id("water-aqualing")
                .name("Aqualing")
                .type(CritterType.WATER)
                .baseStats(BaseStats.builder().health(6).attack(4).defense(2).build())
                .abilities(List.of(tidalWave, healPulse))
                .build();
            Critter voltHound = Critter.builder()
                .id("electric-volthound")
                .name("Volthound")
                .type(CritterType.ELECTRIC)
                .baseStats(BaseStats.builder().health(6).attack(3).defense(3).build())
                .abilities(List.of(thunderStrike))
                .build();
            Critter cogling = Critter.builder()
                .id("steel-cogling")
                .name("Cogling")
                .type(CritterType.STEEL)
                .baseStats(BaseStats.builder().health(4).attack(4).defense(4).build())
                .abilities(List.of(gearGrind, fortifyPlating))
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
                .id("p1")
                .username("BlueOak")
                .password(PasswordUtil.hashPassword("password1"))
                .stats(blueOakStats)
                .roster(List.of(aquaLing, cogling))
                .build();
            Player redAsh = Player.builder()
                .id("p2")
                .username("RedAsh")
                .password(PasswordUtil.hashPassword("password2"))
                .stats(redAshStats)
                .roster(List.of(voltHound))
                .build();

            critterRepository.save(aquaLing);
            critterRepository.save(voltHound);
            critterRepository.save(cogling);
            playerRepository.save(blueOak);
            playerRepository.save(redAsh);
        };
    }
}