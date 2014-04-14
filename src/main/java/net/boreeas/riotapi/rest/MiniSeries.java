package net.boreeas.riotapi.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created on 4/14/2014.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MiniSeries {
    private int losses;
    private String progress;
    private int target;
    private long timeLeftToPlayMillis;
    private int wins;
}
