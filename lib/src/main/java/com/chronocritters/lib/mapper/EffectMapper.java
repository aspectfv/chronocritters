package com.chronocritters.lib.mapper;

import com.chronocritters.lib.model.domain.Effect;
import com.chronocritters.lib.model.effects.CleanseEffect;
import com.chronocritters.lib.model.effects.DamageEffect;
import com.chronocritters.lib.model.effects.DamageOverTimeEffect;
import com.chronocritters.lib.model.effects.HealEffect;
import com.chronocritters.lib.model.effects.LifestealEffect;
import com.chronocritters.lib.model.effects.ShieldEffect;
import com.chronocritters.lib.model.effects.SkipTurnEffect;
import com.chronocritters.lib.model.effects.StatModifierEffect;
import com.chronocritters.lib.model.enums.Stat;
import com.chronocritters.proto.player.PlayerProto.CleanseEffectProto;
import com.chronocritters.proto.player.PlayerProto.DamageEffectProto;
import com.chronocritters.proto.player.PlayerProto.DamageOverTimeEffectProto;
import com.chronocritters.proto.player.PlayerProto.EffectProto;
import com.chronocritters.proto.player.PlayerProto.HealEffectProto;
import com.chronocritters.proto.player.PlayerProto.LifestealEffectProto;
import com.chronocritters.proto.player.PlayerProto.ShieldEffectProto;
import com.chronocritters.proto.player.PlayerProto.SkipTurnEffectProto;
import com.chronocritters.proto.player.PlayerProto.StatModifierEffectProto;
import com.chronocritters.proto.player.PlayerProto.StatProto;

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
            case STAT_MODIFIER_EFFECT -> {
                if (proto.getStatModifierEffect().getStages() == 0) throw new IllegalArgumentException("Stages in StatModifierEffect cannot be zero");
                yield StatModifierEffect.builder()
                        .id(proto.getId())
                        .description(proto.getDescription())
                        .stat(toModel(proto.getStatModifierEffect().getStat()))
                        .stages(proto.getStatModifierEffect().getStages())
                        .build();
            }
            case HEAL_EFFECT -> {
                int healPercent = proto.getHealEffect().getHealPercent();
                if (healPercent < 1 || healPercent > 100) throw new IllegalArgumentException("Heal percent in HealEffect must be between 1 and 100");
                yield HealEffect.builder()
                        .id(proto.getId())
                        .description(proto.getDescription())
                        .healPercent(healPercent)
                        .build();
            }
            case LIFESTEAL_EFFECT -> {
                int drainPercent = proto.getLifestealEffect().getDrainPercent();
                if (proto.getLifestealEffect().getDamage() < 0) throw new IllegalArgumentException("Damage in LifestealEffect cannot be negative");
                if (drainPercent < 1 || drainPercent > 100) throw new IllegalArgumentException("Drain percent in LifestealEffect must be between 1 and 100");
                yield LifestealEffect.builder()
                        .id(proto.getId())
                        .description(proto.getDescription())
                        .damage(proto.getLifestealEffect().getDamage())
                        .drainPercent(drainPercent)
                        .build();
            }
            case SHIELD_EFFECT -> {
                if (proto.getShieldEffect().getAmount() < 1) throw new IllegalArgumentException("Amount in ShieldEffect must be at least 1");
                if (proto.getShieldEffect().getDuration() < 1) throw new IllegalArgumentException("Duration in ShieldEffect must be at least 1");
                yield ShieldEffect.builder()
                        .id(proto.getId())
                        .description(proto.getDescription())
                        .amount(proto.getShieldEffect().getAmount())
                        .duration(proto.getShieldEffect().getDuration())
                        .build();
            }
            case CLEANSE_EFFECT -> CleanseEffect.builder()
                    .id(proto.getId())
                    .description(proto.getDescription())
                    .build();
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
            case LifestealEffect lifestealEffect -> {
                LifestealEffectProto proto = LifestealEffectProto.newBuilder()
                    .setDamage(lifestealEffect.getDamage())
                    .setDrainPercent(lifestealEffect.getDrainPercent())
                    .build();
                builder.setLifestealEffect(proto);
            }
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
            case StatModifierEffect statModifierEffect -> {
                StatModifierEffectProto proto = StatModifierEffectProto.newBuilder()
                    .setStat(toProto(statModifierEffect.getStat()))
                    .setStages(statModifierEffect.getStages())
                    .build();
                builder.setStatModifierEffect(proto);
            }
            case HealEffect healEffect -> {
                HealEffectProto proto = HealEffectProto.newBuilder()
                    .setHealPercent(healEffect.getHealPercent())
                    .build();
                builder.setHealEffect(proto);
            }
            case ShieldEffect shieldEffect -> {
                ShieldEffectProto proto = ShieldEffectProto.newBuilder()
                    .setAmount(shieldEffect.getAmount())
                    .setDuration(shieldEffect.getDuration())
                    .build();
                builder.setShieldEffect(proto);
            }
            case CleanseEffect cleanseEffect -> builder.setCleanseEffect(CleanseEffectProto.getDefaultInstance());
            default -> throw new IllegalArgumentException("Unknown effect type: " + model.getClass().getName());
        }
        return builder.build();
    }

    private static Stat toModel(StatProto proto) {
        return switch (proto) {
            case ATTACK -> Stat.ATTACK;
            case DEFENSE -> Stat.DEFENSE;
            case SPEED -> Stat.SPEED;
            default -> throw new IllegalArgumentException("Unknown or unset stat in StatModifierEffectProto");
        };
    }

    private static StatProto toProto(Stat model) {
        return switch (model) {
            case ATTACK -> StatProto.ATTACK;
            case DEFENSE -> StatProto.DEFENSE;
            case SPEED -> StatProto.SPEED;
        };
    }
}
