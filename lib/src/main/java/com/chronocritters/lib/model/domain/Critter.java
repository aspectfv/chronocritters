package com.chronocritters.lib.model.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.chronocritters.lib.model.enums.CritterType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "Critter ID cannot be blank")
    private String id;

    @NotBlank(message = "Critter name cannot be blank")
    private String name;

    @NotBlank(message = "Critter description cannot be blank")
    private String description;

    @NotNull(message = "Critter type cannot be null")
    private CritterType type;

    @NotNull(message = "BaseStats cannot be null")
    private BaseStats baseStats;
    
    @NotNull(message = "Abilities list cannot be null")
    private List<Ability> abilities;
}
