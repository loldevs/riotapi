package net.boreeas.riotapi;

/**
 * Created on 4/14/2014.
 */
public class RiotUtil {
    public static String standardizeSummonerName(String name) {
        return name.toLowerCase().replace(" ", "");
    }
}
