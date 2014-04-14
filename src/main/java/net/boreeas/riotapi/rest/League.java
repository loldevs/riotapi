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
public class League {
    private List<LeagueItem> entries;
    private String name;
    private String participantId;
    private String queue;
    private String tier;

    public Queue getQueue() {
        return Queue.getByName(queue);
    }

    public Tier getTier() {
        return Tier.getByName(tier);
    }
}
