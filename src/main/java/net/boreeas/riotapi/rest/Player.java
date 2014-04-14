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
public class Player {
    private int championId;
    private int summonerId;
    private int teamId;

    public Team getTeam() {
        return Team.getById(teamId);
    }
}
