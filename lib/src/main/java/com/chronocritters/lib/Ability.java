package com.chronocritters.lib;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "abilities")
public class Ability {
    private String id;
    private String name;
    private int power;
    private AbilityType type;
}