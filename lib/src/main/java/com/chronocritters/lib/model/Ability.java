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
@Document(collection = "abilities")
public class Ability {
    private String id;
    private String name;
    private int power;
    private AbilityType type;
    private List<Effect> effects;
}