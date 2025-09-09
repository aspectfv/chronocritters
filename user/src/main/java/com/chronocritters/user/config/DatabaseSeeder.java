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
import com.chronocritters.lib.model.Effect;
import com.chronocritters.lib.model.EffectType;
import com.chronocritters.lib.model.Player;
import com.chronocritters.lib.model.PlayerStats;
import com.chronocritters.lib.util.PasswordUtil;
import com.chronocritters.user.repository.AbilityRepository;
import com.chronocritters.user.repository.CritterRepository;
import com.chronocritters.user.repository.EffectRepository;
import com.chronocritters.user.repository.PlayerRepository;

@Configuration
public class DatabaseSeeder {
    @Bean
    public CommandLineRunner seedDatabase(AbilityRepository abilityRepository, CritterRepository critterRepository, PlayerRepository playerRepository, EffectRepository effectRepository) {
        return args -> {
            // Reset database state
            abilityRepository.deleteAll();
            critterRepository.deleteAll();
            playerRepository.deleteAll();
            effectRepository.deleteAll();

            // Effects

            // noxious fumes
            Effect poison = Effect.builder()
                .id("eff-poison")
                .name("Poison")
                .type(EffectType.DAMAGE_OVER_TIME)
                .power(1)
                .duration(3)
                .chance(80)
                .build();

            // concussion wave
            Effect stun = Effect.builder()
                .id("eff-stun")
                .name("Stun")
                .type(EffectType.SKIP_TURN)
                .power(0)
                .duration(3)
                .chance(50)
                .build();

            effectRepository.save(poison);
            effectRepository.save(stun);

            // Abilities

            // Aqualing
            Ability riptideLash = Ability.builder()
                .id("atk-riptidelash")
                .name("Riptide Lash")
                .power(3)
                .type(AbilityType.ATTACK)
                .build();
            Ability aqueousVeil = Ability.builder()
                .id("def-aqueousveil")
                .name("Aqueous Veil")
                .power(2)
                .type(AbilityType.DEFENSE)
                .build();

            // Volthound
            Ability staticSnap = Ability.builder()
                .id("atk-staticsnap")
                .name("Static Snap")
                .power(5)
                .type(AbilityType.ATTACK)
                .build();
            Ability joltWard = Ability.builder()
                .id("def-joltward")
                .name("Jolt Ward")
                .power(1)
                .type(AbilityType.DEFENSE)
                .build();

            // Cogling
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

            // Searfiend
            Ability cinderLash = Ability.builder()
                .id("atk-cinderlash")
                .name("Cinder Lash")
                .power(4)
                .type(AbilityType.ATTACK)
                .build();
            Ability ashenGuard = Ability.builder()
                .id("def-ashenguard")
                .name("Ashen Guard")
                .power(1)
                .type(AbilityType.DEFENSE)
                .build();

            // Sylvan Sentinel
            Ability rootJab = Ability.builder()
                .id("atk-rootjab")
                .name("Root Jab")
                .power(2)
                .type(AbilityType.ATTACK)
                .build();

            Ability sunbathe = Ability.builder()
                .id("heal-sunbathe")
                .name("Sunbathe")
                .power(4)
                .type(AbilityType.HEAL)
                .build();

            // Miasmite Abilities
            Ability noxiousFumes = Ability.builder()
                .id("eff-noxiousfumes")
                .name("Noxious Fumes")
                .power(2)
                .type(AbilityType.EFFECT)
                .effects(List.of(poison))
                .build();

            Ability corrosiveBite = Ability.builder()
                .id("atk-corrosivebite")
                .name("Corrosive Bite")
                .power(2)
                .type(AbilityType.ATTACK)
                .build();

            // Strikon Abilities
            Ability concussionWave = Ability.builder()
                .id("eff-concussionwave")
                .name("Concussion Wave")
                .power(1)
                .type(AbilityType.EFFECT)
                .effects(List.of(stun))
                .build();

            Ability impactPunch = Ability.builder()
                .id("atk-impactpunch")
                .name("Impact Punch")
                .power(4)
                .type(AbilityType.ATTACK)
                .build();

            // Persisting Abilities
            // Aqualing
            abilityRepository.save(riptideLash);
            abilityRepository.save(aqueousVeil);

            // Volthound
            abilityRepository.save(staticSnap);
            abilityRepository.save(joltWard);

            // Cogling
            abilityRepository.save(gearGrind);
            abilityRepository.save(fortifyPlating);

            /// Searfiend
            abilityRepository.save(cinderLash);
            abilityRepository.save(ashenGuard);

            // Sylvan Sentinel
            abilityRepository.save(rootJab);
            abilityRepository.save(sunbathe);

            // Miasmite Abilities
            abilityRepository.save(noxiousFumes);
            abilityRepository.save(corrosiveBite);

            // Strikon Abilities
            abilityRepository.save(concussionWave);
            abilityRepository.save(impactPunch);

            // Critters
            Critter aquaLing = Critter.builder()
                .id("water-aqualing")
                .name("Aqualing")
                .type(CritterType.WATER)
                .baseStats(BaseStats.builder().health(5).attack(3).defense(4).build())
                .abilities(List.of(riptideLash, aqueousVeil))
                .build();
            Critter voltHound = Critter.builder()
                .id("electric-volthound")
                .name("Volthound")
                .type(CritterType.ELECTRIC)
                .baseStats(BaseStats.builder().health(4).attack(6).defense(2).build())
                .abilities(List.of(staticSnap, joltWard))
                .build();
            Critter cogling = Critter.builder()
                .id("METAL-cogling")
                .name("Cogling")
                .type(CritterType.METAL)
                .baseStats(BaseStats.builder().health(4).attack(3).defense(5).build())
                .abilities(List.of(gearGrind, fortifyPlating))
                .build();
            Critter searfiend = Critter.builder()
                .id("fire-searfiend")
                .name("Searfiend")
                .type(CritterType.FIRE)
                .baseStats(BaseStats.builder().health(5).attack(5).defense(2).build())
                .abilities(List.of(cinderLash, ashenGuard))
                .build();
            Critter sylvanSentinel = Critter.builder()
                .id("grass-sylvansentinel")
                .name("Sylvan Sentinel")
                .type(CritterType.GRASS)
                .baseStats(BaseStats.builder().health(6).attack(2).defense(4).build())
                .abilities(List.of(rootJab, sunbathe))
                .build();
            Critter miasmite = Critter.builder()
                .id("toxic-miasmite")
                .name("Miasmite")
                .type(CritterType.TOXIC)
                .baseStats(BaseStats.builder().health(5).attack(3).defense(4).build())
                .abilities(List.of(noxiousFumes, corrosiveBite))
                .build();
            Critter strikon = Critter.builder()
                .id("kinetic-strikon")
                .name("Strikon")
                .type(CritterType.KINETIC)
                .baseStats(BaseStats.builder().health(5).attack(5).defense(2).build())
                .abilities(List.of(concussionWave, impactPunch))
                .build();

            critterRepository.save(aquaLing);
            critterRepository.save(voltHound);
            critterRepository.save(cogling);
            critterRepository.save(searfiend);
            critterRepository.save(sylvanSentinel);
            critterRepository.save(miasmite);
            critterRepository.save(strikon);

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
                .roster(List.of(aquaLing, cogling, sylvanSentinel, strikon))
                .build();
            Player redAsh = Player.builder()
                .id("p2")
                .username("RedAsh")
                .password(PasswordUtil.hashPassword("password2"))
                .stats(redAshStats)
                .roster(List.of(voltHound, searfiend, miasmite))
                .build();

            playerRepository.save(blueOak);
            playerRepository.save(redAsh);
        };
    }
}