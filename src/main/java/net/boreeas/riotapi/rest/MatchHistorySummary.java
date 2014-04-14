package net.boreeas.riotapi.rest;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Created on 4/14/2014.
 */
@Getter
public class MatchHistorySummary {
    private int death;
    private int assists;
    private int kills;
    private long date;
    private long gameId;
    private String gameMode;
    private boolean invalid;
    @Getter(AccessLevel.NONE) private int mapId;
    private int opposingTeamKills;
    private String opposingTeamName;
    private boolean win;

    private GameMode getGameMode() {
        return GameMode.getByName(gameMode);
    }

    private Map getMap() {
        return Map.getById(mapId);
    }
}
