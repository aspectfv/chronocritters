package com.chronocritters.lib.model.battle;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerState {
    private String id;
    private String username;
    private Boolean hasTurn;
    private int activeCritterIndex;
    private List<CritterState> roster;
    private String lastSelectedAbilityId;

    public CritterState getActiveCritter() {
        return getCritterByIndex(activeCritterIndex);
    }

    public CritterState getCritterByIndex(int index) {
        if (roster == null || roster.isEmpty() || index < 0 || index >= roster.size()) {
            return null;
        }
        return roster.get(index);
    }
}
