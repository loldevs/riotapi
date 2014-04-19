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
    EUW("euw", "eu.lol.riotgames.com"),
    EUNE("eune", "eun1.lol.riotgames.com"),
    NA("na", "na1.lol.riotgames.com"),
    BR("br", "br.lol.riotgames.com"),
    OCE("oce", "oc1.lol.riotgames.com"),
    TR("tr", "tr.lol.riotgames.com", "eu"),
    RU("ru", "ru.lol.riotgames.com", "eu"),
    LAN("lan", "la1.lol.riotgames.com"),
    LAS("las", "la2.lol.riotgames.com"),
    PBE("pbe", "pbe1.lol.riotgames.com"),
    KR("kr", "kr.lol.riotgames.com", "asia"),
    SG("sg", "lol.garenanow.com", "asia", true),
    TW("tw", "lol.garenanow.com", "asia", "chattw", "prodtw", "loginqueuetw", true),
    TH("th", "lol.garenanow.com", "asia", "chatth", "prodth", "lqth", true),
    PH("ph", "lol.garenanow.com", "asia", "chatph", "prodph", "lqph", true),
    VN("vn", "lol.garenanow.com", "asia", "chatvn", "prodvn", "lqvn", true);


    public final String name;
    public final String baseUrl;
    public final String chatUrl;
    public final String prodUrl;
    public final String loginQueue;
    public final String apiUrl;
    public final boolean isGarena;

    public static final int jabberPort = 5223;
    public static final int rtmpsPort = 2099;
    public static final String rtmpsAppPath = "app:/mod_ser.dat";

    private Shard(String name, String baseUrl) {
        this(name, baseUrl, "prod", "chat", "prod", "lq", false);
    }

    private Shard(String name, String baseUrl, String apiPath) {
        this(name, baseUrl, apiPath, "chat", "prod", "lq", false);
    }

    private Shard(String name, String baseUrl, String apiPath, boolean isGarena) {
        this(name, baseUrl, apiPath, "chat", "prod", "lq", isGarena);
    }

    private Shard(String name, String baseUrl, String apiPath, String chatPath, String prodPath, String lqPath, boolean isGarena) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.apiUrl = "https://" + apiPath + ".api.pvp.net/api/lol";
        this.chatUrl = chatPath + "." + baseUrl;
        this.prodUrl = prodPath + "." + baseUrl;
        this.loginQueue = lqPath + "." + baseUrl;
        this.isGarena = isGarena;
    }
}
