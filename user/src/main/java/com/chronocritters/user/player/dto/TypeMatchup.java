package com.chronocritters.user.player.dto;

import com.chronocritters.lib.model.enums.CritterType;

/** One edge of the type chart: what {@code attacker} is strong against. */
public record TypeMatchup(CritterType attacker, CritterType defender, double multiplier) {}
