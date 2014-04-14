package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
public class MasteryPage {
    private boolean current;
    private long id;
    private List<Mastery> masteries;
    private String name;
}
