package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.Map;
import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
public class ItemList {
    private Item basic;
    private Map<String, Item> data;
    private List<ItemGroup> groups;
    private List<ItemTree> tree;
    private String type;
    private String version;
}
