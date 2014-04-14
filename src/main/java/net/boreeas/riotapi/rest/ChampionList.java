package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.Collection;
import java.util.Map;

/**
 * Created on 4/14/2014.
 */
@Getter
public class ChampionList {
    private Map<String, Champion> data;
    private String format;
    private Map<String, String> keys;
    private String type;
    private String version;

    public Collection<Champion> getChampions() {
        return data.values();
    }
}
