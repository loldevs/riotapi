package net.boreeas.riotapi.rest;

/**
 * Created on 4/12/2014.
 */
public enum GameMode {
    CLASSIC("CLASSIC"),
    DOMINION("ODIN"),
    ARAM("ARAM"),
    TUTORIAL("TUTORIAL"),
    ONE_FOR_ALL("ONEFORALL"),
    FIRST_BLOOD("FIRSTBLOOD");

    public final String name;

    private GameMode(String name) {
        this.name = name;
    }

    public static GameMode getByName(String name) {
        for (GameMode mode: values()) {
            if (mode.name.equals(name)) {
                return mode;
            }
        }

        return null;
    }
}
