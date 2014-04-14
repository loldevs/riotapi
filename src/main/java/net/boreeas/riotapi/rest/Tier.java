package net.boreeas.riotapi.rest;

/**
 * Created on 4/14/2014.
 */
public enum Tier {
    CHALLENGER,
    DIAMOND,
    PLATINUM,
    GOLD,
    SILVER,
    BRONZE;

    public static Tier getByName(String name) {
        return valueOf(name);
    }
}
