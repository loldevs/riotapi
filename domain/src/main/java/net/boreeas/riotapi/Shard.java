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
            "EUW1",
            "eu." + Constants.BASE_PATH,
            "chat.euw1." + Constants.BASE_PATH,
            String.format(Constants.API_PATH_FMT, "euw"),
            "https://lq.euw1." + Constants.BASE_PATH,
            "prod.euw1." + Constants.BASE_PATH,
            "http://spectator.eu." + Constants.BASE_PATH + ":8088",
            false),
    EUNE("eune",
            "EUN1",
            "eun1." + Constants.BASE_PATH,
            "chat.eun1." + Constants.BASE_PATH,
            String.format(Constants.API_PATH_FMT, "eune"),
            "https://lq.eun1." + Constants.BASE_PATH,
            "prod.eun1." + Constants.BASE_PATH,
            "http://spectator.eu." + Constants.BASE_PATH + ":8088",
            false),
    NA("na",
            "NA1",
            "na1." + Constants.BASE_PATH,
            "chat.na1." + Constants.BASE_PATH,
            String.format(Constants.API_PATH_FMT, "na"),
            "https://lq.na1." + Constants.BASE_PATH,
            "prod.na1." + Constants.BASE_PATH,
            "http://spectator.na." + Constants.BASE_PATH,
            false),
    BR("br",
            "BR1",
            "br." + Constants.BASE_PATH,
            "chat.br." + Constants.BASE_PATH,
            String.format(Constants.API_PATH_FMT, "br"),
            "https://lq.br." + Constants.BASE_PATH,
            "prod.br." + Constants.BASE_PATH,
            "http://spectator.br." + Constants.BASE_PATH,
            false),
    OCE("oce",
            "OC1",
            "oc1." + Constants.BASE_PATH,
            "chat.oc1." + Constants.BASE_PATH,
            String.format(Constants.API_PATH_FMT, "oce"),
            "https://lq.oc1." + Constants.BASE_PATH,
            "prod.oc1." + Constants.BASE_PATH,
            "http://spectator.oc1." + Constants.BASE_PATH,
            false),
    TR("tr",
            "TR1",
            "tr." + Constants.BASE_PATH,
            "chat.tr." + Constants.BASE_PATH,
            String.format(Constants.API_PATH_FMT, "tr"),
            "https://lq.tr." + Constants.BASE_PATH,
            "prod.tr." + Constants.BASE_PATH,
            "http://spectator.tr." + Constants.BASE_PATH,
            false),
    RU("ru",
            "RU",
            "ru." + Constants.BASE_PATH,
            "chat.ru." + Constants.BASE_PATH,
            String.format(Constants.API_PATH_FMT, "ru"),
            "https://lq.ru." + Constants.BASE_PATH,
            "prod.ru." + Constants.BASE_PATH,
            "http://spectator.eu." + Constants.BASE_PATH,
            false),
    LAN("lan",
            "LA1",
            "la1." + Constants.BASE_PATH,
            "chat.la1." + Constants.BASE_PATH,
            String.format(Constants.API_PATH_FMT, "lan"),
            "https://lq.la1." + Constants.BASE_PATH,
            "prod.la1." + Constants.BASE_PATH,
            "http://spectator.br." + Constants.BASE_PATH,
            false),
    LAS("las",
            "LA2",
            "la2." + Constants.BASE_PATH,
            "chat.la2." + Constants.BASE_PATH,
            String.format(Constants.API_PATH_FMT, "las"),
            "https://lq.la2." + Constants.BASE_PATH,
            "prod.la2." + Constants.BASE_PATH,
            "http://spectator.br." + Constants.BASE_PATH,
            false),
    PBE("pbe",
            "PBE1",
            "pbe1." + Constants.BASE_PATH,
            "chat.pbe1." + Constants.BASE_PATH,
            Constants.API_PATH_FMT,
            "https://lq.pbe1." + Constants.BASE_PATH,
            "prod.pbe1." + Constants.BASE_PATH,
            "http://spectator.pbe1." + Constants.BASE_PATH + ":8088",
            false),
    KR("kr",
            "KR",
            "kr." + Constants.BASE_PATH,
            "chat.kr." + Constants.BASE_PATH,
            String.format(Constants.API_PATH_FMT, "kr"),
            "https://lq.kr." + Constants.BASE_PATH,
            "prod.kr." + Constants.BASE_PATH,
            "QFKR1PROXY.kassad.in:8088",
            false),
    SG("sg",
            "SG",
            Constants.GARENA_PATH,
            "chat." + Constants.GARENA_PATH,
            null,
            "https://lq." + Constants.GARENA_PATH,
            "prod." + Constants.GARENA_PATH,
            "qfsea1proxy.kassad.in:8088",
            true),
    TW("tw",
            "TW",
            Constants.GARENA_PATH,
            "chatth." + Constants.GARENA_PATH,
            null,
            "https://loginqueuetw." + Constants.GARENA_PATH,
            "prodtw." + Constants.GARENA_PATH,
            "QFTW1PROXY.kassad.in:8088",
            true),
    TH("th",
            "TH",
            Constants.GARENA_PATH,
            "chatth." + Constants.GARENA_PATH,
            null,
            "https://lqth." + Constants.GARENA_PATH,
            "prodth." + Constants.GARENA_PATH,
            "qfsea1proxy.kassad.in:8088",
            true),
    PH("ph",
            "PH",
            Constants.GARENA_PATH,
            "chatph." + Constants.GARENA_PATH,
            null,
            "https://lqph." + Constants.GARENA_PATH,
            "prodph." + Constants.GARENA_PATH,
            "qfsea1proxy.kassad.in:8088",
            true),
    VN("vn",
            "VN",
            Constants.GARENA_PATH,
            "chatvn." + Constants.BASE_PATH,
            null,
            "https://lqvn." + Constants.GARENA_PATH,
            "prodvn." + Constants.GARENA_PATH,
            "qfsea1proxy.kassad.in:8088",
            true),
    ID("id",
            "ID",
            Constants.GARENA_PATH,
            "chatid." + Constants.BASE_PATH,
            null,
            "https://lqid." + Constants.GARENA_PATH,
            "prodid." + Constants.GARENA_PATH,
            "qfsea1proxy.kassad.in:8088",
            true);


    public final String name;
    public final String spectatorPlatformName;
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


    private Shard(String name, String spectatorPlatformName, String base, String chat, String api, String loginQueue, String prod, String spectator, boolean garena) {
        this.name = name;
        this.spectatorPlatformName = spectatorPlatformName;
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
        static String API_PATH_FMT = "https://%s.api.pvp.net/api/lol";
    }

    public static Shard getBySpectatorPlatform(String name) {
        for (Shard shard: values()) {
            if (shard.spectatorPlatformName.equals(name)) {
                return shard;
            }
        }

        return null;
    }
}
