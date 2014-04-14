package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.Collection;
import java.util.Map;

/**
 * Created on 4/14/2014.
 */
@Getter
public class SummonerSpellList {
    private Map<String, SummonerSpell> data;
    private String type;
    private String version;

    public Collection<SummonerSpell> getSpells() {
        return data.values();
    }
}
