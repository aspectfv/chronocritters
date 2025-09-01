package com.chronocritters.gamelogic.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.chronocritters.lib.model.Ability;
import com.chronocritters.lib.model.AbilityType;
import com.chronocritters.lib.model.BaseStats;
import com.chronocritters.lib.model.Critter;
import com.chronocritters.lib.model.CritterState;
import com.chronocritters.lib.model.CritterType;
import com.chronocritters.lib.model.Player;
import com.chronocritters.lib.model.PlayerState;
import com.chronocritters.proto.player.PlayerProto.AbilityProto;
import com.chronocritters.proto.player.PlayerProto.AbilityTypeProto;
import com.chronocritters.proto.player.PlayerProto.BaseStatsProto;
import com.chronocritters.proto.player.PlayerProto.CritterProto;
import com.chronocritters.proto.player.PlayerProto.CritterTypeProto;
import com.chronocritters.proto.player.PlayerProto.PlayerResponse;

@Component
public class PlayerConverter {
    
    public PlayerState convertToPlayerState(PlayerResponse playerResponse) {
        // Convert roster from proto to Critter models
        List<Critter> critterRoster = playerResponse.getRosterList().stream()
            .map(this::convertCritter)
            .collect(Collectors.toList());
        
        // Convert Critter models to CritterState for battle
        List<CritterState> battleRoster = critterRoster.stream()
            .map(this::convertCritterToState)
            .collect(Collectors.toList());
        
        // Build Player from proto response (for reference)
        Player player = Player.builder()
            .id(playerResponse.getId())
            .username(playerResponse.getUsername())
            .roster(critterRoster)
            .build();
        
        // Build PlayerState for battle
        return PlayerState.builder()
            .id(player.getId())
            .username(player.getUsername())
            .hasTurn(false) // Initially false, will be set by battle logic
            .activeCritterId(battleRoster.isEmpty() ? null : battleRoster.get(0).getId())
            .roster(battleRoster)
            .build();
    }
    
    private Critter convertCritter(CritterProto critterProto) {
        return Critter.builder()
            .id(critterProto.getId())
            .name(critterProto.getName())
            .type(convertCritterType(critterProto.getType()))
            .baseStats(convertBaseStats(critterProto.getBaseStats()))
            .abilities(critterProto.getAbilitiesList().stream()
                .map(this::convertAbility)
                .collect(Collectors.toList()))
            .build();
    }
    
    private CritterState convertCritterToState(Critter critter) {
        // Convert from persistent Critter to battle CritterState
        int maxHp = critter.getBaseStats().getHealth();
        
        return CritterState.builder()
            .id(critter.getId()) // CritterState uses String ID
            .name(critter.getName())
            .type(critter.getType())
            .maxHp(maxHp)
            .currentHp(maxHp) // Start with full health
            .abilities(critter.getAbilities())
            .build();
    }
    
    private BaseStats convertBaseStats(BaseStatsProto baseStatsProto) {
        return BaseStats.builder()
            .health(baseStatsProto.getHealth())
            .attack(baseStatsProto.getAttack())
            .defense(baseStatsProto.getDefense())
            .build();
    }
    
    private Ability convertAbility(AbilityProto abilityProto) {
        return Ability.builder()
            .id(abilityProto.getId())
            .name(abilityProto.getName())
            .type(convertAbilityType(abilityProto.getType()))
            .power(abilityProto.getPower())
            .build();
    }
    
    private CritterType convertCritterType(CritterTypeProto protoType) {
        return switch (protoType) {
            case FIRE -> CritterType.FIRE;
            case WATER -> CritterType.WATER;
            case GRASS -> CritterType.GRASS;
            case ELECTRIC -> CritterType.ELECTRIC;
            case CRITTER_TYPE_UNSPECIFIED, UNRECOGNIZED -> 
                throw new IllegalArgumentException("Unknown critter type: " + protoType);
        };
    }
    
    private AbilityType convertAbilityType(AbilityTypeProto protoType) {
        return switch (protoType) {
            case ATTACK -> AbilityType.ATTACK;
            case DEFENSE -> AbilityType.DEFENSE;
            case SUPPORT -> AbilityType.SUPPORT;
            case ABILITY_TYPE_UNSPECIFIED, UNRECOGNIZED -> 
                throw new IllegalArgumentException("Unknown ability type: " + protoType);
        };
    }
}