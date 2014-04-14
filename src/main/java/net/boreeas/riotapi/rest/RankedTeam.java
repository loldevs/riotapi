package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
public class RankedTeam {
    private long createDate;
    private String fullId;
    private long lastGameDate;
    private long lastJoinDate;
    private long lastJoinedRankedTeamQueueDate;
    private List<MatchHistorySummary> matchHistory;
    private MessageOfDay messageOfDay;
    private long modifyDate;
    private String name;
    private Roster roster;
    private long secondLastJoinDate;
    private String status;
    private String tag;
    private TeamStatSummary teamStatSummary;
    private long thirdLastJoinDate;
}
