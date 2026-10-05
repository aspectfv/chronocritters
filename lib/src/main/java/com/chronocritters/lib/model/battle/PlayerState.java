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

    /** Reset whenever this player acts; see BattleService for the forfeit threshold. */
    private int consecutiveTimeouts;

    /** A rewind is once per battle. Sent to both clients, since the opponent's charge is worth knowing too. */
    private boolean rewindUsed;

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
