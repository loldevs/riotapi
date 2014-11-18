/*
 * Copyright 2014 The LolDevs team (https://github.com/loldevs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Constants for the different regions
 * Created on 4/12/2014.
 */
public enum Shard {

    EUW("euw",
            String.format(Constants.API_PATH_TEMPLATE, "euw"),
            // Alt values if loading fails
            "euw",
            "EUW1",
            "chat.euw1." + Constants.BASE_PATH,
            "https://lq.euw1." + Constants.BASE_PATH,
            "prod.euw1." + Constants.BASE_PATH,
            "http://spectator.eu." + Constants.BASE_PATH + ":8088"),
    EUNE("eune",
            String.format(Constants.API_PATH_TEMPLATE, "eune"),
            // Alt values if loading fails
            "eune",
            "EUN1",
            "chat.eun1." + Constants.BASE_PATH,
            "https://lq.eun1." + Constants.BASE_PATH,
            "prod.eun1." + Constants.BASE_PATH,
            "http://spectator.eu." + Constants.BASE_PATH + ":8088"),
    NA("na",
            String.format(Constants.API_PATH_TEMPLATE, "na"),
            // Alt values if loading fails
            "na",
            "NA1",
            "chat.na1." + Constants.BASE_PATH,
            "https://lq.na1." + Constants.BASE_PATH,
            "prod.na1." + Constants.BASE_PATH,
            "http://spectator.na." + Constants.BASE_PATH),
    BR("br",
            String.format(Constants.API_PATH_TEMPLATE, "br"),
            // Alt values if loading fails
            "br",
            "BR1",
            "chat.br." + Constants.BASE_PATH,
            "https://lq.br." + Constants.BASE_PATH,
            "prod.br." + Constants.BASE_PATH,
            "http://spectator.br." + Constants.BASE_PATH),
    OCE("oc1",
            String.format(Constants.API_PATH_TEMPLATE, "oce"),
            // Alt values if loading fails
            "oce",
            "OC1",
            "chat.oc1." + Constants.BASE_PATH,
            "https://lq.oc1." + Constants.BASE_PATH,
            "prod.oc1." + Constants.BASE_PATH,
            "http://spectator.oc1." + Constants.BASE_PATH),
    TR("tr",
            String.format(Constants.API_PATH_TEMPLATE, "tr"),
            // Alt values if loading fails
            "tr",
            "TR1",
            "chat.tr." + Constants.BASE_PATH,
            "https://lq.tr." + Constants.BASE_PATH,
            "prod.tr." + Constants.BASE_PATH,
            "http://spectator.tr." + Constants.BASE_PATH),
    RU("ru",
            String.format(Constants.API_PATH_TEMPLATE, "ru"),
            // Alt values if loading fails
            "ru",
            "RU",
            "chat.ru." + Constants.BASE_PATH,
            "https://lq.ru." + Constants.BASE_PATH,
            "prod.ru." + Constants.BASE_PATH,
            "http://spectator.eu." + Constants.BASE_PATH),
    LAN("lan",
            String.format(Constants.API_PATH_TEMPLATE, "lan"),
            // Alt values if loading fails
            "lan",
            "LA1",
            "chat.la1." + Constants.BASE_PATH,
            "https://lq.la1." + Constants.BASE_PATH,
            "prod.la1." + Constants.BASE_PATH,
            "http://spectator.br." + Constants.BASE_PATH),
    LAS("las",
            String.format(Constants.API_PATH_TEMPLATE, "las"),
            // Alt values if loading fails
            "las",
            "LA2",
            "chat.la2." + Constants.BASE_PATH,
            "https://lq.la2." + Constants.BASE_PATH,
            "prod.la2." + Constants.BASE_PATH,
            "http://spectator.br." + Constants.BASE_PATH),
    KR("kr",
            "http://legendspatch-lol.x-cdn.com/KR_CBT/projects/lol_air_client_config_%s/releases/releaselisting_%s",
            "http://legendspatch-lol.x-cdn.com/KR_CBT/projects/lol_air_client_config_%s/releases/%s/files/lol.properties",
            String.format(Constants.API_PATH_TEMPLATE, "kr"),
            // Alt values if loading fails
            "kr",
            "KR",
            "chat.kr." + Constants.BASE_PATH,
            "https://lq.kr." + Constants.BASE_PATH,
            "prod.kr." + Constants.BASE_PATH,
            "QFKR1PROXY.kassad.in:8088"),

