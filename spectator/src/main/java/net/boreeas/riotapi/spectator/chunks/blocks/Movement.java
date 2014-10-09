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

package net.boreeas.riotapi.spectator.chunks.blocks;

import lombok.Value;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.spectator.chunks.Block;
import net.boreeas.riotapi.spectator.chunks.BlockHeader;
import net.boreeas.riotapi.spectator.chunks.BlockType;
import net.boreeas.riotapi.spectator.chunks.IsBlock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * @author Malte Schütze
 */
@Value
@Log4j
@IsBlock(BlockType.MOVEMENT)
public class Movement extends Block {
    private long timestamp;
    private List<Path> paths = new ArrayList<>();


    public Movement(BlockHeader header, byte[] data) {
        super(header, data);


        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        this.timestamp = buffer.getInt() & 0xffffffffL;

        int updates = buffer.getShort() & 0xffff;
        for (int i = 0; i < updates; i++) {
            paths.add(readPath(buffer));
        }

        assertEndOfBuffer(buffer);
    }

    private Path readPath(ByteBuffer buffer) {
        int numPoints = buffer.get() & 0xff;
        long entityId = buffer.getInt() & 0xffffffffL;

        if (numPoints % 2 == 1) {
            log.warn("[MOVEMENT] unk1 = " + buffer.get());
            numPoints--;
        }

        BitSet markers = new BitSet();
        if (numPoints > 2) {
            byte[] buf = new byte[((numPoints - 3) / 8) + 1];
            for (int i = 0; i < buf.length; i++) {
                buf[i] = buffer.get();
            }

            markers = BitSet.valueOf(buf);
        }

        int startX = buffer.getShort() & 0xffff;
        int startY = buffer.getShort() & 0xffff;

        List<WayPoint> waypoints = new ArrayList<>();
        for (int i = 0; i < (numPoints - 2); i += 2) {
            int x = markers.get(i) ? startX + (buffer.get() & 0xff) : buffer.getShort() & 0xffff;
            int y = markers.get(i+1) ? startY + (buffer.get() & 0xff) : buffer.getShort() & 0xffff;

            waypoints.add(new WayPoint(x, y));
        }

        return new Path(entityId, startX, startY, waypoints);
    }



    @Value
    public static class Path {
        private long entityId;
        private int startX;
        private int startY;
        private List<WayPoint> wayPoints;
    }

    @Value
    public static class WayPoint {
        private int x;
        private int y;
    }
}
