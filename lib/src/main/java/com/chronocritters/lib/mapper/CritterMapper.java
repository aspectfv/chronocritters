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
    /**
     * How much of its base stats a critter gains per level. Eight percent keeps
     * a level gap readable without letting it decide a battle on its own: ten
     * levels is a little under double.
     */
    private static final double GROWTH_PER_LEVEL = 0.08;

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
            .speed(proto.getSpeed())
            .level(proto.getLevel())
            .build();
    }

    private static BaseStatsProto toProto(BaseStats model) {
        return BaseStatsProto.newBuilder()
            .setHealth(model.getHealth())
            .setAttack(model.getAttack())
            .setDefense(model.getDefense())
            .setSpeed(model.getSpeed())
            .setLevel(model.getLevel())
            .build();
    }

    private static CurrentStats toCurrentStats(BaseStats baseStats) {
        if (baseStats.getHealth() < 1) throw new IllegalArgumentException("BaseStats health must be at least 1");

        int maxHp = atLevel(baseStats.getHealth(), baseStats.getLevel());

        return CurrentStats.builder()
            .maxHp(maxHp)
            .currentHp(maxHp)
            .currentAtk(atLevel(baseStats.getAttack(), baseStats.getLevel()))
            .currentDef(atLevel(baseStats.getDefense(), baseStats.getLevel()))
            .currentSpd(atLevel(baseStats.getSpeed(), baseStats.getLevel()))
            .build();
    }

    /**
     * A level below one means the document was written before levels crossed
     * the wire, so it is read as level one rather than scaled downwards.
     */
    private static int atLevel(int baseStat, int level) {
        int levelsGained = Math.max(1, level) - 1;
        return (int) Math.round(baseStat * (1 + GROWTH_PER_LEVEL * levelsGained));
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