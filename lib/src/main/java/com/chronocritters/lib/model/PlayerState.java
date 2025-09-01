package com.chronocritters.lib.model;

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
    private String activeCritterId;
    private List<CritterState> roster;
    
}
