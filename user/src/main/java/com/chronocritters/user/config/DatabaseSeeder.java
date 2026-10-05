package com.chronocritters.user.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.domain.Player;
import com.chronocritters.lib.model.domain.PlayerStats;
import com.chronocritters.lib.model.effects.CleanseEffect;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.effects.HealEffect;
import com.chronocritters.lib.model.effects.LifestealEffect;
import com.chronocritters.lib.model.effects.ShieldEffect;
import com.chronocritters.lib.model.effects.StatModifierEffect;
import com.chronocritters.lib.model.effects.StatusEffect;
import com.chronocritters.lib.model.enums.Stat;
import com.chronocritters.lib.model.enums.StatusCondition;
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
 * is an upsert rather than a duplicate. Player documents are never overwritten,
 * with one exception: the two demo accounts have their roster and password
 * reset, because a fixture that still holds a retired critter demonstrates
 * nothing.
 *
 * <h2>The roster</h2>
 *
 * Seven critters, one of each type. The type chart gives every type two
 * advantages and two weaknesses, so no critter is the safe pick and a team of
 * three is chosen for what it covers. Every critter spends the same stat
 * budget of 31 across health, attack, defense and speed, and differs in where
 * it puts it.
 *
 * Damage is {@code round(base * min(3, atk/def) * type)}, so a type multiplier
 * and a stat ratio compound. Defence is kept in a narrow 5-6 band for that
 * reason: spreading it further turned a resisted hit into a rounding error. As
 * seeded, a favourable attack takes three hits to knock a critter out and a
 * resisted one takes five to eight, which is the gap each critter's second
 * ability exists to close.
 *
 * Each critter carries four moves: a plain attack with no cooldown, so a
 * player always has something to do, and three that each answer a different
 * problem. Status damage ignores the type chart, so the status moves are the
 * answer to a bad matchup rather than a strictly better opening move.
 *
 * <ul>
 *   <li>Searfiend burns, stacks attack and drains. It burns hot and
 *       short.</li>
 *   <li>Sylvan Sentinel poisons, wards and heals, which is how a slow wall
 *       wins a race it is losing.</li>
 *   <li>Aqualing puts the opponent to sleep, erodes its guard and washes off
 *       whatever has been put on it.</li>
 *   <li>Volthound is the fastest thing in the game and the most fragile. It
 *       paralyzes whatever might outpace it, and charges one big hit.</li>
 *   <li>Cogling plates itself and grinds. It gets harder to hurt the longer it
 *       stays in, and carries one heavy blow on a cooldown.</li>
 *   <li>Miasmite poisons, drains and weakens. It wins by making the opponent's
 *       hits smaller than its own.</li>
 *   <li>Strikon carries the heaviest single hit, and a blow that slows the
 *       target so it can land it first.</li>
 * </ul>
 */
@Configuration
public class DatabaseSeeder {

    /** Shared by both demo accounts. They exist to be signed into, not guarded. */
    private static final String DEMO_PASSWORD = "password";

