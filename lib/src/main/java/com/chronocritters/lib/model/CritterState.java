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
public class CritterState {
    private int id;
    private String name;
    private CritterType type;
    private int maxHp;
    private int currentHp;
    private List<Ability> abilities;
}
