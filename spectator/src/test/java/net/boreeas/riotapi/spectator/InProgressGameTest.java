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

import java.util.concurrent.atomic.AtomicInteger;

public class InProgressGameTest extends TestCase {
    private static SpectatorApiHandler handler = new SpectatorApiHandler(Shard.EUW);
    private InProgressGame game = handler.openFeaturedGame(handler.getFeaturedGames().get(0));

    private final AtomicInteger counter = new AtomicInteger(0);
    private GamePool pool = GamePool.singleton(game, err -> counter.incrementAndGet());

    public void testWaitForChunk_testChunkCount() {
        assertNotNull(game.getChunk(game.getLastChunkInfo().getChunkId() + 1));
        System.out.println("Chunks read: " + game.getChunkCount());
        System.out.println("Errors: " + counter);
        System.out.println("Min chunk: " + game.getFirstAvailableChunk());
        System.out.println("Max chunk: " + game.getLastAvailableChunk());
        counter.set(0);
    }

    public void testWaitForKeyFrame() {
        assertNotNull(game.getKeyFrame(game.getLastChunkInfo().getKeyFrameId() + 1));
        System.out.println("Keyframes read: " + game.getKeyFrameCount());
        System.out.println("Errors: " + counter);
        System.out.println("Min keyframe: " + game.getFirstAvailableKeyFrame());
        System.out.println("Max keyframe: " + game.getLastAvailableKeyFrame());
        counter.set(0);
    }
}