    // The following shards apparently don't specify a properties listing
    PBE("pbe",
            "PBE1",
            "chat.pbe1." + Constants.BASE_PATH,
            Constants.API_PATH_TEMPLATE,
            "https://lq.pbe1." + Constants.BASE_PATH,
            "prod.pbe1." + Constants.BASE_PATH,
            "http://spectator.pbe1." + Constants.BASE_PATH + ":8088",
            false),
    SG("sg",
            "SG",
            "chat." + Constants.GARENA_PATH,
            null,
            "https://lq." + Constants.GARENA_PATH,
            "prod." + Constants.GARENA_PATH,
            "qfsea1proxy.kassad.in:8088",
            true),
    TW("tw",
            "TW",
            "chatth." + Constants.GARENA_PATH,
            null,
            "https://loginqueuetw." + Constants.GARENA_PATH,
            "prodtw." + Constants.GARENA_PATH,
            "QFTW1PROXY.kassad.in:8088",
            true),
    TH("th",
            "TH",
            "chatth." + Constants.GARENA_PATH,
            null,
            "https://lqth." + Constants.GARENA_PATH,
            "prodth." + Constants.GARENA_PATH,
            "qfsea1proxy.kassad.in:8088",
            true),
    PH("ph",
            "PH",
            "chatph." + Constants.GARENA_PATH,
            null,
            "https://lqph." + Constants.GARENA_PATH,
            "prodph." + Constants.GARENA_PATH,
            "qfsea1proxy.kassad.in:8088",
            true),
    VN("vn",
            "VN",
            "chatvn." + Constants.GARENA_PATH,
            null,
            "https://lqvn." + Constants.GARENA_PATH,
            "prodvn." + Constants.GARENA_PATH,
            "qfsea1proxy.kassad.in:8088",
            true),
    ID("id",
            "ID",
            "chatid." + Constants.GARENA_PATH,
            null,
            "https://lqid." + Constants.GARENA_PATH,
            "prodid." + Constants.GARENA_PATH,
            "qfsea1proxy.kassad.in:8088",
            true);


    public final String name;
    public final String spectatorPlatformName;
    public final String chatUrl;
    public final String prodUrl;
    public final String loginQueue;
    public final String apiUrl;
    public final String spectatorUrl;
    public final boolean isGarena;

    public final Version version;

    public static final int JABBER_PORT = 5223;
    public static final int RTMPS_PORT = 2099;
    public static final String CONN_INFO_SERVICE = "http://ll.leagueoflegends.com/services/connection_info";

    private Shard(String cdnTag, String api,
                  String altName, String altSpectatorPlatformName, String altChat, String altLoginQueue, String altProd, String altSpectator) {

        this(cdnTag, Constants.VERSION_LISTING_TEMPLATE, Constants.PROPERTIES_TEMPLATE,
                api, altName, altSpectatorPlatformName, altChat, altLoginQueue, altProd, altSpectator);
    }

    private Shard(String cdnTag, String versionListingTemplate, String propertiesTemplate, String api,
                  String altName, String altSpectatorPlatformName, String altChat, String altLoginQueue, String altProd, String altSpectator) {

        Properties properties = new Properties();
        Version version = new Version("0");
        try {
            List<Version> versions = loadCurrentVersions(versionListingTemplate, cdnTag);

            PropertyData propertyData = new PropertyData(cdnTag, propertiesTemplate, properties, version, versions).invoke();
            version = propertyData.getVersion();
            properties = propertyData.getProperties();

        } catch (IOException e) {
            Logger.getLogger(Shard.class).fatal("Failed to load shard data for " + cdnTag, e);
        }

        this.version = version;

        this.prodUrl = properties.getProperty("host", altProd).trim();
        this.chatUrl = properties.getProperty("xmpp_server", altChat).trim();
        this.loginQueue = properties.getProperty("lq_uri", altLoginQueue).trim();
        this.name = properties.getProperty("regionTag", altName).trim();
        this.spectatorPlatformName = properties.getProperty("platformId", altSpectatorPlatformName).trim();


        String spectator = properties.getProperty("featuredGamesUrl");
        if (spectator == null || spectator.isEmpty()) {
            spectator = altSpectator;
        } else {
            try {
                URI spectatorUri = new URI(spectator.trim());
                spectator = spectatorUri.getScheme() + "://" + spectatorUri.getHost() + ":" + spectatorUri.getPort();
            } catch (URISyntaxException ex) {
                spectator = altSpectator;
            }
        }
        this.spectatorUrl = spectator;

        this.isGarena = false;
        this.apiUrl = api;
    }

