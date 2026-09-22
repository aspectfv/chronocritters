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

/**
 * Seeds the reference data every critter and ability is built from.
 *
 * Reference documents all carry explicit ids, so re-saving them on each startup
 * is an upsert rather than a duplicate. Player documents are never overwritten:
 * the two demo accounts are only created when they are missing, so registered
 * accounts and their match history survive a restart.
 */
@Configuration
public class DatabaseSeeder {

    @Bean
    public CommandLineRunner seedDatabase(AbilityRepository abilityRepository, CritterRepository critterRepository, PlayerRepository playerRepository, EffectRepository effectRepository) {
        return args -> {
            // Effects

            DamageEffect dmg = DamageEffect.builder()
                .id("eff-damage")
                .description("Deals 1 damage to the target.")
                .damage(1)
                .build();

            DamageOverTimeEffect dot = DamageOverTimeEffect.builder()
                .id("eff-damageovertime")
                .description("Deals 1 damage per turn for 3 turns.")
                .damagePerTurn(1)
                .duration(3)
                .build();

            SkipTurnEffect skipTurn = SkipTurnEffect.builder()
                .id("eff-skipturn")
                .description("Causes the target to skip their next 2 turns.")
                .duration(2)
                .build();

            effectRepository.saveAll(List.of(dmg, dot, skipTurn));

            // Abilities

            Ability riptideLash = Ability.builder()
                .id("atk-riptidelash")
                .name("Riptide Lash")
                .description("Strikes the opponent with a sudden, forceful current of water.")
                .effects(List.of(dmg))
                .build();

            Ability staticSnap = Ability.builder()
                .id("atk-staticsnap")
                .name("Static Snap")
                .description("Bites down with jaws of raw, concentrated electricity.")
                .effects(List.of(dmg))
                .build();

            Ability gearGrind = Ability.builder()
                .id("atk-geargrind")
                .name("Gear Grind")
                .description("Launches a series of sharpened, spinning gears at the opponent.")
                .effects(List.of(dmg))
                .build();

            Ability cinderLash = Ability.builder()
                .id("atk-cinderlash")
                .name("Cinder Lash")
                .description("Strikes the foe with a superheated whip of fire and embers.")
                .effects(List.of(dmg))
                .build();

            Ability rootJab = Ability.builder()
                .id("atk-rootjab")
                .name("Root Jab")
                .description("Thrusts a hardened, sharp root from the ground at the foe.")
                .effects(List.of(dmg))
                .build();

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

            abilityRepository.saveAll(List.of(
                riptideLash, staticSnap, gearGrind, cinderLash, rootJab,
                noxiousFumes, corrosiveBite, concussionWave, impactPunch
            ));

            // Critters

            Critter aqualing = Critter.builder()
                .id("water-aqualing")
                .name("Aqualing")
                .description("A shy, capricious spirit born from pure mountain springs, its body is a shimmering, ever-shifting form of water that is difficult to strike directly.")
                .type(CritterType.WATER)
                .baseStats(BaseStats.builder().health(5).attack(3).defense(4).build())
                .abilities(List.of(riptideLash))
                .build();
            Critter voltHound = Critter.builder()
                .id("electric-volthound")
                .name("Volthound")
                .description("A being of pure, chaotic energy, the Volthound is a relentless hunter that crackles with untamed power. Its form constantly sparks and shifts, making it a dangerously unpredictable foe.")
                .type(CritterType.ELECTRIC)
                .baseStats(BaseStats.builder().health(4).attack(6).defense(2).build())
                .abilities(List.of(staticSnap))
                .build();
            Critter cogling = Critter.builder()
                .id("metal-cogling")
                .name("Cogling")
                .description("A small, intricate creature assembled from discarded clockwork and enchanted metals. It whirs and clicks with meticulous purpose, constantly seeking to add to its own complex mechanisms.")
                .type(CritterType.METAL)
                .baseStats(BaseStats.builder().health(4).attack(3).defense(5).build())
                .abilities(List.of(gearGrind))
                .build();
            Critter searfiend = Critter.builder()
                .id("fire-searfiend")
                .name("Searfiend")
                .description("A malevolent creature born from the heart of a volcano, its body is a jagged shell of cooling magma animated by an insatiable inner flame. It seeks only to turn the world to ash.")
                .type(CritterType.FIRE)
                .baseStats(BaseStats.builder().health(5).attack(5).defense(2).build())
                .abilities(List.of(cinderLash))
                .build();
            Critter sylvanSentinel = Critter.builder()
                .id("grass-sylvansentinel")
                .name("Sylvan Sentinel")
                .description("An ancient guardian of the deep woods, its body is composed of hardened bark and living vines. It moves with slow, deliberate purpose, defending the natural order.")
                .type(CritterType.GRASS)
                .baseStats(BaseStats.builder().health(6).attack(2).defense(4).build())
                .abilities(List.of(rootJab))
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

            critterRepository.saveAll(List.of(
                aqualing, voltHound, cogling, searfiend, sylvanSentinel, miasmite, strikon
            ));

            // Demo accounts, created once and never overwritten.

            if (playerRepository.findById("p1").isEmpty()) {
                playerRepository.save(Player.builder()
                    .id("p1")
                    .username("BlueOak")
                    .password(PasswordUtil.hashPassword("password1"))
                    .stats(PlayerStats.builder().build())
                    .roster(List.of(aqualing, cogling, sylvanSentinel, strikon))
                    .build());
            }

            if (playerRepository.findById("p2").isEmpty()) {
                playerRepository.save(Player.builder()
                    .id("p2")
                    .username("RedAsh")
                    .password(PasswordUtil.hashPassword("password2"))
                    .stats(PlayerStats.builder().build())
                    .roster(List.of(voltHound, searfiend, miasmite))
                    .build());
            }
        };
    }
}
