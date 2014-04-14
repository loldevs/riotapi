package net.boreeas.riotapi.rest;

import lombok.Getter;
import java.util.Map;

/**
 * Created on 4/14/2014.
 */
public class Realm {
    @Getter private String cdn;
    private String css;
    private String dd;
    private String l;
    private String lg;
    private Map<String, String> n;
    @Getter private int profileiconmax;
    @Getter private String store;
    private String v;

    public String getCssVersion() {
        return css;
    }

    public String getDragonVersion() {
        return dd;
    }

    public String getDefaultLanguage() {
        return l;
    }

    public String getLegacyScriptMode() {
        return lg;
    }

    public Map<String, String> getVersions() {
        return n;
    }

    public String getVersion() {
        return v;
    }
}
