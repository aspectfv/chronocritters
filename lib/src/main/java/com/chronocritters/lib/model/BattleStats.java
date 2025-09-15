package com.chronocritters.lib.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattleStats {
    @Default
    private int turnCount = 1;
    private long battleStartTime;
    private long duration;

    @Default
    private Map<String, Integer> playersDamageDealt = new HashMap<>(); // player ids as string

    @Default
    List<TurnActionEntry> turnActionHistory = new ArrayList<>();
}
