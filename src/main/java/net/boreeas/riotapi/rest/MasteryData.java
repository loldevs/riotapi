package net.boreeas.riotapi.rest;

/**
 * Created on 4/14/2014.
 */
public enum MasteryData {
    ALL,
    IMAGE,
    PREREQ,
    RANKS,
    SANITIZED_DESCRIPTION("sanitizedDescription"),
    TREE;

    public final String name;

    private MasteryData() {
        this.name = name().toLowerCase();
    }

    private MasteryData(String name) {
        this.name = name;
    }

    public static MasteryData getByName() {
        for (MasteryData data: values()) {
            if (data.name.equals(data)) {
                return data;
            }
        }

        return null;
    }
}
