package com.chronocritters.lib.model;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "matchHistoryEntries")
public class MatchHistoryEntry {
    private String battleId;
    private String winnerId;
    private String loserId;
    private String opponentUsername; 
    private Instant timestamp;
    private List<String> crittersUsed; 
    private BattleStats battleStats;
}