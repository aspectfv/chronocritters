package com.chronocritters.lib.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.chronocritters.lib.model.domain.BaseStats;
import com.chronocritters.lib.model.domain.Critter;
import com.chronocritters.lib.model.domain.Player;
import com.chronocritters.lib.model.domain.PlayerStats;

class ExperienceUtilTest {

    private Player playerAtLevel(int level, int... critterLevels) {
        List<Critter> roster = java.util.Arrays.stream(critterLevels)
                .mapToObj(critterLevel -> Critter.builder()
                        .id("c" + critterLevel)
                        .baseStats(BaseStats.builder().health(5).attack(3).defense(3).level(critterLevel).build())
                        .build())
                .toList();

        return Player.builder()
                .id("p")
                .stats(PlayerStats.builder().level(level).build())
                .roster(roster)
                .build();
    }

    @Test
    @DisplayName("level 1 requires no experience, and the requirement grows with each level")
    void requiredExperienceGrowsWithLevel() {
        assertThat(ExperienceUtil.getRequiredExpForPlayerLevel(1)).isZero();
        assertThat(ExperienceUtil.getRequiredExpForPlayerLevel(2)).isEqualTo(100);
        assertThat(ExperienceUtil.getRequiredExpForPlayerLevel(3)).isEqualTo(150);
        assertThat(ExperienceUtil.getRequiredExpForPlayerLevel(4)).isEqualTo(225);
    }

    @Test
    @DisplayName("critters level more slowly than their trainer")
    void critterCurveIsSlowerThanPlayerCurve() {
        assertThat(ExperienceUtil.getRequiredExpForCritterLevel(2))
                .isEqualTo(2 * ExperienceUtil.getRequiredExpForPlayerLevel(2));
    }

    @Test
    @DisplayName("an evenly matched win awards the base amount")
    void evenMatchAwardsBaseExperience() {
        long experience = ExperienceUtil.calculatePlayerXpForWin(playerAtLevel(5), playerAtLevel(5));

        assertThat(experience).isEqualTo(50);
    }

    @Test
    @DisplayName("beating a higher level opponent awards a bonus")
    void beatingAHigherLevelOpponentAwardsBonus() {
        long experience = ExperienceUtil.calculatePlayerXpForWin(playerAtLevel(1), playerAtLevel(4));

        assertThat(experience).isEqualTo(50 + (3 * 10));
    }

    @Test
    @DisplayName("beating a lower level opponent awards no bonus rather than a penalty")
    void beatingALowerLevelOpponentAwardsNoBonus() {
        long experience = ExperienceUtil.calculatePlayerXpForWin(playerAtLevel(10), playerAtLevel(1));

        assertThat(experience).isEqualTo(50);
    }

    @Test
    @DisplayName("losing still awards experience so a losing streak keeps progressing")
    void losingStillAwardsExperience() {
        assertThat(ExperienceUtil.calculatePlayerXpForLoss(playerAtLevel(3))).isEqualTo(15);
        assertThat(ExperienceUtil.calculateCritterXpForLoss(
                Critter.builder().baseStats(BaseStats.builder().level(3).build()).build())).isEqualTo(25);
    }

    @Test
    @DisplayName("critter win experience scales against the average level of the beaten roster")
    void critterWinExperienceUsesAverageOpponentLevel() {
        Critter winner = Critter.builder()
                .id("winner")
                .baseStats(BaseStats.builder().health(5).attack(3).defense(3).level(2).build())
                .build();

        // Average opponent level 6, winner at 2 -> bonus of 4 * 5
        long experience = ExperienceUtil.calculateCritterXpForWin(winner, playerAtLevel(1, 4, 6, 8));

        assertThat(experience).isEqualTo(100 + (4 * 5));
    }
}
