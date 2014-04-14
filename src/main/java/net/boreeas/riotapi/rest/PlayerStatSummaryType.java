package net.boreeas.riotapi.rest;

/**
 * Created on 4/12/2014.
 */
public enum PlayerStatSummaryType {
    NORMAL("Unranked"),
    NORMAL_3v3("Unranked3x3"),
    DOMINION("OdinUnranked"),
    ARAM("AramUnranked5x5"),
    BOTS("CoopVsAI"),
    BOTS_3v3("CoopVsAI3x3"),
    RANKED_SOLO_5v5("RankedSolo5x5"),
    RANKED_TEAM_3v3("RankedTeam3x3"),
    RANKED_TEAM_5v5("RankedTeam5x5"),
    ONE_FOR_ALL("OneForAll5x5"),
    FIRSTBLOOD_1v1("FirstBlood1x1"),
    FIRSTBLOOD_2v2("FirstBlood2x2"),
    HEXAKILL("SummonersRift6x6"),
    TEAMBUILDER("CAP5x5"),
    URF("URF"),
    URF_BOTS("URFBots");

    public final String name;

    private PlayerStatSummaryType(String name) {
        this.name = name;
    }

    public static PlayerStatSummaryType getByName(String name) {
        for (PlayerStatSummaryType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }

        return null;
    }
}