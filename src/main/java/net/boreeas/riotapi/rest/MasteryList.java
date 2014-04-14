package net.boreeas.riotapi.rest;

import lombok.Getter;
import java.util.Map;

/**
 * Created on 4/14/2014.
 */
@Getter
public class MasteryList {
    private Map<String, Mastery> data;
    private MasteryTree tree;
    private String type;
    private String version;
}
