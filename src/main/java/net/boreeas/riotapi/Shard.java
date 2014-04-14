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
    EUW("euw", "chat.eu.lol.riotgames.com", 5223, "prod.euw.lol.riotgames.com"),
    EUNE("eune", "chat.eun1.lol.riotgames.com", 5223, "prod.eun1.lol.riotgames.com"),
    NA("na", "chat.na1.lol.riotgames.com", 5223, "prod.na1.lol.riotgames.com"),
    BR("br", "chat.br.lol.riotgames.com", 5223, "prod.br.lol.riotgames.com"),
    OCE("oce", "chat.oc1.lol.riotgames.com", 5223, "prod.oc1.lol.riotgames.com"),
    KR("kr", null, 0, null);  // TODO: kr urls

    public final String name;
    public final String jabberUrl;
    public final int jabberPort;
    public final String prodUrl;

    private Shard(String name, String jabberUrl, int jabberPort, String prodUrl) {
        this.name = name;
        this.jabberUrl = jabberUrl;
        this.jabberPort = jabberPort;
        this.prodUrl = prodUrl;
    }
}
