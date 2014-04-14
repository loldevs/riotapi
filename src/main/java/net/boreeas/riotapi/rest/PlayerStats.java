package net.boreeas.riotapi.rest;

import lombok.Getter;

/**
 * Created on 4/14/2014.
 */
@Getter
public class PlayerStats {
    private AggregatedStats aggregatedStats;
    private int losses;
    private long modifyDate;
    private String playerStatSummaryType;
    private int wins;

    public PlayerStatSummaryType getPlayerStatSummaryType() {
        return PlayerStatSummaryType.getByName(playerStatSummaryType);
    }
}
