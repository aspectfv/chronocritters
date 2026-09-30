package com.chronocritters.lib.model.enums;

/** The conditions that take a critter's one status slot. */
public enum StatusCondition {
    BURN("burned"),
    POISON("poisoned"),
    PARALYSIS("paralyzed"),
    SLEEP("asleep");

    private final String adjective;

    StatusCondition(String adjective) {
        this.adjective = adjective;
    }

    /** How the log describes a critter holding it: "is burned", "is asleep". */
    public String adjective() {
        return adjective;
    }
}
