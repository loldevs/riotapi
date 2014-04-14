package net.boreeas.riotapi.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    private int championId;
    private long createDate;
    private List<Player> fellowPlayers;
    private long gameId;
    private String gameMode;
    private String gameType;
    private boolean invalid;
    private int ipEarned;
    private int level;
    private int mapId;
    private int spell1;
    private int spell2;
    private Stats stats;
    private String subType;
    private int teamId;


    public GameMode getGameMode() {
        return GameMode.getByName(gameMode);
    }

    public GameType getGameType() {
        return GameType.getByName(gameType);
    }

    public Queue getSubType() {
        return Queue.getByName(subType);
    }

    public Team getTeam() {
        return Team.getById(teamId);
    }
}
