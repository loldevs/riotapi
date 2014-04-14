package net.boreeas.riotapi.rest;

/**
 * Created on 4/14/2014.
 */
public enum SpellData {
    ALL,
    COOLDOWN,
    COOLDOWN_BURN("cooldownBurn"),
    COST,
    COST_BURN("costBurn"),
    COST_TYPE("costType"),
    EFFECT,
    EFFECT_BURN("effectBurn"),
    IMAGE,
    KEY,
    LEVELTIP,
    MAXRANK,
    MODES,
    RANGE,
    RANGE_BURN("rangeBurn"),
    RESOURCE,
    SANITIZED_DESCRIPTION("sanitizedDescription"),
    SANITIZED_TOOLTIP("sanitizedTooltip"),
    TOOLTIP,
    VARS;

    public final String name;

    private SpellData() {
        this.name = name().toLowerCase();
    }

    private SpellData(String name) {
        this.name = name;
    }

    public static SpellData getByName(String name) {
        for (SpellData data: values()) {
            if (data.name.equals(name)) {
                return data;
            }
        }

        return null;
    }
}
