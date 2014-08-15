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
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.Util;

/**
 * @author Malte Schütze
 */
public class KeyframeAndChunkDecodingTest extends TestCase {

    public SpectatorApiHandler apiHandler = new SpectatorApiHandler(Shard.EUW);
    public InProgressGame game = apiHandler.openFeaturedGame(apiHandler.getFeaturedGames().get(0));
    public GamePool pool = GamePool.singleton(game);

    public void testDumpChunk() {
        game.waitForEndOfGame();
        pool.shutdown();

        Util.hexdump(game.getChunk(1).getBuffer()).forEach(System.out::println);
    }

    public void testDumpKeyframe() {
        game.waitForEndOfGame();
        pool.shutdown();

        Util.hexdump(game.getKeyFrame(1).getBuffer()).forEach(System.out::println);
    }
}
