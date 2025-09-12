package com.chronocritters.lib.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.BaseStats;
import com.chronocritters.lib.model.Critter;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.CritterType;
import com.chronocritters.lib.model.CurrentStats;
import com.chronocritters.lib.model.Effect;
import com.chronocritters.lib.model.Player;
import com.chronocritters.lib.model.PlayerState;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.effects.DamageOverTimeEffect;
import com.chronocritters.lib.model.effects.SkipTurnEffect;
import com.chronocritters.proto.player.PlayerProto.AbilityProto;
import com.chronocritters.proto.player.PlayerProto.BaseStatsProto;
import com.chronocritters.proto.player.PlayerProto.CritterProto;
import com.chronocritters.proto.player.PlayerProto.CritterTypeProto;
import com.chronocritters.proto.player.PlayerProto.DamageEffectProto;
import com.chronocritters.proto.player.PlayerProto.DamageOverTimeEffectProto;
import com.chronocritters.proto.player.PlayerProto.EffectProto;
import com.chronocritters.proto.player.PlayerProto.PlayerResponse;
import com.chronocritters.proto.player.PlayerProto.SkipTurnEffectProto;

public final class PlayerProtoMapper {

    private PlayerProtoMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    // --- Proto to Model ---
    public static PlayerState convertToPlayerState(PlayerResponse playerResponse) {
        if (playerResponse == null) throw new IllegalArgumentException("PlayerResponse cannot be null");
        if (playerResponse.getId().isBlank()) throw new IllegalArgumentException("Player ID from PlayerResponse cannot be blank");
        if (playerResponse.getUsername().isBlank()) throw new IllegalArgumentException("Username from PlayerResponse cannot be blank");
        if (playerResponse.getRosterList() == null) throw new IllegalArgumentException("Roster list from PlayerResponse cannot be null");
        if (playerResponse.getRosterList().isEmpty()) throw new IllegalArgumentException("Roster list from PlayerResponse cannot be empty");

        List<Critter> critterRoster = playerResponse.getRosterList().stream()
            .map(PlayerProtoMapper::convertCritterProtoToModel)
            .collect(Collectors.toList());

        List<CritterState> battleRoster = critterRoster.stream()
            .map(PlayerProtoMapper::convertCritterToState)
            .collect(Collectors.toList());

        Player player = Player.builder()
            .id(playerResponse.getId())
            .username(playerResponse.getUsername())
            .roster(critterRoster)
            .build();

        return PlayerState.builder()
            .id(player.getId())
            .username(player.getUsername())
            .hasTurn(false)
            .activeCritterIndex(battleRoster.isEmpty() ? -1 : 0)
            .roster(battleRoster)
            .build();
    }

    private static Critter convertCritterProtoToModel(CritterProto critterProto) {
        if (critterProto == null) throw new IllegalArgumentException("CritterProto cannot be null");
        if (critterProto.getId().isBlank()) throw new IllegalArgumentException("Critter ID from CritterProto cannot be blank");
        if (critterProto.getName().isBlank()) throw new IllegalArgumentException("Critter name from CritterProto cannot be blank");
        if (critterProto.getDescription().isBlank()) throw new IllegalArgumentException("Critter description from CritterProto cannot be blank");
        if (critterProto.getAbilitiesList() == null) throw new IllegalArgumentException("Abilities list from CritterProto cannot be null");
        if (critterProto.getBaseStats() == null) throw new IllegalArgumentException("BaseStats from CritterProto cannot be null");
        if (critterProto.getAbilitiesList().isEmpty()) throw new IllegalArgumentException("Abilities list from CritterProto cannot be empty");
        
        return Critter.builder()
            .id(critterProto.getId())
            .name(critterProto.getName())
            .description(critterProto.getDescription())
            .type(convertCritterTypeProtoToModel(critterProto.getType()))
            .baseStats(convertBaseStatsProtoToModel(critterProto.getBaseStats()))
            .abilities(critterProto.getAbilitiesList().stream()
                .map(PlayerProtoMapper::convertAbilityProtoToModel)
                .collect(Collectors.toList()))
            .build();
    }

    private static CritterState convertCritterToState(Critter critter) {
        if (critter == null) throw new IllegalArgumentException("Critter cannot be null");
        if (critter.getId().isBlank()) throw new IllegalArgumentException("Critter ID cannot be blank");
        if (critter.getName().isBlank()) throw new IllegalArgumentException("Critter name cannot be blank");
        if (critter.getBaseStats() == null) throw new IllegalArgumentException("Critter base stats cannot be null");
        if (critter.getAbilities() == null) throw new IllegalArgumentException("Critter abilities cannot be null");
        if (critter.getAbilities().isEmpty()) throw new IllegalArgumentException("Critter abilities cannot be empty");

        return CritterState.builder()
            .id(critter.getId())
            .name(critter.getName())
            .type(critter.getType())
            .stats(convertCurrentStats(critter.getBaseStats()))
            .abilities(critter.getAbilities())
            .fainted(false)
            .build();
    }

