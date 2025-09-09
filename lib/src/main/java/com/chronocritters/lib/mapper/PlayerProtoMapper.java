package com.chronocritters.lib.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.AbilityType;
import com.chronocritters.lib.model.BaseStats;
import com.chronocritters.lib.model.Critter;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.CritterType;
import com.chronocritters.lib.model.CurrentStats;
import com.chronocritters.lib.model.Effect;
import com.chronocritters.lib.model.EffectType;
import com.chronocritters.lib.model.Player;
import com.chronocritters.lib.model.PlayerState;
import com.chronocritters.proto.player.PlayerProto.AbilityProto;
import com.chronocritters.proto.player.PlayerProto.AbilityTypeProto;
import com.chronocritters.proto.player.PlayerProto.BaseStatsProto;
import com.chronocritters.proto.player.PlayerProto.CritterProto;
import com.chronocritters.proto.player.PlayerProto.CritterTypeProto;
import com.chronocritters.proto.player.PlayerProto.EffectProto;
import com.chronocritters.proto.player.PlayerProto.EffectTypeProto;
import com.chronocritters.proto.player.PlayerProto.PlayerResponse;

public final class PlayerProtoMapper {

    private PlayerProtoMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    // --- Proto to Model ---
    public static PlayerState convertToPlayerState(PlayerResponse playerResponse) {
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
        return Critter.builder()
            .id(critterProto.getId())
            .name(critterProto.getName())
            .type(convertCritterTypeProtoToModel(critterProto.getType()))
            .baseStats(convertBaseStatsProtoToModel(critterProto.getBaseStats()))
            .abilities(critterProto.getAbilitiesList().stream()
                .map(PlayerProtoMapper::convertAbilityProtoToModel)
                .collect(Collectors.toList()))
            .build();
    }

    private static CritterState convertCritterToState(Critter critter) {
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
        return CurrentStats.builder()
            .maxHp(baseStats.getHealth())
            .currentHp(baseStats.getHealth())
            .currentAtk(baseStats.getAttack())
            .currentDef(baseStats.getDefense())
            .build();
    }

    private static BaseStats convertBaseStatsProtoToModel(BaseStatsProto baseStatsProto) {
        return BaseStats.builder()
            .health(baseStatsProto.getHealth())
            .attack(baseStatsProto.getAttack())
            .defense(baseStatsProto.getDefense())
            .build();
    }

    private static Ability convertAbilityProtoToModel(AbilityProto abilityProto) {
        return Ability.builder()
            .id(abilityProto.getId())
            .name(abilityProto.getName())
            .type(convertAbilityTypeProtoToModel(abilityProto.getType()))
            .power(abilityProto.getPower())
            .effects(abilityProto.getEffectsList().stream()
                .map(PlayerProtoMapper::convertEffectProtoToModel)
                .collect(Collectors.toList()))
            .build();
    }

    private static Effect convertEffectProtoToModel(EffectProto effectProto) {
        return Effect.builder()
            .id(effectProto.getId())
            .name(effectProto.getName())
            .type(convertEffectTypeProtoToModel(effectProto.getType()))
            .power(effectProto.getPower())
            .duration(effectProto.getDuration())
            .chance(effectProto.getChance())
            .build();
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

    private static AbilityType convertAbilityTypeProtoToModel(AbilityTypeProto protoType) {
        return switch (protoType) {
            case ATTACK -> AbilityType.ATTACK;
            case DEFENSE -> AbilityType.DEFENSE;
            case HEAL -> AbilityType.HEAL;
            case EFFECT -> AbilityType.EFFECT;
            case ABILITY_TYPE_UNSPECIFIED, UNRECOGNIZED -> 
                throw new IllegalArgumentException("Unknown ability type: " + protoType);
        };
    }

    private static EffectType convertEffectTypeProtoToModel(EffectTypeProto protoType) {
        return switch (protoType) {
            case DAMAGE_OVER_TIME -> EffectType.DAMAGE_OVER_TIME;
            case SKIP_TURN -> EffectType.SKIP_TURN;
            case BUFF -> EffectType.BUFF;
            case DEBUFF -> EffectType.DEBUFF;
            case EFFECT_TYPE_UNSPECIFIED, UNRECOGNIZED -> 
                throw new IllegalArgumentException("Unknown effect type: " + protoType);
        };
    }

    // --- Model to Proto ---
    public static CritterProto convertCritterModelToProto(Critter critter) {
        CritterProto.Builder builder = CritterProto.newBuilder()
            .setId(critter.getId())
            .setName(critter.getName())
            .setType(convertCritterTypeModelToProto(critter.getType()));

        if (critter.getBaseStats() != null) {
            BaseStatsProto baseStats = BaseStatsProto.newBuilder()
                .setHealth(critter.getBaseStats().getHealth())
                .setAttack(critter.getBaseStats().getAttack())
                .setDefense(critter.getBaseStats().getDefense())
                .build();
            builder.setBaseStats(baseStats);
        }

        critter.getAbilities().stream()
            .map(PlayerProtoMapper::convertAbilityModelToProto)
            .forEach(builder::addAbilities);

        return builder.build();
    }

    private static AbilityProto convertAbilityModelToProto(Ability ability) {
        AbilityProto.Builder builder = AbilityProto.newBuilder()
            .setId(ability.getId())
            .setName(ability.getName())
            .setType(convertAbilityTypeModelToProto(ability.getType()))
            .setPower(ability.getPower());

        ability.getEffects().stream()
            .map(PlayerProtoMapper::convertEffectModelToProto)
            .forEach(builder::addEffects);

        return builder.build();
    }

    private static EffectProto convertEffectModelToProto(Effect effect) {
        return EffectProto.newBuilder()
            .setId(effect.getId())
            .setName(effect.getName())
            .setType(convertEffectTypeModelToProto(effect.getType()))
            .setPower(effect.getPower())
            .setDuration(effect.getDuration())
            .setChance(effect.getChance())
            .build();
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

    private static AbilityTypeProto convertAbilityTypeModelToProto(AbilityType type) {
        return switch (type) {
            case ATTACK -> AbilityTypeProto.ATTACK;
            case DEFENSE -> AbilityTypeProto.DEFENSE;
            case HEAL -> AbilityTypeProto.HEAL;
            case EFFECT -> AbilityTypeProto.EFFECT;
        };
    }

    private static EffectTypeProto convertEffectTypeModelToProto(EffectType type) {
        return switch (type) {
            case DAMAGE_OVER_TIME -> EffectTypeProto.DAMAGE_OVER_TIME;
            case SKIP_TURN -> EffectTypeProto.SKIP_TURN;
            case BUFF -> EffectTypeProto.BUFF;
            case DEBUFF -> EffectTypeProto.DEBUFF;
        };
    }
}