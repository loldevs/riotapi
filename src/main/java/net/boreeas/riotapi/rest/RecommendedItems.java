package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
public class RecommendedItems {
    private List<RecommendedItemBlock> blocks;
    private String champion;
    private String map;
    private String mode;
    private boolean priority;
    private String title;
    private String type;
}