    private static CurrentStats convertCurrentStats(BaseStats baseStats) {
        if (baseStats == null) throw new IllegalArgumentException("BaseStats cannot be null");
        if (baseStats.getHealth() < 1) throw new IllegalArgumentException("BaseStats health must be at least 1");
        if (baseStats.getAttack() < 0) throw new IllegalArgumentException("BaseStats attack cannot be negative");
        if (baseStats.getDefense() < 0) throw new IllegalArgumentException("BaseStats defense cannot be negative");

        return CurrentStats.builder()
            .maxHp(baseStats.getHealth())
            .currentHp(baseStats.getHealth())
            .currentAtk(baseStats.getAttack())
            .currentDef(baseStats.getDefense())
            .build();
    }

    private static BaseStats convertBaseStatsProtoToModel(BaseStatsProto baseStatsProto) {
        if (baseStatsProto.getHealth() < 1) throw new IllegalArgumentException("BaseStats health must be at least 1");
        if (baseStatsProto.getAttack() < 0) throw new IllegalArgumentException("BaseStats attack cannot be negative");
        if (baseStatsProto.getDefense() < 0) throw new IllegalArgumentException("BaseStats defense cannot be negative");

        return BaseStats.builder()
            .health(baseStatsProto.getHealth())
            .attack(baseStatsProto.getAttack())
            .defense(baseStatsProto.getDefense())
            .build();
    }

    private static Ability convertAbilityProtoToModel(AbilityProto abilityProto) {
        if (abilityProto.getId().isBlank()) throw new IllegalArgumentException("Ability ID from AbilityProto cannot be blank");
        if (abilityProto.getName().isBlank()) throw new IllegalArgumentException("Ability name from AbilityProto cannot be blank");
        if (abilityProto.getEffectsList() == null) throw new IllegalArgumentException("Effects list from AbilityProto cannot be null");
        if (abilityProto.getEffectsList().isEmpty()) throw new IllegalArgumentException("Effects list from AbilityProto cannot be empty");

        return Ability.builder()
            .id(abilityProto.getId())
            .name(abilityProto.getName())
            .effects(abilityProto.getEffectsList().stream()
                .map(PlayerProtoMapper::convertEffectProtoToModel)
                .collect(Collectors.toList()))
            .build();
    }

    private static Effect convertEffectProtoToModel(EffectProto effectProto) {
        if (effectProto.getId().isBlank()) throw new IllegalArgumentException("Effect ID from EffectProto cannot be blank");
        if (effectProto.getDescription().isBlank()) throw new IllegalArgumentException("Effect description from EffectProto cannot be blank");

        switch (effectProto.getEffectDataCase()) {
            case DAMAGE_EFFECT:
                if (effectProto.getDamageEffect().getDamage() < 0) throw new IllegalArgumentException("Damage in DamageEffect cannot be negative");
                return DamageEffect.builder()
                        .id(effectProto.getId())
                        .description(effectProto.getDescription())
                        .damage(effectProto.getDamageEffect().getDamage())
                        .build();
            case DAMAGE_OVER_TIME_EFFECT:
                if (effectProto.getDamageOverTimeEffect().getDamagePerTurn() < 0) throw new IllegalArgumentException("Damage per turn in DamageOverTimeEffect cannot be negative");
                if (effectProto.getDamageOverTimeEffect().getDuration() < 1) throw new IllegalArgumentException("Duration in DamageOverTimeEffect must be at least 1");
                return DamageOverTimeEffect.builder()
                        .id(effectProto.getId())
                        .description(effectProto.getDescription())
                        .damagePerTurn(effectProto.getDamageOverTimeEffect().getDamagePerTurn())
                        .duration(effectProto.getDamageOverTimeEffect().getDuration())
                        .build();
            case SKIP_TURN_EFFECT:
                if (effectProto.getSkipTurnEffect().getDuration() < 1) throw new IllegalArgumentException("Duration in SkipTurnEffect must be at least 1");
                return SkipTurnEffect.builder()
                        .id(effectProto.getId())
                        .description(effectProto.getDescription())
                        .duration(effectProto.getSkipTurnEffect().getDuration())
                        .build();
            case EFFECTDATA_NOT_SET:
            default:
                throw new IllegalArgumentException("Unknown or unset effect type in EffectProto");
        }
    }

    private static CritterType convertCritterTypeProtoToModel(CritterTypeProto protoType) {
        return switch (protoType) {
            case FIRE -> CritterType.FIRE;
            case WATER -> CritterType.WATER;
            case GRASS -> CritterType.GRASS;
            case ELECTRIC -> CritterType.ELECTRIC;
            case METAL -> CritterType.METAL;
            case TOXIC -> CritterType.TOXIC;
            case KINETIC -> CritterType.KINETIC;
            case CRITTER_TYPE_UNSPECIFIED, UNRECOGNIZED -> 
                throw new IllegalArgumentException("Unknown critter type: " + protoType);
        };
    }

