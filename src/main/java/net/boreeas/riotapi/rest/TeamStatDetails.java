package net.boreeas.riotapi.rest;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Created on 4/14/2014.
 */
@Getter
public class TeamStatDetails {
    private int averageGamesPlayed;
    private String fullId;
    private int losses;
    @Getter(AccessLevel.NONE) private String teamStatType;
    private int wins;

    private Queue getQueue() {
        return Queue.getByName(teamStatType);
    }
}
