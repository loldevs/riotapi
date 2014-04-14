package net.boreeas.riotapi.rest;

/**
 * Created on 4/12/2014.
 */
public enum GameType {
    CUSTOM("CUSTOM_GAME"),
    TUTORIAL("TUTORIAL_GAME"),
    MATCHED("MATCHED_GAME");

    public final String name;

    private GameType(String name) {
        this.name = name;
    }

    public static GameType getByName(String name) {
        for (GameType type: values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }

        return null;
    }
}
