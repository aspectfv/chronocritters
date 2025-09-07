package com.chronocritters.user.converter;

import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.Critter;
import com.chronocritters.proto.player.PlayerProto.AbilityProto;
import com.chronocritters.proto.player.PlayerProto.AbilityTypeProto;
import com.chronocritters.proto.player.PlayerProto.BaseStatsProto;
import com.chronocritters.proto.player.PlayerProto.CritterProto;
import com.chronocritters.proto.player.PlayerProto.CritterTypeProto;

public class PlayerProtoConverter {

    public static CritterProto convertCritterToProto(Critter critter) {
        CritterProto.Builder builder = CritterProto.newBuilder()
                .setId(critter.getId())
                .setName(critter.getName())
                .setType(convertCritterType(critter.getType()));

        // Convert base stats
        if (critter.getBaseStats() != null) {
            BaseStatsProto baseStats = BaseStatsProto.newBuilder()
                    .setHealth(critter.getBaseStats().getHealth())
                    .setAttack(critter.getBaseStats().getAttack())
                    .setDefense(critter.getBaseStats().getDefense())
                    .build();
            builder.setBaseStats(baseStats);
        }

        // Convert abilities
        if (critter.getAbilities() != null) {
            for (Ability ability : critter.getAbilities()) {
                AbilityProto abilityProto = convertAbilityToProto(ability);
                builder.addAbilities(abilityProto);
            }
        }

        return builder.build();
    }

    public static AbilityProto convertAbilityToProto(Ability ability) {
        return AbilityProto.newBuilder()
                .setId(ability.getId())
                .setName(ability.getName())
                .setType(convertAbilityType(ability.getType()))
                .setPower(ability.getPower())
                .build();
    }

    public static CritterTypeProto convertCritterType(com.chronocritters.lib.model.CritterType type) {
        return switch (type) {
            case FIRE -> CritterTypeProto.FIRE;
            case WATER -> CritterTypeProto.WATER;
            case GRASS -> CritterTypeProto.GRASS;
            case ELECTRIC -> CritterTypeProto.ELECTRIC;
            case STEEL -> CritterTypeProto.STEEL;
            default -> CritterTypeProto.CRITTER_TYPE_UNSPECIFIED;
        };
    }

    public static AbilityTypeProto convertAbilityType(com.chronocritters.lib.model.AbilityType type) {
        return switch (type) {
            case ATTACK -> AbilityTypeProto.ATTACK;
            case DEFENSE -> AbilityTypeProto.DEFENSE;
            case SUPPORT -> AbilityTypeProto.SUPPORT;
            default -> AbilityTypeProto.ABILITY_TYPE_UNSPECIFIED;
        };
    }
}