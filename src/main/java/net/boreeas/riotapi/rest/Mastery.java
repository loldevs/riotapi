package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.List;

/**
 * Created on 4/14/2014.
 */
@Getter
public class Mastery {
    private List<String> description;
    private int id;
    private Image image;
    private String name;
    private String prereq;
    private int ranks;
    private List<String> sanitizedDescription;
}
