package com.chronocritters.user.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.lib.model.domain.Player;
import com.chronocritters.lib.model.domain.PlayerStats;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.effects.DamageOverTimeEffect;
import com.chronocritters.lib.model.effects.SkipTurnEffect;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.lib.util.PasswordUtil;
import com.chronocritters.user.player.repository.AbilityRepository;
import com.chronocritters.user.player.repository.CritterRepository;
import com.chronocritters.user.player.repository.EffectRepository;
import com.chronocritters.user.player.repository.PlayerRepository;

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

            DamageEffect dmg = DamageEffect.builder()
                .description("Deals 1 damage to the target.")
                .damage(1)
                .build();
            
            DamageOverTimeEffect dot = DamageOverTimeEffect.builder()
                .description("Deals 1 damage per turn for 3 turns.")
                .damagePerTurn(1)
                .duration(3)
                .build();

            // concussion wave
            SkipTurnEffect skipTurn = SkipTurnEffect.builder()
                .description("Causes the target to skip their next 2 turns.")
                .duration(2)
                .build();

            effectRepository.save(dmg);
            effectRepository.save(dot);
            effectRepository.save(skipTurn);

            // Abilities

            // Aqualing
            Ability riptideLash = Ability.builder()
                .id("ab-riptidelash")
                .name("Riptide Lash")
                .description("Strikes the opponent with a sudden, forceful current of water.")
                .effects(List.of(dmg))
                .build();
            // Ability aqueousVeil = Ability.builder()
            //     .id("def-aqueousveil")
            //     .name("Aqueous Veil")
            //     .effects(null)
            //     .build();

            // Volthound
            Ability staticSnap = Ability.builder()
                .id("atk-staticsnap")
                .name("Static Snap")
                .description("Bites down with jaws of raw, concentrated electricity.")
                .effects(List.of(dmg))
                .build();
            // Ability joltWard = Ability.builder()
            //     .id("def-joltward")
            //     .name("Jolt Ward")
            //     .power(1)
            //     .type(AbilityType.DEFENSE)
            //     .build();

            // Cogling
            Ability gearGrind = Ability.builder()
                .id("atk-geargrind")
                .name("Gear Grind")
                .description("Launches a series of sharpened, spinning gears at the opponent.")
                .effects(List.of(dmg))
                .build();
            // Ability fortifyPlating = Ability.builder()
            //     .id("def-fortifyplating")
            //     .name("Fortify Plating")
            //     .effects(List.of(def))
            //     .build();

            // Searfiend
            Ability cinderLash = Ability.builder()
                .id("atk-cinderlash")
                .name("Cinder Lash")
                .description("Strikes the foe with a superheated whip of fire and embers.")
                .effects(List.of(dmg))
                .build();
            // Ability ashenGuard = Ability.builder()
            //     .id("def-ashenguard")
            //     .name("Ashen Guard")
            //     .power(1)
            //     .type(AbilityType.DEFENSE)
            //     .build();

            // Sylvan Sentinel
            Ability rootJab = Ability.builder()
                .id("atk-rootjab")
                .name("Root Jab")
                .description("Thrusts a hardened, sharp root from the ground at the foe.")
                .effects(List.of(dmg))
                .build();

            // Ability sunbathe = Ability.builder()
            //     .id("heal-sunbathe")
            //     .name("Sunbathe")
            //     .power(4)
            //     .type(AbilityType.HEAL)
            //     .build();

            // Miasmite Abilities
            Ability noxiousFumes = Ability.builder()
                .id("eff-noxiousfumes")
                .name("Noxious Fumes")
                .description("Releases a cloud of sickening gas that clings to the opponent.")
                .effects(List.of(dot))
                .build();

            Ability corrosiveBite = Ability.builder()
                .id("atk-corrosivebite")
                .name("Corrosive Bite")
                .description("A vicious bite that sizzles with acidic venom.")
                .effects(List.of(dmg))
                .build();

            // Strikon Abilities
            Ability concussionWave = Ability.builder()
                .id("eff-concussionwave")
                .name("Concussion Wave")
                .description("Unleashes a disorienting shockwave that temporarily stuns the opponent.")
                .effects(List.of(skipTurn))
                .build();

            Ability impactPunch = Ability.builder()
                .id("atk-impactpunch")
                .name("Impact Punch")
                .description("Delivers a straightforward but incredibly forceful punch.")
                .effects(List.of(dmg))
                .build();

            // Persisting Abilities

            // Aqualing
            abilityRepository.save(riptideLash);
            // abilityRepository.save(aqueousVeil);

            // Volthound
            abilityRepository.save(staticSnap);
            // abilityRepository.save(joltWard);

            // Cogling
            abilityRepository.save(gearGrind);
            // abilityRepository.save(fortifyPlating);

            /// Searfiend
            abilityRepository.save(cinderLash);
            // abilityRepository.save(ashenGuard);

            // Sylvan Sentinel
            abilityRepository.save(rootJab);
            // abilityRepository.save(sunbathe);

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
                .description("A shy, capricious spirit born from pure mountain springs, its body is a shimmering, ever-shifting form of water that is difficult to strike directly.")
                .type(CritterType.WATER)
                .baseStats(BaseStats.builder().health(5).attack(3).defense(4).build())
                .abilities(List.of(riptideLash /*, aqueousVeil */))
                .build();
            Critter voltHound = Critter.builder()
                .id("electric-volthound")
                .name("Volthound")
                .description("A being of pure, chaotic energy, the Volthound is a relentless hunter that crackles with untamed power. Its form constantly sparks and shifts, making it a dangerously unpredictable foe.")
                .type(CritterType.ELECTRIC)
                .baseStats(BaseStats.builder().health(4).attack(6).defense(2).build())
                .abilities(List.of(staticSnap /*,  joltWard */))
                .build();
            Critter cogling = Critter.builder()
                .id("METAL-cogling")
                .name("Cogling")
                .description("A small, intricate creature assembled from discarded clockwork and enchanted metals. It whirs and clicks with meticulous purpose, constantly seeking to add to its own complex mechanisms.")
                .type(CritterType.METAL)
                .baseStats(BaseStats.builder().health(4).attack(3).defense(5).build())
                .abilities(List.of(gearGrind /*,  fortifyPlating */))
                .build();
            Critter searfiend = Critter.builder()
                .id("fire-searfiend")
                .name("Searfiend")
                .description("A malevolent creature born from the heart of a volcano, its body is a jagged shell of cooling magma animated by an insatiable inner flame. It seeks only to turn the world to ash.")
                .type(CritterType.FIRE)
                .baseStats(BaseStats.builder().health(5).attack(5).defense(2).build())
                .abilities(List.of(cinderLash /*,  ashenGuard */))
                .build();
            Critter sylvanSentinel = Critter.builder()
                .id("grass-sylvansentinel")
                .name("Sylvan Sentinel")
                .description("An ancient guardian of the deep woods, its body is composed of hardened bark and living vines. It moves with slow, deliberate purpose, defending the natural order.")
                .type(CritterType.GRASS)
                .baseStats(BaseStats.builder().health(6).attack(2).defense(4).build())
                .abilities(List.of(rootJab /*, sunbathe */))
                .build();
            Critter miasmite = Critter.builder()
                .id("toxic-miasmite")
                .name("Miasmite")
                .description("A creature born from polluted swamps, Miasmite's gelatinous body constantly leaks a foul-smelling, corrosive ooze. It seeks to corrupt everything it touches, leaving a trail of decay in its wake.")
                .type(CritterType.TOXIC)
                .baseStats(BaseStats.builder().health(5).attack(3).defense(4).build())
                .abilities(List.of(noxiousFumes, corrosiveBite))
                .build();
            Critter strikon = Critter.builder()
                .id("kinetic-strikon")
                .name("Strikon")
                .description("A heavily-built Critter that channels raw kinetic energy into its powerful limbs. It overwhelms opponents not with elemental power, but with pure, concussive force.")
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