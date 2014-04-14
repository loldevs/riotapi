package net.boreeas.riotapi.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created on 4/12/2014.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BasicChampData {
    private boolean active;
    private boolean botEnabledCustom;
    private boolean botEnabledQueue;
    private boolean freeToPlay;
    private long id;
    private boolean rankedEnabled;
}