    private Shard(String name, String spectatorPlatformName, String chat, String api, String loginQueue, String prod, String spectator, boolean garena) {

        this.name = name;
        this.spectatorPlatformName = spectatorPlatformName;
        this.chatUrl = chat;
        this.apiUrl = api;
        this.prodUrl = prod;
        this.loginQueue = loginQueue;
        this.spectatorUrl = spectator;
        this.isGarena = garena;
        this.version = new Version("0");
    }



    private List<Version> loadCurrentVersions(String template, String name) throws IOException {
        List<Version> result = new ArrayList<>();

        URL versionData = new URL(String.format(template, name, name.toUpperCase()));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(versionData.openStream()))) {
            String in;
            while ((in = reader.readLine()) != null) {
                result.add(new Version(in.trim()));
            }
        }

        return result;
    }


    private static class Constants {
        static String BASE_PATH = "lol.riotgames.com";
        static String GARENA_PATH = "lol.garenanow.com";
        static String API_PATH_TEMPLATE = "https://%s.api.pvp.net/api/lol";

        static String VERSION_LISTING_TEMPLATE = "http://l3cdn.riotgames.com/releases/live/projects/lol_air_client_config_%s/releases/releaselisting_%s";
        static String PROPERTIES_TEMPLATE = "http://l3cdn.riotgames.com/releases/live/projects/lol_air_client_config_%s/releases/%s/files/lol.properties";

        static int MAX_VERSION_FALLBACK = 3;
    }

    private class PropertyData {
        private String cdnTag;
        private String propertiesTemplate;
        private Properties properties;
        private Version version;
        private List<Version> versions;

        public PropertyData(String cdnTag, String propertiesTemplate, Properties properties, Version version, List<Version> versions) {
            this.cdnTag = cdnTag;
            this.propertiesTemplate = propertiesTemplate;
            this.properties = properties;
            this.version = version;
            this.versions = versions;
        }

        public Properties getProperties() {
            return properties;
        }

        public Version getVersion() {
            return version;
        }

        public Shard.PropertyData invoke() throws IOException {
            for (int i = 0; i < Constants.MAX_VERSION_FALLBACK; i++) {
                try {
                    properties = loadShardData(propertiesTemplate, cdnTag, versions.get(i).getVersionString());
                    version = versions.get(i);
                    return this;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Shard.class).error("No shard data listing for " + cdnTag + "/" + versions.get(i).getVersionString());
                }
            }

            Logger.getLogger(Shard.class).error("No shard data listing found for the last " + Constants.MAX_VERSION_FALLBACK + " versions, falling back to default values");

            return this;
        }

        private Properties loadShardData(String template, String cdnTag, String version) throws IOException {
            Properties properties = new Properties();
            try (InputStream stream = new URL(String.format(template, cdnTag, version)).openStream()) {
                properties.load(stream);
            }

            return properties;
        }
    }



    public static Shard getBySpectatorPlatform(String name) {
        for (Shard shard: values()) {
            if (shard.spectatorPlatformName.equals(name)) {
                return shard;
            }
        }

        return null;
    }

    public static Shard getByName(String slug) {
        for (Shard shard: values()) {
            if (shard.name.equalsIgnoreCase(slug)) {
                return shard;
            }
        }

        return null;
    }
}