    // --- Model to Proto ---
    public static CritterProto convertCritterModelToProto(Critter critter) {
        if (critter == null) {
            throw new IllegalArgumentException("Critter cannot be null");
        }
        if (critter.getId().isBlank()) {
            throw new IllegalArgumentException("Critter ID cannot be blank");
        }
        if (critter.getName().isBlank()) {
            throw new IllegalArgumentException("Critter name cannot be blank");
        }
        if (critter.getDescription().isBlank()) {
            throw new IllegalArgumentException("Critter description cannot be blank");
        }
        if (critter.getBaseStats() == null) {
            throw new IllegalArgumentException("Critter base stats cannot be null");
        }
        if (critter.getAbilities() == null) {
            throw new IllegalArgumentException("Critter abilities cannot be null");
        }

        CritterProto.Builder builder = CritterProto.newBuilder()
            .setId(critter.getId())
            .setName(critter.getName())
            .setDescription(critter.getDescription())
            .setType(convertCritterTypeModelToProto(critter.getType()));

        BaseStatsProto baseStats = BaseStatsProto.newBuilder()
            .setHealth(critter.getBaseStats().getHealth())
            .setAttack(critter.getBaseStats().getAttack())
            .setDefense(critter.getBaseStats().getDefense())
            .build();
        builder.setBaseStats(baseStats);

        critter.getAbilities().stream()
            .map(PlayerProtoMapper::convertAbilityModelToProto)
            .forEach(builder::addAbilities);

        return builder.build();
    }

    private static AbilityProto convertAbilityModelToProto(Ability ability) {
        if (ability == null) {
            throw new IllegalArgumentException("Ability cannot be null");
        }
        if (ability.getId().isBlank()) {
            throw new IllegalArgumentException("Ability ID cannot be blank");
        }
        if (ability.getName().isBlank()) {
            throw new IllegalArgumentException("Ability name cannot be blank");
        }
        if (ability.getEffects() == null) {
            throw new IllegalArgumentException("Ability effects cannot be null");
        }
        if (ability.getEffects().isEmpty()) {
            throw new IllegalArgumentException("Ability effects cannot be empty");
        }

        AbilityProto.Builder builder = AbilityProto.newBuilder()
            .setId(ability.getId())
            .setName(ability.getName())
            .setDescription(ability.getDescription());

        ability.getEffects().stream()
            .map(PlayerProtoMapper::convertEffectModelToProto)
            .forEach(builder::addEffects);

        return builder.build();
    }

    private static EffectProto convertEffectModelToProto(Effect effect) {
        if (effect == null) throw new IllegalArgumentException("Effect cannot be null");
        if (effect.getId().isBlank()) throw new IllegalArgumentException("Effect ID cannot be blank");
        if (effect.getDescription().isBlank()) throw new IllegalArgumentException("Effect description cannot be blank");
        
        EffectProto.Builder builder = EffectProto.newBuilder()
            .setId(effect.getId())
            .setDescription(effect.getDescription());
        
        switch (effect) {
            case DamageEffect damageEffect -> {
                DamageEffectProto damageEffectProto = DamageEffectProto.newBuilder()
                    .setDamage(damageEffect.getDamage())
                    .build();
                builder.setDamageEffect(damageEffectProto);
            }
            case DamageOverTimeEffect dotEffect -> {
                DamageOverTimeEffectProto dotEffectProto = DamageOverTimeEffectProto.newBuilder()
                    .setDamagePerTurn(dotEffect.getDamagePerTurn())
                    .setDuration(dotEffect.getDuration())
                    .build();
                builder.setDamageOverTimeEffect(dotEffectProto);
            }
            case SkipTurnEffect skipTurnEffect -> {
                SkipTurnEffectProto skipTurnEffectProto = SkipTurnEffectProto.newBuilder()
                    .setDuration(skipTurnEffect.getDuration())
                    .build();
                builder.setSkipTurnEffect(skipTurnEffectProto);
            }
            default -> throw new IllegalArgumentException("Unknown effect type: " + effect.getClass().getName());
        }
        return builder.build();
    }

    private static CritterTypeProto convertCritterTypeModelToProto(CritterType type) {
        return switch (type) {
            case FIRE -> CritterTypeProto.FIRE;
            case WATER -> CritterTypeProto.WATER;
            case GRASS -> CritterTypeProto.GRASS;
            case ELECTRIC -> CritterTypeProto.ELECTRIC;
            case METAL -> CritterTypeProto.METAL;
            case TOXIC -> CritterTypeProto.TOXIC;
            case KINETIC -> CritterTypeProto.KINETIC;
        };
    }
}