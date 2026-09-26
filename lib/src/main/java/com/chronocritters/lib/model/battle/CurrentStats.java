package com.chronocritters.lib.model.battle;

import java.util.EnumMap;
import java.util.Map;

import com.chronocritters.lib.model.enums.Stat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A critter's numbers for the length of one battle. Attack, defense and speed
 * are held at their level-scaled base and read through a stage, so a buff can
 * be undone by moving the stage back rather than by remembering what it was.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"currentAtk", "currentDef", "currentSpd"}, allowGetters = true)
public class CurrentStats {
    /** How far a stat can be pushed either way. */
    public static final int MAX_STAGE = 6;

    private int maxHp;
    private int currentHp;
    private int attack;
    private int defense;
    private int speed;

    /** Stage shifts by stat, from -6 to +6. A stat at stage zero is absent rather than held at zero. */
    @Default
    private Map<Stat, Integer> stages = new EnumMap<>(Stat.class);

    public int getCurrentAtk() {
        return atStage(attack, Stat.ATTACK);
    }

    public int getCurrentDef() {
        return atStage(defense, Stat.DEFENSE);
    }

    public int getCurrentSpd() {
        return atStage(speed, Stat.SPEED);
    }

    public int getStage(Stat stat) {
        return stages.getOrDefault(stat, 0);
    }

    /** Pushes a stat by the given stages, stopping at the cap. Returns how far it moved. */
    public int shiftStage(Stat stat, int by) {
        int before = getStage(stat);
        int after = Math.max(-MAX_STAGE, Math.min(MAX_STAGE, before + by));
        if (after == 0) {
            stages.remove(stat);
        } else {
            stages.put(stat, after);
        }
        return after - before;
    }

    /**
     * The stage table is Pokemon's: each stage up adds half the base, each
     * stage down adds half the base to the divisor. Six stages is 4x either way.
     */
    private int atStage(int base, Stat stat) {
        int stage = getStage(stat);
        double multiplier = stage >= 0 ? (2.0 + stage) / 2.0 : 2.0 / (2.0 - stage);
        return Math.max(1, (int) Math.round(base * multiplier));
    }
}
