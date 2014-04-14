package net.boreeas.riotapi.rest;

import lombok.Getter;

/**
 * Created on 4/14/2014.
 */
@Getter
public class TeamMemberInfo {
    private long inviteDate;
    private long joinDate;
    private long playerId;
    private String status;
}
