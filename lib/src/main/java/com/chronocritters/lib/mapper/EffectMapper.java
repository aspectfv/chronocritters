package com.chronocritters.lib.mapper;

import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.effects.DamageOverTimeEffect;
import com.chronocritters.lib.model.effects.SkipTurnEffect;
import com.chronocritters.proto.player.PlayerProto.DamageEffectProto;
import com.chronocritters.proto.player.PlayerProto.DamageOverTimeEffectProto;
import com.chronocritters.proto.player.PlayerProto.EffectProto;
import com.chronocritters.proto.player.PlayerProto.SkipTurnEffectProto;

public final class EffectMapper {
    private EffectMapper() {}

    public static Effect toModel(EffectProto proto) {
        if (proto.getId().isBlank()) throw new IllegalArgumentException("Effect ID from EffectProto cannot be blank");
        if (proto.getDescription().isBlank()) throw new IllegalArgumentException("Effect description from EffectProto cannot be blank");

        return switch (proto.getEffectDataCase()) {
            case DAMAGE_EFFECT -> {
                if (proto.getDamageEffect().getDamage() < 0) throw new IllegalArgumentException("Damage in DamageEffect cannot be negative");
                yield DamageEffect.builder()
                        .id(proto.getId())
                        .description(proto.getDescription())
                        .damage(proto.getDamageEffect().getDamage())
                        .build();
            }
            case DAMAGE_OVER_TIME_EFFECT -> {
                if (proto.getDamageOverTimeEffect().getDamagePerTurn() < 0) throw new IllegalArgumentException("Damage per turn in DamageOverTimeEffect cannot be negative");
                if (proto.getDamageOverTimeEffect().getDuration() < 1) throw new IllegalArgumentException("Duration in DamageOverTimeEffect must be at least 1");
                yield DamageOverTimeEffect.builder()
                        .id(proto.getId())
                        .description(proto.getDescription())
                        .damagePerTurn(proto.getDamageOverTimeEffect().getDamagePerTurn())
                        .duration(proto.getDamageOverTimeEffect().getDuration())
                        .build();
            }
            case SKIP_TURN_EFFECT -> {
                if (proto.getSkipTurnEffect().getDuration() < 1) throw new IllegalArgumentException("Duration in SkipTurnEffect must be at least 1");
                yield SkipTurnEffect.builder()
                        .id(proto.getId())
                        .description(proto.getDescription())
                        .duration(proto.getSkipTurnEffect().getDuration())
                        .build();
            }
            case EFFECTDATA_NOT_SET -> throw new IllegalArgumentException("Unknown or unset effect type in EffectProto");
        };
    }

    public static EffectProto toProto(Effect model) {
        if (model == null) throw new IllegalArgumentException("Effect cannot be null");
        if (model.getId().isBlank()) throw new IllegalArgumentException("Effect ID cannot be blank");
        if (model.getDescription().isBlank()) throw new IllegalArgumentException("Effect description cannot be blank");

        EffectProto.Builder builder = EffectProto.newBuilder()
            .setId(model.getId())
            .setDescription(model.getDescription());

        switch (model) {
            case DamageEffect damageEffect -> {
                DamageEffectProto proto = DamageEffectProto.newBuilder()
                    .setDamage(damageEffect.getDamage())
                    .build();
                builder.setDamageEffect(proto);
            }
            case DamageOverTimeEffect dotEffect -> {
                DamageOverTimeEffectProto proto = DamageOverTimeEffectProto.newBuilder()
                    .setDamagePerTurn(dotEffect.getDamagePerTurn())
                    .setDuration(dotEffect.getDuration())
                    .build();
                builder.setDamageOverTimeEffect(proto);
            }
            case SkipTurnEffect skipTurnEffect -> {
                SkipTurnEffectProto proto = SkipTurnEffectProto.newBuilder()
                    .setDuration(skipTurnEffect.getDuration())
                    .build();
                builder.setSkipTurnEffect(proto);
            }
            default -> throw new IllegalArgumentException("Unknown effect type: " + model.getClass().getName());
        }
        return builder.build();
    }
}