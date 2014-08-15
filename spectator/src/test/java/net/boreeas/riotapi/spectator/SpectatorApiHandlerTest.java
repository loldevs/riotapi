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

package net.boreeas.riotapi.spectator;

import junit.framework.TestCase;
import net.boreeas.riotapi.RequestException;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.spectator.rest.FeaturedGame;

public class SpectatorApiHandlerTest extends TestCase {

    private static Shard shard = Shard.EUW;
    private static SpectatorApiHandler handler = new SpectatorApiHandler(shard);

    private FeaturedGame game = handler.getFeaturedGames().get(0);

    public void testGetCurrentVersion() {
        System.out.println("Version: " + handler.getCurrentVersion());
    }

    public void testGetFeaturedGames() {
        handler.getFeaturedGameListDto();
    }

    public void testGetLastChunkInfo() {
        handler.getLastChunkInfo(game.getPlatformId(), game.getGameId());

        try {
            Object obj = handler.getLastChunkInfo(game.getPlatformId(), 0);
            fail("Expected requestException, got " + obj);
        } catch (RequestException ex) {

        } catch (Exception ex) {
            fail("Expected requestException, got " + ex);
        }
    }

    public void testGetMetaData() {
        handler.getGameMetaData(game.getPlatformId(), game.getGameId());
    }

    public void testGetChunk() {
        handler.getEncryptedChunk(game.getPlatformId(), game.getGameId(), handler.getLastChunkInfo(game.getPlatformId(), game.getGameId()).getChunkId());

        try {
            Object obj = handler.getEncryptedChunk(game.getPlatformId(), game.getGameId(), handler.getLastChunkInfo(game.getPlatformId(), game.getGameId()).getChunkId() + 5);
            fail("Expected requestException, got " + obj);
        } catch (RequestException ex) {

        } catch (Exception ex) {
            fail("Expected requestException, got " + ex);
        }
    }

    public void testGetKeyframe() {
        handler.getEncryptedKeyframe(game.getPlatformId(), game.getGameId(), handler.getLastChunkInfo(game.getPlatformId(), game.getGameId()).getKeyFrameId());
    }

}