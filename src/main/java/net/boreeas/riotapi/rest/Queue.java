package net.boreeas.riotapi.rest;

/**
 * Created on 4/12/2014.
 */
public enum Queue {
    NONE("NONE"),
    NORMAL_5v5("NORMAL"),
    NORMAL_3v3("NORMAL_3x3"),
    DOMINION("ODIN_UNRANKED"),
    ARAM("ARAM_UNRANKED_5x5"),
    BOT("BOT"),
    BOT_3v3("BOT_3x3"),
    RANKED_SOLO_5v5("RANKED_SOLO_5x5"),
    RANKED_TEAM_3v3("RANKED_TEAM_3x3"),
    RANKED_TEAM_5v5("RANKED_TEAM_5x5"),
    ONE_FOR_ALL("ONEFORALL_5x5"),
    FIRSTBLOOD_1v1("FIRSTBLOOD_1x1"),
    FIRSTBLOOD_2v2("FIRSTBLOOD_2x2"),
    HEXAKILL("SR_6x6"),
    TEAMBUILDER("CAP_5x5"),
    URF("URF"),
    URF_BOT("URF_BOT");


    public final String name;

    private Queue(String name) {
        this.name = name;
    }

    public static Queue getByName(String name) {
        for (Queue type: values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }

        return null;
    }
}
