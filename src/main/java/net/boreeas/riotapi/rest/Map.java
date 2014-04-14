package net.boreeas.riotapi.rest;

/**
 * Created on 4/12/2014.
 */
public enum Map {
    SR_SUMMER(1, "Summoner's Rift"),
    SR_AUTUMN(2, "Summoner's Rift"),
    PROVING_GROUNDS(3, "The Proving Grounds"),
    TWISTED_TREELINE_ORIG(4, "Twisted Treeline (Original)"),
    CRYSTAL_SCAR(8, "The Crystal Scar"),
    TWISTED_TREELINE_CURR(10, "Twisted Treeline (Current)"),
    HOWLING_ABYSS(12, "Howling Abyss");


    public final int id;
    public final String name;

    private Map(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Map getById(int id) {
        switch (id) {
            case 1: return SR_SUMMER;
            case 2: return SR_AUTUMN;
            case 3: return PROVING_GROUNDS;
            case 4: return TWISTED_TREELINE_ORIG;
            case 8: return CRYSTAL_SCAR;
            case 10: return TWISTED_TREELINE_CURR;
            case 12: return HOWLING_ABYSS;
            default: return null;
        }
    }
}
