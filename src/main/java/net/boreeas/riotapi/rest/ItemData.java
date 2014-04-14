package net.boreeas.riotapi.rest;

/**
 * Created on 4/14/2014.
 */
public enum ItemData {
    ALL,
    COLLOQUIAL("colloq"),
    CONSUME_ON_FULL("consumeOnFull"),
    CONSUMED,
    DEPTH,
    FROM,
    GOLD,
    GROUPS,
    HIDE_FROM_ALL("hideFromAll"),
    IMAGE,
    IN_STORE("inStore"),
    INTO,
    MAPS,
    REQUIRED_CHAMPIONS("requiredChampions"),
    SANITIZED_DESCRIPTION("sanitizedDescription"),
    SPECIAL_RECIPE("specialRecipe"),
    STACKS,
    STATS,
    TAGS,
    TREE;

    public final String name;

    private ItemData() {
        this.name = name().toLowerCase();
    }

    private ItemData(String name) {
        this.name = name;
    }

    public static ItemData getByName(String name) {
        for (ItemData data: values()) {
            if (data.name.equals(name)) {
                return data;
            }
        }

        return null;
    }
}
