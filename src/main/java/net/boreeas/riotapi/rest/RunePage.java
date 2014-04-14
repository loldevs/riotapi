package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.Set;

/**
 * Created on 4/14/2014.
 */
@Getter
public class RunePage {
    private boolean current;
    private long id;
    private String name;
    private Set<RuneSlot> slots;
}
