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

/**
 * Constants for the different regions
 * Created on 4/12/2014.
 */
public enum Shard {

    EUW("euw",
            "eu." + Constants.BASE_PATH,
            "chat.eu." + Constants.BASE_PATH,
            Constants.API_PATH,
            "https://lq.euw1." + Constants.BASE_PATH,
            "prod.euw1." + Constants.BASE_PATH,
            "http://spectator.eu." + Constants.BASE_PATH + ":8088",
            false),
    EUNE("eune",
            "eun1." + Constants.BASE_PATH,
            "chat.eun1." + Constants.BASE_PATH,
            Constants.API_PATH,
            "https://lq.eun1." + Constants.BASE_PATH,
            "prod.eun1." + Constants.BASE_PATH,
            "https://spectator.eu." + Constants.BASE_PATH + ":8088",
            false),
    NA("na",
            "na1." + Constants.BASE_PATH,
            "chat.na1." + Constants.BASE_PATH,
            Constants.API_PATH,
            "https://lq.na1." + Constants.BASE_PATH,
            "prod.na1." + Constants.BASE_PATH,
            "https://spectator.na." + Constants.BASE_PATH + ":8088",
            false),
    BR("br",
            "br." + Constants.BASE_PATH,
            "chat.br." + Constants.BASE_PATH,
            Constants.API_PATH,
            "https://lq.br." + Constants.BASE_PATH,
            "prod.br." + Constants.BASE_PATH,
            "https://spectator.br." + Constants.BASE_PATH + ":80",
            false),
    OCE("oce",
            "oc1." + Constants.BASE_PATH,
            "chat.oc1." + Constants.BASE_PATH,
            Constants.API_PATH,
            "https://lq.oc1." + Constants.BASE_PATH,
            "prod.oc1." + Constants.BASE_PATH,
            null,   // TODO: OCE spectator url
            false),
    TR("tr",
            "tr." + Constants.BASE_PATH,
            "chat.tr." + Constants.BASE_PATH,
            Constants.API_PATH,
            "https://lq.tr." + Constants.BASE_PATH,
            "prod.tr." + Constants.BASE_PATH,
            "https://spectator.tr." + Constants.BASE_PATH + ":80",
            false),
    RU("ru",
            "ru." + Constants.BASE_PATH,
            "chat.ru." + Constants.BASE_PATH,
            Constants.API_PATH,
            "https://lq.ru." + Constants.BASE_PATH,
            "prod.ru." + Constants.BASE_PATH,
            "https://spectator.eu." + Constants.BASE_PATH + ":80",
            false),
    LAN("lan",
            "la1." + Constants.BASE_PATH,
            "chat.la1." + Constants.BASE_PATH,
            Constants.API_PATH,
            "https://lq.la1." + Constants.BASE_PATH,
            "prod.la1." + Constants.BASE_PATH,
            "https://spectator.br." + Constants.BASE_PATH + ":80",
            false),
    LAS("las",
            "la2." + Constants.BASE_PATH,
            "chat.la2." + Constants.BASE_PATH,
            Constants.API_PATH,
            "https://lq.la2." + Constants.BASE_PATH,
            "prod.la2." + Constants.BASE_PATH,
            "https://spectator.br." + Constants.BASE_PATH + ":80",
            false),
    PBE("pbe",
            "pbe1." + Constants.BASE_PATH,
            "chat.pbe1." + Constants.BASE_PATH,
            Constants.API_PATH,
            "https://lq.pbe1." + Constants.BASE_PATH,
            "prod.pbe1." + Constants.BASE_PATH,
            "https://spectator.pbe1." + Constants.BASE_PATH + ":8088",
            false),
    KR("kr",
            "kr." + Constants.BASE_PATH,
            "chat.kr." + Constants.BASE_PATH,
            "https://asia.api." + Constants.BASE_PATH + "/api/lol",
            "https://lq.kr." + Constants.BASE_PATH,
            "prod.kr." + Constants.BASE_PATH,
            "https://QFKR1PROXY.kassad.in:8088",
            false),
    SG("sg",
            Constants.GARENA_PATH,
            "chat." + Constants.GARENA_PATH,
            "https://asia.api." + Constants.BASE_PATH + "/api/lol",
            "https://lq." + Constants.GARENA_PATH,
            "prod." + Constants.GARENA_PATH,
            "https://qfsea1proxy.kassad.in:8088",
            true),
    TW("tw",
            Constants.GARENA_PATH,
            "chatth." + Constants.GARENA_PATH,
            "https://asia.api." + Constants.BASE_PATH + "/api/lol",
            "https://loginqueuetw." + Constants.GARENA_PATH,
            "prodtw." + Constants.GARENA_PATH,
            "https://FQTW1PROXY.kassad.in:8088",
            true),
    TH("th",
            Constants.GARENA_PATH,
            "chatth." + Constants.GARENA_PATH,
            "https://asia.api." + Constants.BASE_PATH + "/api/lol",
            "https://lqth." + Constants.GARENA_PATH,
            "prodth." + Constants.GARENA_PATH,
            "https://qfsea1proxy.kassad.in:8088",
            true),
    PH("ph",
            Constants.GARENA_PATH,
            "chatph." + Constants.GARENA_PATH,
            "https://asia.api." + Constants.BASE_PATH + "/api/lol",
            "https://lqph." + Constants.GARENA_PATH,
            "prodph." + Constants.GARENA_PATH,
            "https://qfsea1proxy.kassad.in:8088",
            true),
    VN("vn",
            Constants.GARENA_PATH,
            "chatvn." + Constants.BASE_PATH,
            "https://asia.api." + Constants.BASE_PATH + "/api/lol",
            "https://lqvn." + Constants.GARENA_PATH,
            "prodvn." + Constants.GARENA_PATH,
            "https://qfsea1proxy.kassad.in:8088",
            true);


    public final String name;
    public final String baseUrl;
    public final String chatUrl;
    public final String prodUrl;
    public final String loginQueue;
    public final String apiUrl;
    public final String spectatorUrl;
    public final boolean isGarena;

    public static final int JABBER_PORT = 5223;
    public static final int RTMPS_PORT = 2099;
    public static final String CONN_INFO_SERVICE = "http://ll.leagueoflegends.com/services/connection_info";


    private Shard(String name, String base, String chat, String api, String loginQueue, String prod, String spectator, boolean garena) {
        this.name = name;
        this.baseUrl = base;
        this.chatUrl = chat;
        this.apiUrl = api;
        this.prodUrl = prod;
        this.loginQueue = loginQueue;
        this.spectatorUrl = spectator;
        this.isGarena = garena;
    }


    private static class Constants {
        static String BASE_PATH = "lol.riotgames.com";
        static String GARENA_PATH = "lol.garenanow.com";
        static String API_PATH= "https://prod.api." + Constants.BASE_PATH + "/api/lol";
    }
}
