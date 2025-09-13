package com.chronocritters.lib.util;

import com.chronocritters.lib.model.Critter;
import com.chronocritters.lib.model.Player;

public final class ExperienceUtil {
    private static final double EXP_GROWTH_FACTOR = 1.5;

    private static final long PLAYER_BASE_EXP_REQ = 100;
    private static final long PLAYER_BASE_WIN_EXP_GAIN = 50;
    private static final long PLAYER_MIN_EXP_GAIN = 10;

    private static final long PLAYER_BASE_LOSS_EXP_GAIN = 15;
    private static final long PLAYER_MIN_LOSS_EXP_GAIN = 5;

    private static final long CRITTER_BASE_EXP_REQ = PLAYER_BASE_EXP_REQ * 2;
    private static final long CRITTER_BASE_WIN_EXP_GAIN = PLAYER_BASE_WIN_EXP_GAIN * 2;
    private static final long CRITTER_MIN_EXP_GAIN = 20;

    private static final long CRITTER_BASE_LOSS_EXP_GAIN = Math.round(CRITTER_BASE_WIN_EXP_GAIN * 0.25); // 25% of the win amount
    private static final long CRITTER_MIN_LOSS_EXP_GAIN = 5;

    private ExperienceUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static long getRequiredExpForPlayerLevel(int level) {
        return calculateRequiredExp(level, PLAYER_BASE_EXP_REQ, EXP_GROWTH_FACTOR);
    }


    public static long getRequiredExpForCritterLevel(int level) {
        return calculateRequiredExp(level, CRITTER_BASE_EXP_REQ, EXP_GROWTH_FACTOR);
    }


    public static long calculatePlayerXpForWin(Player winner, Player loser) {
        int levelDifference = loser.getLevel() - winner.getLevel();
        long levelBonus = Math.max(0, levelDifference * 10);
        long totalXp = PLAYER_BASE_WIN_EXP_GAIN + levelBonus;
        return Math.max(PLAYER_MIN_EXP_GAIN, totalXp);
    }

    public static long calculateCritterXpForWin(Critter winningCritter, Player loser) {
        double averageLoserCritterLevel = loser.getRoster().stream()
            .mapToInt(Critter::getLevel)
            .average()
            .orElse(1.0);
            
        int levelDifference = (int) Math.round(averageLoserCritterLevel - winningCritter.getLevel());
        long levelBonus = Math.max(0, levelDifference * 5);
        long totalXp = CRITTER_BASE_WIN_EXP_GAIN + levelBonus;

        return Math.max(CRITTER_MIN_EXP_GAIN, totalXp);
    }

    public static long calculatePlayerXpForLoss(Player loser) {
        long totalXp = PLAYER_BASE_LOSS_EXP_GAIN;
        return Math.max(PLAYER_MIN_LOSS_EXP_GAIN, totalXp);
    }

    public static long calculateCritterXpForLoss(Critter losingCritter) {
        long totalXp = CRITTER_BASE_LOSS_EXP_GAIN;
        return Math.max(CRITTER_MIN_LOSS_EXP_GAIN, totalXp);
    }

    private static long calculateRequiredExp(int level, long baseExp, double growthFactor) {
        if (level <= 1) {
            return 0;
        }
        return Math.round(baseExp * Math.pow(growthFactor, level - 2));
    }
}