package net.boreeas.riotapi.rest;

import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * Created on 4/14/2014.
 */
@Getter
public class Item {
    private String colloq;
    private boolean consumeOnFull;
    private boolean consumed;
    private int depth;
    private String description;
    private List<String> from;
    private PurchaseData gold;
    private String group;
    private boolean hideFromAll;
    private int id;
    private Image image;
    private boolean inStore;
    private List<String> into;
    private Map<String, Boolean> maps;
    private String name;
    private String plaintext;
    private String requiredChampion;
    private ItemMetaData rune;
    private String sanitizedDescription;
    private int specialRecipe;
    private int stacks;
    private ItemStats stats;
    private List<String> tags;
}
