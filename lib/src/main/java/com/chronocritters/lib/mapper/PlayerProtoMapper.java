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
import com.chronocritters.lib.model.Player;
import com.chronocritters.lib.model.PlayerState;
import com.chronocritters.proto.player.PlayerProto.AbilityProto;
import com.chronocritters.proto.player.PlayerProto.AbilityTypeProto;
import com.chronocritters.proto.player.PlayerProto.BaseStatsProto;
import com.chronocritters.proto.player.PlayerProto.CritterProto;
import com.chronocritters.proto.player.PlayerProto.CritterTypeProto;
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
            .build();
    }

    private static CritterType convertCritterTypeProtoToModel(CritterTypeProto protoType) {
        return switch (protoType) {
            case FIRE -> CritterType.FIRE;
            case WATER -> CritterType.WATER;
            case GRASS -> CritterType.GRASS;
            case ELECTRIC -> CritterType.ELECTRIC;
            case STEEL -> CritterType.STEEL;
            case CRITTER_TYPE_UNSPECIFIED, UNRECOGNIZED -> 
                throw new IllegalArgumentException("Unknown critter type: " + protoType);
        };
    }

    private static AbilityType convertAbilityTypeProtoToModel(AbilityTypeProto protoType) {
        return switch (protoType) {
            case ATTACK -> AbilityType.ATTACK;
            case DEFENSE -> AbilityType.DEFENSE;
            case SUPPORT -> AbilityType.SUPPORT;
            case ABILITY_TYPE_UNSPECIFIED, UNRECOGNIZED -> 
                throw new IllegalArgumentException("Unknown ability type: " + protoType);
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

        if (critter.getAbilities() != null) {
            for (Ability ability : critter.getAbilities()) {
                AbilityProto abilityProto = convertAbilityModelToProto(ability);
                builder.addAbilities(abilityProto);
            }
        }

        return builder.build();
    }

    public static AbilityProto convertAbilityModelToProto(Ability ability) {
        return AbilityProto.newBuilder()
            .setId(ability.getId())
            .setName(ability.getName())
            .setType(convertAbilityTypeModelToProto(ability.getType()))
            .setPower(ability.getPower())
            .build();
    }

    public static CritterTypeProto convertCritterTypeModelToProto(CritterType type) {
        return switch (type) {
            case FIRE -> CritterTypeProto.FIRE;
            case WATER -> CritterTypeProto.WATER;
            case GRASS -> CritterTypeProto.GRASS;
            case ELECTRIC -> CritterTypeProto.ELECTRIC;
            case STEEL -> CritterTypeProto.STEEL;
            default -> CritterTypeProto.CRITTER_TYPE_UNSPECIFIED;
        };
    }

    public static AbilityTypeProto convertAbilityTypeModelToProto(AbilityType type) {
        return switch (type) {
            case ATTACK -> AbilityTypeProto.ATTACK;
            case DEFENSE -> AbilityTypeProto.DEFENSE;
            case SUPPORT -> AbilityTypeProto.SUPPORT;
            default -> AbilityTypeProto.ABILITY_TYPE_UNSPECIFIED;
        };
    }
}