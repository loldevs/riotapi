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

package net.boreeas.riotapi.rtmp;

import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.rtmp.messages.control.SetChunkSize;
import net.boreeas.riotapi.rtmp.serialization.AmfWriter;
import net.boreeas.riotapi.rtmp.serialization.ObjectEncoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

/**
 * Created on 5/18/2014.
 */
@Log4j(topic = "Writer")
public class RtmpPacketWriter implements Runnable {

    private int CHUNK_SIZE = 128;

    private AmfWriter writer;
    private ObjectEncoding encoding;
    private Map<Integer, RtmpHeader> previousHeaders = new HashMap<>();
    private Map<Integer, RtmpPacket> previousPackets = new HashMap<>();
    private Consumer<IOException> onError;

    private BlockingQueue<RtmpPacket> packetQueue = new LinkedBlockingQueue<>();
    private volatile boolean interrupted;

    public RtmpPacketWriter(AmfWriter writer, ObjectEncoding encoding, Consumer<IOException> onError) {
        this.writer = writer;
        this.encoding = encoding;
        this.onError = onError;
    }

    @Override
    public void run() {
        try {
            while (!interrupted && !Thread.interrupted()) {
                write(packetQueue.take());  // Waits until a packet becomes available
            }
        } catch (IOException e) {
            if (!interrupted) {
                onError.accept(e);
            }
        } catch (InterruptedException ex) {
            log.warn("Writer thread interrupted");
        } catch (Exception ex) {
            onError.accept(new IOException(ex));
        } finally {
            try {
                writer.close();
            } catch (IOException e) {}
        }
    }

    public void interrupt() {
        this.interrupted = true;
    }


    public void write(RtmpEvent body, int streamId, int msgStreamId) {

        RtmpHeader header = new RtmpHeader();
        RtmpPacket packet = new RtmpPacket(header, body);

        header.setStreamId(streamId);
        header.setMsgStreamId(msgStreamId);
        header.setTimestamp(body.getTimeStamp());
        header.setMessageType(body.getType());

        if (body.getHeader() != null) {
            header.setTimestampRelative(body.getHeader().isTimestampRelative());
        }

        try {
            packetQueue.put(packet);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    private void write(RtmpPacket packet) throws IOException {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        packet.getBody().writeBody(new AmfWriter(bout));
        byte[] buffer = bout.toByteArray();

        RtmpHeader header = packet.getHeader();
        header.setPacketLength(buffer.length);

        RtmpHeader previousHeader = previousHeaders.get(header.getStreamId());
        previousHeaders.put(header.getStreamId(), header);
        previousPackets.put(header.getStreamId(), packet);

        writeHeader(header, previousHeader);
        boolean first = true; // Split off packets exceeding the chunk size
        for (int i = 0; i < header.getPacketLength(); i += CHUNK_SIZE) {
            if (!first) {
                // Continuation
                writeFmtAndStreamId(ChunkHeaderType.NO_HEADER.ordinal(), header.getStreamId());
            }

            int writeLen = i + CHUNK_SIZE > header.getPacketLength() ? header.getPacketLength() - i : CHUNK_SIZE;
            writer.write(buffer, i, writeLen);
            first = false;
        }

        if (packet.getBody() instanceof SetChunkSize) {
            CHUNK_SIZE = ((SetChunkSize) packet.getBody()).getChunkSize();
        }

        writer.flush();

    }

    private ChunkHeaderType getHeaderType(RtmpHeader header, RtmpHeader previousHeader) {
        if (previousHeader == null) {
            return ChunkHeaderType.FULL;
        }

        if (header.getMsgStreamId() != previousHeader.getMsgStreamId() || !header.isTimestampRelative()) {
            return ChunkHeaderType.FULL;
        }

        if (header.getPacketLength() != previousHeader.getPacketLength() || header.getMessageType() != previousHeader.getMessageType()) {
            return ChunkHeaderType.NO_MSG_STREAM_ID;
        }

        if (header.getTimestamp() != previousHeader.getTimestamp()) {
            return ChunkHeaderType.TIMESTAMP_ONLY;
        }

        return ChunkHeaderType.NO_HEADER;
    }

    private void writeFmtAndStreamId(int fmt, int streamId) throws IOException {
        fmt <<= 6;

        if (streamId <= 63) {
            writer.write(fmt | streamId); // This is going to blow up for stream id 0 or 1
        } else if (streamId <= 320) {
            writer.write(fmt);
            writer.write(streamId - 64);
        } else {
            streamId -= 64;

            writer.write(fmt | 1);  // 1 marks double extended id
            writer.write(streamId);
            writer.write(streamId >> 8);
        }
    }

    private void writeHeader(RtmpHeader header, RtmpHeader previous) throws IOException {
        ChunkHeaderType headerType = getHeaderType(header, previous);
        writeFmtAndStreamId(headerType.ordinal(), header.getStreamId());

        int timeStamp = header.getTimestamp() >= 0xffffff ? 0xffffff : header.getTimestamp();

        switch (headerType) {
            case FULL:
                writer.writeUint24(timeStamp);
                writer.writeUint24(header.getPacketLength());
                writer.write(header.getMessageType().id);
                writer.writeLittleEndianInt(header.getMsgStreamId());
                break;

            case NO_MSG_STREAM_ID:
                writer.writeUint24(timeStamp);
                writer.writeUint24(header.getPacketLength());
                writer.write(header.getMessageType().id);
                break;

            case TIMESTAMP_ONLY:
                writer.writeUint24(timeStamp);
                break;
        }

        if (timeStamp == 0xffffff) {
            writer.writeInt(header.getTimestamp());
        }
    }

    public void close() {
        try {
            interrupt();
            writer.close();
        } catch (IOException e) {}
    }
}
