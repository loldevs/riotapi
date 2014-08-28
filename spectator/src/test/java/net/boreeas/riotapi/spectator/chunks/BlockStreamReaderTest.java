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

package net.boreeas.riotapi.spectator.chunks;

import junit.framework.TestCase;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.spectator.GamePool;
import net.boreeas.riotapi.spectator.InProgressGame;
import net.boreeas.riotapi.spectator.SpectatorApiHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j
public class BlockStreamReaderTest extends TestCase {

    private static ArrayList<Block> blocks = new ArrayList<>();

    static {
        SpectatorApiHandler handler = new SpectatorApiHandler(Shard.EUW);
        InProgressGame game = handler.openFeaturedGame(handler.getFeaturedGames().get(0));
        GamePool pool = GamePool.singleton(game);

        BlockStreamReader reader = new BlockStreamReader(game.getKeyFrame(game.getFirstAvailableKeyFrame()).getBuffer());
        while (reader.hasNext()) {
            blocks.add(reader.next());
        }
    }

    public void testNext() throws Exception {
        assertFalse(blocks.isEmpty());

        Map<Integer, AtomicInteger> counts = new HashMap<>();
        for (Block block: blocks) {
            logBlock(block);
            if (counts.containsKey(block.getHeader().getType())) {
                counts.get(block.getHeader().getType()).incrementAndGet();
            } else {
                counts.put(block.getHeader().getType(), new AtomicInteger(1));
            }
        }

        log.info("\n===== Counts =====");
        for (Map.Entry<Integer, AtomicInteger> countEntry: counts.entrySet()) {
            log.info(String.format("[%2x.d] %d", countEntry.getKey(), countEntry.getValue().get()));
        }
        log.info("Total: " + blocks.size());
    }

    private void logBlock(Block block) {
        String fmt = "[%2x.d] t=%6.dms p=%x.d l=%d";
        BlockHeader header = block.getHeader();
        log.info(String.format(fmt, header.getType(), header.getTimestamp(), header.getTimestamp(), header.getBlockParam(), header.getContentLength()));
    }
}