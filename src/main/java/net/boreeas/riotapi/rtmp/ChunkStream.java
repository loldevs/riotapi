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

package net.boreeas.riotapi.rtmp;

import net.boreeas.riotapi.rtmp.packets.RtmpPacket;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 4/19/2014.
 */
public class ChunkStream {
    private static int nextMessageStreamId = 0;

    private int id;
    private long timeOffset;
    private boolean useAmf3;
    private RtmpPacketWriter writer;

    private Map<Integer, MessageStream> messageStreams = new HashMap<>();

    public ChunkStream(int id, long timeOffset, boolean useAmf3, RtmpPacketWriter writer) {
        this.id = id;
        this.timeOffset = timeOffset;
        this.useAmf3 = useAmf3;
        this.writer = writer;
    }

    private int nextMessageStreamId() {
        return nextMessageStreamId++;
    }

    private long getTimeDelta() {
        return (System.currentTimeMillis() - timeOffset) & 0xFFFFFFFF;
    }

    public MessageStream createMessageStream() {
        int id = nextMessageStreamId();
        this.messageStreams.put(id, new MessageStream(id, this, useAmf3));
        return getMessageStream(id);
    }

    public MessageStream getMessageStream(int id) {
        return messageStreams.get(id);
    }

    public Collection<MessageStream> getMessageStreams() {
        return Collections.unmodifiableCollection(messageStreams.values());
    }

    public void send(RtmpPacket packet) throws IOException {
        packet.setChunkStreamId(id);
        packet.setTimestamp((int) getTimeDelta());
        writer.writePacket(packet);
    }
}