    @Bean
    public CommandLineRunner seedDatabase(AbilityRepository abilityRepository, CritterRepository critterRepository, PlayerRepository playerRepository, EffectRepository effectRepository, MongoTemplate mongoTemplate) {
        return args -> {
            // Effects. Each carries its numbers in its id, so an ability's
            // strength is readable where the ability is declared.

            DamageEffect strike1 = DamageEffect.builder()
                .id("eff-strike-1")
                .description("Deals 1 damage.")
                .damage(1)
                .build();

            DamageEffect strike3 = DamageEffect.builder()
                .id("eff-strike-3")
                .description("Deals 3 damage.")
                .damage(3)
                .build();

            DamageEffect strike4 = DamageEffect.builder()
                .id("eff-strike-4")
                .description("Deals 4 damage.")
                .damage(4)
                .build();

            // Status conditions. A critter holds one at a time, so each of
            // these fails on a target that already has one.
            //
            // Burn and poison tick on every turn in the battle, the caster's
            // included, so four ticks cover two of the target's own turns.
            StatusEffect burn = StatusEffect.builder()
                .id("eff-burn")
                .description("Burns the target for 1 damage a turn over 4 turns, halving its attack while it lasts.")
                .condition(StatusCondition.BURN)
                .potency(1)
                .duration(4)
                .build();

            StatusEffect poison = StatusEffect.builder()
                .id("eff-poison")
                .description("Poisons the target for 4 turns, dealing 1 more damage each turn than the last.")
                .condition(StatusCondition.POISON)
                .potency(1)
                .duration(4)
                .build();

            // Sleep counts the target's own turns rather than ticks, so one is
            // one turn missed. A hit wakes it early.
            StatusEffect sleep = StatusEffect.builder()
                .id("eff-sleep")
                .description("Puts the target to sleep for its next turn, or until it is hit.")
                .condition(StatusCondition.SLEEP)
                .duration(1)
                .build();

            // A stage is a step on the Pokemon table: +1 is one and a half
            // times the stat, -1 is two thirds. Stages hold until the critter
            // leaves the field.
            StatModifierEffect kindle = StatModifierEffect.builder()
                .id("eff-attack-up-1")
                .description("Raises the user's attack by one stage.")
                .stat(Stat.ATTACK)
                .stages(1)
                .build();

            StatModifierEffect erode = StatModifierEffect.builder()
                .id("eff-defense-down-1")
                .description("Lowers the target's defense by one stage.")
                .stat(Stat.DEFENSE)
                .stages(-1)
                .build();

            HealEffect mend40 = HealEffect.builder()
                .id("eff-mend-40")
                .description("Restores 40% of the user's maximum health.")
                .healPercent(40)
                .build();

            HealEffect mend20 = HealEffect.builder()
                .id("eff-mend-20")
                .description("Restores 20% of the user's maximum health.")
                .healPercent(20)
                .build();

            LifestealEffect drain3 = LifestealEffect.builder()
                .id("eff-drain-3")
                .description("Deals 3 damage and restores half of it to the user.")
                .damage(3)
                .drainPercent(50)
                .build();

            // Five turns of ward is two enemy attacks: it ticks on every turn
            // in the battle, the caster's included, and is spent before the
            // third one lands.
            ShieldEffect ward5 = ShieldEffect.builder()
                .id("eff-ward-5")
                .description("Absorbs the next 5 damage taken over the next two enemy turns.")
                .amount(5)
                .duration(5)
                .build();

            CleanseEffect cleanse = CleanseEffect.builder()
                .id("eff-cleanse")
                .description("Clears every ailment on the user.")
                .build();

            DamageEffect strike2 = DamageEffect.builder()
                .id("eff-strike-2")
                .description("Deals 2 damage.")
                .damage(2)
                .build();

            DamageEffect strike5 = DamageEffect.builder()
                .id("eff-strike-5")
                .description("Deals 5 damage.")
                .damage(5)
                .build();

            DamageEffect strike6 = DamageEffect.builder()
                .id("eff-strike-6")
                .description("Deals 6 damage.")
                .damage(6)
                .build();

            StatusEffect paralysis = StatusEffect.builder()
                .id("eff-paralysis")
                .description("Paralyzes the target for 4 turns, halving its speed while it lasts.")
                .condition(StatusCondition.PARALYSIS)
                .duration(4)
                .build();

            StatModifierEffect quicken = StatModifierEffect.builder()
                .id("eff-speed-up-1")
                .description("Raises the user's speed by one stage.")
                .stat(Stat.SPEED)
                .stages(1)
                .build();

            StatModifierEffect harden = StatModifierEffect.builder()
                .id("eff-defense-up-1")
                .description("Raises the user's defense by one stage.")
                .stat(Stat.DEFENSE)
                .stages(1)
                .build();

            StatModifierEffect weaken = StatModifierEffect.builder()
                .id("eff-attack-down-1")
                .description("Lowers the target's attack by one stage.")
                .stat(Stat.ATTACK)
                .stages(-1)
                .build();

            StatModifierEffect slow = StatModifierEffect.builder()
                .id("eff-speed-down-1")
                .description("Lowers the target's speed by one stage.")
                .stat(Stat.SPEED)
                .stages(-1)
                .build();

            HealEffect mend30 = HealEffect.builder()
                .id("eff-mend-30")
                .description("Restores 30% of the user's maximum health.")
                .healPercent(30)
                .build();

            List<Effect> effects = List.of(strike1, strike2, strike3, strike4, strike5, strike6,
                burn, poison, sleep, paralysis, kindle, quicken, harden, erode, weaken, slow,
                mend20, mend30, mend40, drain3, ward5, cleanse);
            effectRepository.saveAll(effects);

            // Abilities. Every critter keeps one move with no cooldown, so a
            // player always has something to do. The status moves are the ones
            // that carry a cooldown, because refreshing a status every turn was
            // the whole of the old best play.
            //
            // A cooldown ticks down as the turn comes back to its owner, so it
            // has lost one by the time they could use it again: 3 costs them two
            // of their own turns, 4 costs them three.

            Ability cinderLash = Ability.builder()
                .id("atk-cinderlash")
                .name("Cinder Lash")
                .description("Strikes the foe with a superheated whip of fire and embers.")
                .effects(List.of(strike4))
                .build();

            Ability ashenBrand = Ability.builder()
                .id("atk-ashenbrand")
                .name("Ashen Brand")
                .description("Marks the foe with a searing brand that keeps burning long after the blow.")
                .effects(List.of(strike1, burn))
                .cooldownTurns(3)
                .build();

            Ability kindling = Ability.builder()
                .id("atk-kindling")
                .name("Kindling")
                .description("Stokes the flame inside until the next blow lands harder, and the one after it.")
                .effects(List.of(kindle))
                .cooldownTurns(3)
                .build();

            Ability emberDrain = Ability.builder()
                .id("atk-emberdrain")
                .name("Ember Drain")
                .description("Pulls the heat out of the foe and takes it in.")
                .effects(List.of(drain3))
                .cooldownTurns(3)
                .build();

            Ability rootJab = Ability.builder()
                .id("atk-rootjab")
                .name("Root Jab")
                .description("Thrusts a hardened, sharp root from the ground at the foe.")
                .effects(List.of(strike3))
                .build();

            Ability brambleSnare = Ability.builder()
                .id("atk-bramblesnare")
                .name("Bramble Snare")
                .description("Binds the foe in creeping thorns that draw tighter with every turn.")
                .effects(List.of(poison))
                .cooldownTurns(4)
                .build();

            Ability barkWard = Ability.builder()
                .id("atk-barkward")
                .name("Bark Ward")
                .description("Grows a layer of hardened bark that takes the next blows in its place.")
                .effects(List.of(ward5))
                .cooldownTurns(5)
                .build();

            Ability regrowth = Ability.builder()
                .id("atk-regrowth")
                .name("Regrowth")
                .description("Puts roots down and lets the forest close its wounds.")
                .effects(List.of(mend40))
                .cooldownTurns(4)
                .build();

            Ability riptideLash = Ability.builder()
                .id("atk-riptidelash")
                .name("Riptide Lash")
                .description("Strikes the opponent with a sudden, forceful current of water.")
                .effects(List.of(strike3))
                .build();

            Ability undertow = Ability.builder()
                .id("atk-undertow")
                .name("Undertow")
                .description("Drags the foe under a slow, heavy current until it drifts off.")
                .effects(List.of(strike1, sleep))
                .cooldownTurns(4)
                .build();

            Ability erosion = Ability.builder()
                .id("atk-erosion")
                .name("Erosion")
                .description("Wears at the foe's guard until it no longer holds.")
                .effects(List.of(erode))
                .cooldownTurns(3)
                .build();

            Ability clearWater = Ability.builder()
                .id("atk-clearwater")
                .name("Clear Water")
                .description("Washes off whatever clings, and closes a little of what it left behind.")
                .effects(List.of(cleanse, mend20))
                .cooldownTurns(4)
                .build();

            Ability staticSnap = Ability.builder()
                .id("atk-staticsnap")
                .name("Static Snap")
                .description("Bites down with jaws that spark on contact.")
                .effects(List.of(strike3))
                .build();

            Ability joltLock = Ability.builder()
                .id("atk-joltlock")
                .name("Jolt Lock")
                .description("Sends a current through the foe that leaves its limbs slow to answer.")
                .effects(List.of(strike1, paralysis))
                .cooldownTurns(3)
                .build();

            Ability overcharge = Ability.builder()
                .id("atk-overcharge")
                .name("Overcharge")
                .description("Pulls in more current than its body should hold, and moves on the surge.")
                .effects(List.of(kindle, quicken))
                .cooldownTurns(4)
                .build();

            Ability thunderclap = Ability.builder()
                .id("atk-thunderclap")
                .name("Thunderclap")
                .description("Releases everything it has stored in one deafening strike.")
                .effects(List.of(strike5))
                .cooldownTurns(3)
                .build();

            Ability gearGrind = Ability.builder()
                .id("atk-geargrind")
                .name("Gear Grind")
                .description("Catches the foe between turning cogs.")
                .effects(List.of(strike3))
                .build();

            Ability ironPlating = Ability.builder()
                .id("atk-ironplating")
                .name("Iron Plating")
                .description("Bolts another layer of scrap over its casing.")
                .effects(List.of(harden))
                .cooldownTurns(3)
                .build();

            Ability pistonSlam = Ability.builder()
                .id("atk-pistonslam")
                .name("Piston Slam")
                .description("Winds a spring all the way back and lets it go.")
                .effects(List.of(strike5))
                .cooldownTurns(3)
                .build();

            Ability mainspring = Ability.builder()
                .id("atk-mainspring")
                .name("Mainspring")
                .description("Rewinds its own works and sets the bent parts straight.")
                .effects(List.of(mend30))
                .cooldownTurns(4)
                .build();

            Ability corrosiveBite = Ability.builder()
                .id("atk-corrosivebite")
                .name("Corrosive Bite")
                .description("Sinks dripping fangs into the foe.")
                .effects(List.of(strike3))
                .build();

            Ability noxiousFumes = Ability.builder()
                .id("atk-noxiousfumes")
                .name("Noxious Fumes")
                .description("Breathes a sour cloud that the foe keeps breathing long after.")
                .effects(List.of(strike1, poison))
                .cooldownTurns(4)
                .build();

            Ability enfeeble = Ability.builder()
                .id("atk-enfeeble")
                .name("Enfeeble")
                .description("Coats the foe in an ooze that saps the strength from its blows.")
                .effects(List.of(weaken))
                .cooldownTurns(3)
                .build();

            Ability leechOoze = Ability.builder()
                .id("atk-leechooze")
                .name("Leech Ooze")
                .description("Clings to the foe and feeds on what it takes.")
                .effects(List.of(drain3))
                .cooldownTurns(3)
                .build();

            Ability impactPunch = Ability.builder()
                .id("atk-impactpunch")
                .name("Impact Punch")
                .description("A short, straight punch with the whole body behind it.")
                .effects(List.of(strike3))
                .build();

            Ability concussionWave = Ability.builder()
                .id("atk-concussionwave")
                .name("Concussion Wave")
                .description("Stamps the ground hard enough to leave the foe staggering.")
                .effects(List.of(strike2, slow))
                .cooldownTurns(3)
                .build();

            Ability brace = Ability.builder()
                .id("atk-brace")
                .name("Brace")
                .description("Sets its feet, steadies its breathing and shrugs off the last blow.")
                .effects(List.of(harden, mend20))
                .cooldownTurns(4)
                .build();

            Ability haymaker = Ability.builder()
                .id("atk-haymaker")
                .name("Haymaker")
                .description("A wild, looping swing that leaves it wide open if it misses the moment.")
                .effects(List.of(strike6))
                .cooldownTurns(4)
                .build();

            List<Ability> abilities = List.of(
                cinderLash, ashenBrand, kindling, emberDrain,
                rootJab, brambleSnare, barkWard, regrowth,
                riptideLash, undertow, erosion, clearWater,
                staticSnap, joltLock, overcharge, thunderclap,
                gearGrind, ironPlating, pistonSlam, mainspring,
                corrosiveBite, noxiousFumes, enfeeble, leechOoze,
                impactPunch, concussionWave, brace, haymaker
            );
            abilityRepository.saveAll(abilities);

            // Critters

            Critter searfiend = Critter.builder()
                .id("fire-searfiend")
                .name("Searfiend")
                .description("Born in the heart of a volcano, its shell of cooling magma is held together by the flame inside it. It burns hottest and shortest: nothing it faces outlasts it, so nothing it faces is given the time to.")
                .type(CritterType.FIRE)
                .baseStats(BaseStats.builder().health(11).attack(7).defense(5).speed(8).build())
                .abilities(List.of(cinderLash, ashenBrand, kindling, emberDrain))
                .build();

            Critter sylvanSentinel = Critter.builder()
                .id("grass-sylvansentinel")
                .name("Sylvan Sentinel")
                .description("An old guardian of the deep woods, bark over living vine. It does not hurry and it does not strike hard. It puts roots into whatever stands in front of it and waits for the forest to finish the work.")
                .type(CritterType.GRASS)
                .baseStats(BaseStats.builder().health(16).attack(5).defense(6).speed(4).build())
                .abilities(List.of(rootJab, brambleSnare, barkWard, regrowth))
                .build();

            Critter aqualing = Critter.builder()
                .id("water-aqualing")
                .name("Aqualing")
                .description("A spring spirit that has never held one shape for long. It gives ground, takes the current with it, and picks the moment a heavier opponent is off balance.")
                .type(CritterType.WATER)
                .baseStats(BaseStats.builder().health(13).attack(6).defense(5).speed(7).build())
                .abilities(List.of(riptideLash, undertow, erosion, clearWater))
                .build();

            Critter volthound = Critter.builder()
                .id("electric-volthound")
                .name("Volthound")
                .description("A hunter made of loose current, never quite the same shape twice. It is on its prey before the prey has turned, and it has to be, because it cannot take much of a hit back.")
                .type(CritterType.ELECTRIC)
                .baseStats(BaseStats.builder().health(10).attack(7).defense(5).speed(9).build())
                .abilities(List.of(staticSnap, joltLock, overcharge, thunderclap))
                .build();

            Critter cogling = Critter.builder()
                .id("metal-cogling")
                .name("Cogling")
                .description("A small, fussy creature of discarded clockwork. It adds a plate for every blow it takes, and a Cogling that has been left alone long enough stops being worth hitting at all.")
                .type(CritterType.METAL)
                .baseStats(BaseStats.builder().health(14).attack(6).defense(6).speed(5).build())
                .abilities(List.of(gearGrind, ironPlating, pistonSlam, mainspring))
                .build();

            Critter miasmite = Critter.builder()
                .id("toxic-miasmite")
                .name("Miasmite")
                .description("Born in a polluted swamp, its body leaks a corrosive ooze. It does not beat anything quickly. It makes everything near it weaker and sicker, and waits.")
                .type(CritterType.TOXIC)
                .baseStats(BaseStats.builder().health(14).attack(5).defense(5).speed(7).build())
                .abilities(List.of(corrosiveBite, noxiousFumes, enfeeble, leechOoze))
                .build();

            Critter strikon = Critter.builder()
                .id("kinetic-strikon")
                .name("Strikon")
                .description("Heavily built, and channels raw force into its limbs instead of any element. It throws the biggest punch in the game, and spends most of a fight setting it up.")
                .type(CritterType.KINETIC)
                .baseStats(BaseStats.builder().health(12).attack(8).defense(5).speed(6).build())
                .abilities(List.of(impactPunch, concussionWave, brace, haymaker))
                .build();

            List<Critter> critters = List.of(searfiend, sylvanSentinel, aqualing, volthound, cogling, miasmite, strikon);
            critterRepository.saveAll(critters);

            // Whatever is no longer declared above is dropped, so the reference
            // collections hold the seed and nothing else. Listing retired ids by
            // hand missed documents written under an id scheme this seeder no
            // longer uses, which is how a retired critter stayed in the game.
            effectRepository.deleteByIdNotIn(effects.stream().map(Effect::getId).toList());
            abilityRepository.deleteByIdNotIn(abilities.stream().map(Ability::getId).toList());
            critterRepository.deleteByIdNotIn(critters.stream().map(Critter::getId).toList());

            // A roster embeds a copy of each critter, abilities included, so a
            // player keeps whatever moves existed when they registered. Moves
            // are reference data, so every copy is brought up to the seed. It
            // runs before any player is read, because a copy that names an
            // effect class no longer in the game cannot be read at all.
            for (Critter critter : critters) {
                refreshRosterAbilities(mongoTemplate, critter);
            }

            // Demo accounts. Both hold the whole triangle, so a battle can reach
            // every matchup and neither side is ahead on the draw.
            //
            // On a closed loop every fixed lead pairing is either a mirror or a
            // 1.5x, so the only even opening is a mirror. It leads with the wall
            // rather than the aggressor: a Searfiend mirror is two hits, and a
            // mirror ties on speed, so the coin toss that breaks the tie would
            // decide that opening. A Sylvan Sentinel mirror takes six, which
            // leaves room for the first real decision, which is who switches.
            List<Critter> demoRoster = List.of(sylvanSentinel, searfiend, aqualing);

            seedDemoAccount(playerRepository, "p1", "BlueOak", demoRoster);
            seedDemoAccount(playerRepository, "p2", "RedAsh", demoRoster);
        };
    }

    /** Replaces the embedded abilities of this critter in every roster that holds it. */
    private void refreshRosterAbilities(MongoTemplate mongoTemplate, Critter critter) {
        mongoTemplate.updateMulti(
                Query.query(Criteria.where("roster._id").is(critter.getId())),
                new Update().set("roster.$[held].abilities", critter.getAbilities())
                        .filterArray(Criteria.where("held._id").is(critter.getId())),
                Player.class);
    }

    /**
     * The roster and the password are reset on every startup, so the fixture
     * always matches what is seeded beside it. Stats and match history are left
     * alone: they are the only part of a demo account worth accumulating.
     */
    private void seedDemoAccount(PlayerRepository playerRepository, String id, String username, List<Critter> roster) {
        Player player = playerRepository.findById(id).orElseGet(() -> Player.builder()
                .id(id)
                .username(username)
                .stats(PlayerStats.builder().build())
                .build());

        player.setRoster(roster);
        player.setPassword(PasswordUtil.hashPassword(DEMO_PASSWORD));

        playerRepository.save(player);
    }
}
