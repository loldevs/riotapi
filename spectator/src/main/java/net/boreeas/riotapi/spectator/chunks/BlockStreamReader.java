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
 *
 * @author Malte Schütze
 */
public class BlockStreamReader {
    /**
     * The last read block, or <code>null</code> if no block has been read yet.
     */
    @Getter
    private Block last;
    private final ByteBuffer buffer;
    private BlockFactory factory;

    public BlockStreamReader(byte[] data) {
        this(data, DefaultBlockFactory.INSTANCE);
    }

    public BlockStreamReader(byte[] data, BlockFactory factory) {
        buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        this.factory = factory;
    }

    /**
     * Reads the next block from the buffer.
     *
     * @return The next block.
     */
    public Block next() {

        BlockHeader header = getBlockHeader();

        if (header.getContentLength() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid content length: " + header.getContentLength());
        }
        byte[] content = new byte[(int) header.getContentLength()];
        buffer.get(content);

        Block next = factory.getBlock(header, content);

        last = next;
        return next;
    }


    private BlockHeader getBlockHeader() {

        Flags flags = new Flags(buffer.get());

        long timestamp;
        if (flags.hasFlag(Flags.RELATIVE_TIME)) {
            if (last == null) {
                throw new IllegalStateException("Missing previous block");
            }
            timestamp = last.getHeader().getTimestamp() + (buffer.get() & 0xff);
        } else {
            timestamp = (long) (buffer.getFloat() * 1000);
        }

        long contentLength;
        if (flags.hasFlag(Flags.SHORT_CONTENT_LENGTH)) {
            contentLength = buffer.get() & 0xFF;
        } else {
            contentLength = ((long) buffer.getInt()) & 0xFFFFFFFFL;
        }

        int blockType;
        if (flags.hasFlag(Flags.NO_BLOCKTYPE)) {
            if (last == null) {
                throw new IllegalStateException("Missing previous block");
            }
            blockType = last.getHeader().getType();
        } else {
            blockType = buffer.get() & 0xFF;
        }

        long blockParam;
        if (flags.hasFlag(Flags.SHORT_BLK_PARAM)) {
            blockParam = buffer.get() & 0xFF;
        } else {
            blockParam = buffer.getInt() & 0xFFFFFFFFL;
        }

        return new BlockHeader(flags, timestamp, blockType, contentLength, blockParam);
    }

    /**
     * @return Whether there are more blocks in this stream.
     */
    public boolean hasNext() {
        return buffer.hasRemaining();
    }
}
