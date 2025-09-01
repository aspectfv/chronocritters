package com.chronocritters.lib.model;

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
@Document(collection = "critters")
public class Critter {
    private String id;
    private String name;
    private CritterType type;
    private BaseStats baseStats;
    private List<Ability> abilities;
}
