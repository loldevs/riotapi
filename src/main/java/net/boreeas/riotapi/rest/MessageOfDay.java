package net.boreeas.riotapi.rest;

import lombok.Getter;

/**
 * Created on 4/14/2014.
 */
@Getter
public class MessageOfDay {
    private long createDate;
    private String message;
    private int version;
}
