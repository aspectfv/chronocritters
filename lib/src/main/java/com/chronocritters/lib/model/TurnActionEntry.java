package com.chronocritters.lib.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnActionEntry {
    private String playerId;
    private boolean playerHasTurn;

    @Default
    private int turn = 1;
    private String turnActionLog;
}
