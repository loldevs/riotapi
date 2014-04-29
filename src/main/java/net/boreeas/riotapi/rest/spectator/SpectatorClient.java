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

package net.boreeas.riotapi.rest.spectator;

import net.boreeas.riotapi.Shard;

/**
 * Created on 4/28/2014.
 */
public class SpectatorClient {

    private SpectatorApiHandler handler;
    private Platform platform;
    private long gameId;
    private GameMetaData metaData;
    private String encryptionKey;


    public SpectatorClient(Shard region, Platform platform, long gameId) {
        this.handler = new SpectatorApiHandler(region);
        this.platform = platform;
        this.gameId = gameId;

        initialize();
    }

    private void initialize() {
        metaData = handler.getGameMetaData(platform, gameId);

    }


}
