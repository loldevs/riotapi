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

import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.Util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Malte Schütze
 */
public class KeyframeAndChunkDecodingTest /*extends TestCase*/ {

    public static SpectatorApiHandler apiHandler = new SpectatorApiHandler(Shard.EUW);
    public static InProgressGame game = apiHandler.openFeaturedGame(apiHandler.getFeaturedGames().get(0));
    public static GamePool pool = GamePool.singleton(game, Throwable::printStackTrace);

    public void testDumpChunk() throws InterruptedException, ExecutionException, TimeoutException {
        game.waitForEndOfGame();
        pool.shutdown();

        Util.hexdump(game.getFutureChunk(1).get(100, TimeUnit.MILLISECONDS).getBuffer()).forEach(System.out::println);
    }

    public void testDumpKeyframe() throws InterruptedException, ExecutionException, TimeoutException {
        game.waitForEndOfGame();
        pool.shutdown();

        Util.hexdump(game.getFutureKeyFrame(1).get(100, TimeUnit.MILLISECONDS).getBuffer()).forEach(System.out::println);
    }

    public void testDumpAllKeyFrames() {
        game.waitForEndOfGame();
        pool.shutdown();

        for (int i = game.getFirstAvailableKeyFrame(); i <= game.getLastAvailableKeyFrame(); i++) {
            System.out.println("[ Keyframe " + i + " ]");
            try {
                Util.hexdump(game.getFutureKeyFrame(i).get(20, TimeUnit.MILLISECONDS).getBuffer()).forEach(System.out::println);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    public void testDumpAllChunks() {
        game.waitForEndOfGame();
        pool.shutdown();

        for (int i = game.getFirstAvailableChunk(); i <= game.getLastAvailableChunk(); i++) {
            System.out.println("[ Chunk " + i + " ]");
            try {
                Util.hexdump(game.getFutureChunk(i).get(20, TimeUnit.MILLISECONDS).getBuffer()).forEach(System.out::println);
            } catch (InterruptedException | TimeoutException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    private void analyzeChunk(Chunk c) {

    }
}
