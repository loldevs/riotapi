package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
public class RankedStats {
    private List<PlayerChampionStats> champions;
    private long modifyDate;
    private long summonerId;
}
