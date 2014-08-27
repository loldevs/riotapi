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

import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Reads consecutive blocks from a buffer
 * @author Malte Schütze
 */
public class BlockStreamReader {
    /**
     * The last read block, or <code>null</code> if no block has been read yet.
     */
    @Getter private Block last;
    private final ByteBuffer buffer;

    public BlockStreamReader(byte[] data) {
        buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Reads the next block from the buffer.
     * @return The next block.
     */
    public Block next() {

        BlockHeader header = getBlockHeader();

        byte[] content = new byte[header.getContentLength()];
        buffer.get(content);

        Block next = new Block(header, content);

        last = next;
        return next;
    }

    private BlockHeader getBlockHeader() {

        Flags flags = new Flags(buffer.get());

        long timestamp = flags.hasFlag(Flags.RELATIVE_TIME)
                ? last.getHeader().getTimestamp() + buffer.get()
                : (long) (buffer.getFloat() * 1000);

        int contentLength = flags.hasFlag(Flags.SHORT_CONTENT_LENGTH) ? buffer.get() : buffer.getInt();
        int blockType = buffer.get() & 0xFF;
        int blockParam = flags.hasFlag(Flags.SHORT_BLK_PARAM) ? buffer.get() : buffer.getInt();

        return new BlockHeader(flags, timestamp, blockType, contentLength, blockParam);
    }

    /**
     * @return Whether there are more blocks in this stream.
     */
    public boolean hasNext() {
        return buffer.hasRemaining();
    }
}
