package com.chronocritters.lib.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BattleRewards {
    @Default
    private Map<String, Long> playersExp = new HashMap<>();
    @Default
    private Map<String, Long> crittersExp = new HashMap<>();
}
