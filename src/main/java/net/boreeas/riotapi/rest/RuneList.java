package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.Map;

/**
 * Created on 4/14/2014.
 */
@Getter
public class RuneList {
    private Item basic;
    private Map<String, Item> data;
    private String type;
    private String version;
}
