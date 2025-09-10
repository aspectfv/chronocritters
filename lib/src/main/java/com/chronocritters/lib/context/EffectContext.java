package com.chronocritters.lib.context;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
@Data
@Builder
public class EffectContext {
    private Map<EffectContextType, Object> data;
}
