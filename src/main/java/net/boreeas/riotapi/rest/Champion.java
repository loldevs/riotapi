package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
public class Champion {
    private List<String> allytips;
    private String blurb;
    private List<String> enemytips;
    private int id;
    private Image image;
    private ChampionInfo info;
    private String key;
    private String lore;
    private String partype;
    private ChampionPassive passive;
    private List<RecommendedItems> recommended;
    private List<Skin> skins;
    private List<Spell> spells;
    private ChampionStats stats;
    private List<String> tags;
    private String title;
}
