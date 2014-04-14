package net.boreeas.riotapi.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created on 4/14/2014.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeagueItem {
    private boolean isFreshBlood;
    private boolean isHotStreak;
    private boolean isInactive;
    private boolean isVeteran;
    private long lastPlayed;
    private String leagueName;
    private int leaguePoints;
    private MiniSeries miniSeries;
    private String playerOrTeamId;
    private String playerOrTeamName;
    private String queueType;
    private String rank;
    private String tier;
    private int wins;

    public int getRank() {
        switch (rank) {
            case "I": return 1;
            case "II": return 2;
            case "III": return 3;
            case "IV": return 4;
            case "V": return 5;
            default: throw new IllegalStateException("Invalid rank: " + rank);
        }
    }

    public Tier getTier() {
        return Tier.getByName(tier);
    }

    public Queue getQueueType() {
        return Queue.getByName(queueType);
    }
}
