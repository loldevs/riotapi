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

    public static SpectatorApiHandler apiHandler = new SpectatorApiHandler(Shard.EUW);
    public static InProgressGame game = apiHandler.openFeaturedGame(apiHandler.getFeaturedGames().get(0));
    public static GamePool pool = GamePool.singleton(game, Throwable::printStackTrace);

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

    public void testDumpAllKeyFrames() {
        game.waitForEndOfGame();
        pool.shutdown();

        for (int i = game.getFirstAvailableKeyFrame(); i <= game.getLastAvailableKeyFrame(); i++) {
            System.out.println("[ Keyframe " + i + " ]");
            Util.hexdump(game.getKeyFrame(i).getBuffer()).forEach(System.out::println);
            System.out.println();
        }
    }

    public void testDumpAllChunks() {
        game.waitForEndOfGame();
        pool.shutdown();

        for (int i = game.getFirstAvailableChunk(); i <= game.getLastAvailableChunk(); i++) {
            System.out.println("[ Chunk " + i + " ]");
            Util.hexdump(game.getChunk(i).getBuffer()).forEach(System.out::println);
            System.out.println();
        }
    }
}
