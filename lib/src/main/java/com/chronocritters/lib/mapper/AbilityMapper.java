package com.chronocritters.lib.mapper;

import com.chronocritters.lib.model.domain.Ability;
import com.chronocritters.proto.player.PlayerProto.AbilityProto;

import java.util.stream.Collectors;

public final class AbilityMapper {
    private AbilityMapper() {}

    public static Ability toModel(AbilityProto proto) {
        if (proto.getId().isBlank()) throw new IllegalArgumentException("Ability ID from AbilityProto cannot be blank");
        if (proto.getName().isBlank()) throw new IllegalArgumentException("Ability name from AbilityProto cannot be blank");
        if (proto.getEffectsList() == null || proto.getEffectsList().isEmpty()) throw new IllegalArgumentException("Effects list from AbilityProto cannot be null or empty");

        return Ability.builder()
            .id(proto.getId())
            .name(proto.getName())
            .description(proto.getDescription())
            .effects(proto.getEffectsList().stream()
                .map(EffectMapper::toModel)
                .collect(Collectors.toList()))
            .build();
    }

    public static AbilityProto toProto(Ability model) {
        if (model == null) throw new IllegalArgumentException("Ability cannot be null");
        if (model.getId().isBlank()) throw new IllegalArgumentException("Ability ID cannot be blank");
        if (model.getName().isBlank()) throw new IllegalArgumentException("Ability name cannot be blank");
        if (model.getEffects() == null || model.getEffects().isEmpty()) throw new IllegalArgumentException("Ability effects cannot be null or empty");

        AbilityProto.Builder builder = AbilityProto.newBuilder()
            .setId(model.getId())
            .setName(model.getName())
            .setDescription(model.getDescription());

        model.getEffects().stream()
            .map(EffectMapper::toProto)
            .forEach(builder::addEffects);

        return builder.build();
    }
}