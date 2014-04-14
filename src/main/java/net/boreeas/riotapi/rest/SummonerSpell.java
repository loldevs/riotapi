package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
public class SummonerSpell extends Spell {
    private int id;
    private List<String> modes;
    private int summonerLevel;
}
