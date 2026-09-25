package com.chronocritters.user.player.controller;

import java.util.List;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.user.player.dto.TypeMatchup;
import com.chronocritters.user.player.service.CritterCatalogService;

import lombok.RequiredArgsConstructor;

/**
 * The catalogue is reference data rather than player data: the same rows for
 * everyone, owned by nobody, so there is no identity to check it against.
 */
@Controller
@RequiredArgsConstructor
public class CritterController {
    private final CritterCatalogService critterCatalogService;

    @QueryMapping
    public List<Critter> critters() {
        return critterCatalogService.findAll();
    }

    @QueryMapping
    public List<TypeMatchup> typeAdvantages() {
        return critterCatalogService.typeAdvantages();
    }
}
