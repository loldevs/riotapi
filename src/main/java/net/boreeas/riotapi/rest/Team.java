package net.boreeas.riotapi.rest;

/**
 * Created on 4/14/2014.
 */
public enum Team {
    BLUE(100),
    PURPLE(200);

    public final int id;

    private Team(int id) {
        this.id = id;
    }

    public static Team getById(int id) {
        switch (id) {
            case 100: return BLUE;
            case 200: return PURPLE;
            default: return null;
        }
    }
}
