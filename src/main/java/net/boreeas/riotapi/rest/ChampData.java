package net.boreeas.riotapi.rest;

/**
 * Created on 4/14/2014.
 */
public enum ChampData {
    ALL,
    ALLY_TIPS("allytips"),
    BLURB,
    ENEMY_TIPS("enemytips"),
    IMAGE,
    INFO,
    LORE,
    PARTYPE,
    PASSIVE,
    RECOMMENDED,
    SKINS,
    SPELLS,
    STATS,
    TAGS;

    public final String name;

    private ChampData() {
        this.name = toString().toLowerCase();
    }

    private ChampData(String name) {
        this.name = name;
    }

    public static ChampData getByName(String name) {
        for (ChampData data: values()) {
            if (data.name.equals(name)) {
                return data;
            }
        }

        return null;
    }
}
