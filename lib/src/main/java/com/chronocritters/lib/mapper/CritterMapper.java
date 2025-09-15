package com.chronocritters.lib.mapper;

import com.chronocritters.lib.model.battle.CritterState;
import com.chronocritters.lib.model.battle.CurrentStats;
import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.lib.model.enums.CritterType;
import com.chronocritters.proto.player.PlayerProto.BaseStatsProto;
import com.chronocritters.proto.player.PlayerProto.CritterProto;
import com.chronocritters.proto.player.PlayerProto.CritterTypeProto;

import java.util.stream.Collectors;

public final class CritterMapper {
    private CritterMapper() {}

    public static Critter toModel(CritterProto proto) {
        if (proto == null) throw new IllegalArgumentException("CritterProto cannot be null");
        if (proto.getId().isBlank()) throw new IllegalArgumentException("Critter ID from CritterProto cannot be blank");

        return Critter.builder()
            .id(proto.getId())
            .name(proto.getName())
            .description(proto.getDescription())
            .type(toModel(proto.getType()))
            .baseStats(toModel(proto.getBaseStats()))
            .abilities(proto.getAbilitiesList().stream()
                .map(AbilityMapper::toModel)
                .collect(Collectors.toList()))
            .build();
    }

    public static CritterProto toProto(Critter model) {
        if (model == null) throw new IllegalArgumentException("Critter cannot be null");
        if (model.getId().isBlank()) throw new IllegalArgumentException("Critter ID cannot be blank");

        CritterProto.Builder builder = CritterProto.newBuilder()
            .setId(model.getId())
            .setName(model.getName())
            .setDescription(model.getDescription())
            .setType(toProto(model.getType()))
            .setBaseStats(toProto(model.getBaseStats()));

        model.getAbilities().stream()
            .map(AbilityMapper::toProto)
            .forEach(builder::addAbilities);

        return builder.build();
    }

    public static CritterState toCritterState(Critter model) {
        if (model == null) throw new IllegalArgumentException("Critter cannot be null");
        
        return CritterState.builder()
            .id(model.getId())
            .name(model.getName())
            .type(model.getType())
            .stats(toCurrentStats(model.getBaseStats()))
            .abilities(model.getAbilities())
            .build();
    }

    private static BaseStats toModel(BaseStatsProto proto) {
        if (proto.getHealth() < 1) throw new IllegalArgumentException("BaseStats health must be at least 1");
        return BaseStats.builder()
            .health(proto.getHealth())
            .attack(proto.getAttack())
            .defense(proto.getDefense())
            .build();
    }

    private static BaseStatsProto toProto(BaseStats model) {
        return BaseStatsProto.newBuilder()
            .setHealth(model.getHealth())
            .setAttack(model.getAttack())
            .setDefense(model.getDefense())
            .build();
    }

    private static CurrentStats toCurrentStats(BaseStats baseStats) {
        if (baseStats.getHealth() < 1) throw new IllegalArgumentException("BaseStats health must be at least 1");
        return CurrentStats.builder()
            .maxHp(baseStats.getHealth())
            .currentHp(baseStats.getHealth())
            .currentAtk(baseStats.getAttack())
            .currentDef(baseStats.getDefense())
            .build();
    }

    private static CritterType toModel(CritterTypeProto proto) {
        return switch (proto) {
            case FIRE -> CritterType.FIRE;
            case WATER -> CritterType.WATER;
            case GRASS -> CritterType.GRASS;
            case ELECTRIC -> CritterType.ELECTRIC;
            case METAL -> CritterType.METAL;
            case TOXIC -> CritterType.TOXIC;
            case KINETIC -> CritterType.KINETIC;
            default -> throw new IllegalArgumentException("Unknown critter type: " + proto);
        };
    }

    private static CritterTypeProto toProto(CritterType model) {
        return switch (model) {
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