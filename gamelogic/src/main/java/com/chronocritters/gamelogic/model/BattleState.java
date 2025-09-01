package com.chronocritters.gamelogic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattleState {
    private String battleId;
    private String activePlayerId;
    private String playerOne;
    private String playerTwo;
    private String lastActionLog;
}
