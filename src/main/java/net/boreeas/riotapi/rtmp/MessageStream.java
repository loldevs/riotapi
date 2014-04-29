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

import net.boreeas.riotapi.rtmp.packets.RtmpAbort;
import net.boreeas.riotapi.rtmp.packets.RtmpAckWindowSize;
import net.boreeas.riotapi.rtmp.packets.RtmpAcknowledgement;
import net.boreeas.riotapi.rtmp.packets.RtmpInvoke;
import net.boreeas.riotapi.rtmp.packets.RtmpPacket;
import net.boreeas.riotapi.rtmp.packets.RtmpSetChunkSize;
import net.boreeas.riotapi.rtmp.packets.RtmpSetPeerBandwidth;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created on 4/19/2014.
 */
public class MessageStream {
    private int id;
    private ChunkStream parent;
    private boolean useAmf3;
    private Set<Consumer<RtmpPacket>> listeners = new HashSet<>();

    public MessageStream(int id, ChunkStream parent, boolean useAmf3) {
        this.id = id;
        this.parent = parent;
        this.useAmf3 = useAmf3;
    }

    public void onPacketReceived(RtmpPacket packet) {
        listeners.forEach((listener) -> new Thread(() -> listener.accept(packet)).start());
    }

    public RtmpPacket send(RtmpPacket packet) throws IOException {
        packet.setMessageStreamId(id);
        parent.send(packet);
        return packet;
    }

    public RtmpAbort sendAbort(int chunkStreamId) throws IOException {
        return (RtmpAbort) send(new RtmpAbort(chunkStreamId));
    }

    public RtmpAcknowledgement sendAcknowledgement(int sequenceNum) throws IOException {
        return (RtmpAcknowledgement) send(new RtmpAcknowledgement(sequenceNum));
    }

    public RtmpAckWindowSize sendAckWindowSize(int maxBytes) throws IOException {
        return (RtmpAckWindowSize) send(new RtmpAckWindowSize(maxBytes));
    }

    public RtmpInvoke createInvoke(String command, int transactionId, Object... values) throws IOException {
        return (RtmpInvoke) send(new RtmpInvoke(command, transactionId, useAmf3, 0, values));
    }

    public RtmpSetChunkSize sendSetChunkSize(int chunkSize) throws IOException {
        return (RtmpSetChunkSize) send(new RtmpSetChunkSize(chunkSize));
    }

    public RtmpSetPeerBandwidth sendSetPeerBandwidth(int maxBytes, byte limitType) throws IOException {
        return (RtmpSetPeerBandwidth) send(new RtmpSetPeerBandwidth(maxBytes, limitType));
    }
}
