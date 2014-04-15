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
    EUW("euw",  "eu.lol.riotgames.com"),
    EUNE("eune","chat.eun1.lol.riotgames.com"),
    NA("na",    "chat.na1.lol.riotgames.com"),
    BR("br",    "chat.br.lol.riotgames.com"),
    OCE("oce",  "chat.oc1.lol.riotgames.com"),
    TR("tr",    "tr.lol.riotgames.com"),
    RU("ru",    "ru.lol.riotgames.com"),
    LAN("lan",  "la1.lol.riotgames.com"),
    LAS("las",  "la2.lol.riotgames.com"),
    PBE("pbe",  "pbe1.lol.riotgames.com"),
    KR("kr",    "kr.lol.riotgames.com"),
    SG("sg",    "lol.garenanow.com", true),
    TW("tw",    "lol.garenanow.com", "chattw.lol.garenanow.com", "prodtw.lol.garenanow.com", "loginqueuetw.lol.garenanow.com", true),
    TH("th",    "lol.garenanow.com", "chatth.lol.garenanow.com", "prodth.lol.garenanow.com", "lqth.lol.garenanow.com", true),
    PH("ph",    "lol.garenanow.com", "chatph.lol.garenanow.com", "prodph.lol.garenanow.com", "lqph.lol.garenanow.com", true),
    VN("vn",    "lol.garenanow.com", "chatvn.lol.garenanow.com", "prodvn.lol.garenanow.com", "lqvn.lol.garenanow.com", true);


    public final String name;
    public final String baseUrl;
    public final String chatUrl;
    public final String prodUrl;
    public final String loginQueue;
    public final boolean isGarena;

    public static final int jabberPort = 5223;


    private Shard(String name, String baseUrl) {
        this(name, baseUrl, "chat." + baseUrl, "prod." + baseUrl, "lq." + baseUrl, false);
    }

    private Shard(String name, String baseUrl, boolean isGarena) {
        this(name, baseUrl, "chat." + baseUrl, "prod." + baseUrl, "lq." + baseUrl, isGarena);
    }

    private Shard(String name, String baseUrl, String chatUrl, String prodUrl, String lqUrl, boolean isGarena) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.chatUrl = chatUrl;
        this.prodUrl = prodUrl;
        this.loginQueue = lqUrl;
        this.isGarena = isGarena;
    }
}
