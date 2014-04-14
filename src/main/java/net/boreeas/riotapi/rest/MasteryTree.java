package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
public class MasteryTree {
    private List<MasteryTreeList> Defense;
    private List<MasteryTreeList> Offense;
    private List<MasteryTreeList> Utility;
}
