/*
 * Copyright 2014 Malte Schütze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi;

/**
 * Constants for the different regions
 * Created on 4/12/2014.
 */
public enum Shard {
    // TODO: Some of this may be outdated. SEA is missing?
    EUW("euw", "eu.lol.riotgames.com", "eu", 8088),
    EUNE("eune", "eun1.lol.riotgames.com", "eu", 8088),
    NA("na", "na1.lol.riotgames.com", "na", 8088),
    BR("br", "br.lol.riotgames.com", "br", 80),
    OCE("oce", "oc1.lol.riotgames.com", null, 8088),    // TODO: OCE spectator url
    TR("tr", "tr.lol.riotgames.com", "eu", "tr", 80),
    RU("ru", "ru.lol.riotgames.com", "eu", "tr", 80),
    LAN("lan", "la1.lol.riotgames.com", "br", 80),
    LAS("las", "la2.lol.riotgames.com", "br", 80),
    PBE("pbe", "pbe1.lol.riotgames.com", "pbe1", 8088),
    KR("kr", "kr.lol.riotgames.com", "asia", false, "QFKR1PROXY.kassad.in", 8088),
    SG("sg", "lol.garenanow.com", "asia", true, null, 8088),    // TODO: SG spectator URL
    TW("tw", "lol.garenanow.com", "asia", "chattw", "prodtw", "loginqueuetw", true, "FQTW1PROXY.kassad.in", 8088),
    TH("th", "lol.garenanow.com", "asia", "chatth", "prodth", "lqth", true, null, 8088), // TODO: TH spectator url
    PH("ph", "lol.garenanow.com", "asia", "chatph", "prodph", "lqph", true, null, 8088), // TODO: PH spectator url
    VN("vn", "lol.garenanow.com", "asia", "chatvn", "prodvn", "lqvn", true, null, 8088); // TODO: VN spectator url


    public final String name;
    public final String baseUrl;
    public final String chatUrl;
    public final String prodUrl;
    public final String loginQueue;
    public final String apiUrl;
    public final String spectatorUrl;
    public final boolean isGarena;

    public static final int jabberPort = 5223;
    public static final int rtmpsPort = 2099;
    public static final String rtmpsAppPath = "app:/mod_ser.dat";

    private Shard(String name, String baseUrl, String spectatorPath, int spectatorPort) {
        this(name, baseUrl, "prod", "chat", "prod", "lq", false, "spectator." + spectatorPath + "." + baseUrl, spectatorPort);
    }

    private Shard(String name, String baseUrl, String apiPath, String spectatorPath, int spectatorPort) {
        this(name, baseUrl, apiPath, "chat", "prod", "lq", false, "spectator." + spectatorPath + "." + baseUrl, spectatorPort);
    }

    private Shard(String name, String baseUrl, String apiPath, boolean isGarena, String spectatorUrl, int spectatorPort) {
        this(name, baseUrl, apiPath, "chat", "prod", "lq", isGarena, spectatorUrl, spectatorPort);
    }

    private Shard(String name, String baseUrl, String apiPath, String chatPath, String prodPath, String lqPath,
                  boolean isGarena, String spectatorUrl, int spectatorPort) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.apiUrl = "https://" + apiPath + ".api.pvp.net/api/lol";
        this.chatUrl = chatPath + "." + baseUrl;
        this.prodUrl = prodPath + "." + baseUrl;
        this.loginQueue = lqPath + "." + baseUrl;
        this.isGarena = isGarena;
        this.spectatorUrl = "http://" + spectatorUrl + ":" + spectatorPort;
    }
}
