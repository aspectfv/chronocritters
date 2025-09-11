package com.chronocritters.lib.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.chronocritters.lib.context.EffectContext;
import com.chronocritters.lib.factory.EffectContextFactory;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "abilities")
public class Ability {
    private String id;
    private String name;
    private List<Effect> effects;

    public void execute(BattleState battleState) {
        for (Effect effect : effects) {
            EffectContext context = EffectContextFactory.createContext(effect.getType(), battleState);
            effect.apply(context);
        }
    }
}