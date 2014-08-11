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

import lombok.Setter;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.Util;
import net.boreeas.riotapi.rtmp.messages.control.*;
import net.boreeas.riotapi.rtmp.serialization.AmfReader;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


/**
 * Created on 5/18/2014.
 */
@Log4j(topic="Reader")
public class RtmpPacketReader implements Runnable {

    private AmfReader reader;
    private int chunkSize = 128;
    private volatile boolean interrupted;
    @Setter private Consumer<Exception> onError;
    @Setter private Consumer<RtmpEvent> onPacket;

    private Map<Integer, RtmpHeader> headers = new HashMap<>();
    private Map<Integer, RtmpPacket> packets = new HashMap<>();

    public RtmpPacketReader(AmfReader reader, Consumer<Exception> onError, Consumer<RtmpEvent> onPacket) {
        this.reader = reader;
        this.onError = onError;
        this.onPacket = onPacket;
    }

    public RtmpPacketReader(AmfReader reader) {
        this(reader, e -> {}, p -> {});
    }

    public void run() {
        try {
            while (!interrupted && !Thread.interrupted()) {

                readPacket();
            }
        } catch (IOException ex) {

            if (!interrupted) {
                onError.accept(ex);
            }
        } catch (Exception ex) {

            onError.accept(ex);
        } finally {

            try {
                reader.close();
            } catch (IOException e) {}
        }
    }

    public void interrupt() {
        this.interrupted = true;
    }

    public void close() {
        try {
            interrupt();
            reader.close();
        } catch (IOException e) {}
    }

    private void errorClose(Exception ex) {
        onError.accept(ex);
        interrupt();
    }

    private void readPacket() throws IOException {
        RtmpHeader header = readHeader();
        headers.put(header.getStreamId(), header);

        RtmpPacket packet;
        if ((packet = packets.get(header.getStreamId())) == null) {
            packet = new RtmpPacket(header);
            packets.put(header.getStreamId(), packet);
        }

        int remainingLength = packet.getLength() + (header.getTimestamp() >= 0xffffff ? 4 : 0) - packet.getCurrentPos();
        int bufferSize = Math.min(remainingLength, chunkSize);
        byte[] buffer = new byte[bufferSize];

        int count = 0;
        do {
            count += reader.read(buffer, count, bufferSize - count);
        } while (count != bufferSize);

        packet.append(buffer);

        if (packet.isComplete()) {
            packets.remove(header.getStreamId());
            RtmpEvent event = parsePacket(packet);

            onPacket.accept(event);

            if (event instanceof SetChunkSize) {
                this.chunkSize = ((SetChunkSize)event).getChunkSize();
            }

            if (event instanceof AbortMessage) {
                packets.remove(((AbortMessage)event).getAbortStreamId());
            }
        }
    }


    private RtmpHeader readHeader() throws IOException {
        int headerByte = reader.read() & 0xff;

        if (headerByte == 0b11111111) {
            errorClose(new EOFException());
        }

        int chunkStreamId = getChunkStreamId(headerByte);

        ChunkHeaderType headerType = ChunkHeaderType.values()[headerByte >> 6];
        RtmpHeader header = new RtmpHeader(null, 0, headerType.ordinal(), chunkStreamId, 0, 0, headerType != ChunkHeaderType.FULL);

        RtmpHeader previous;
        if ((previous = headers.get(chunkStreamId)) == null && headerType != ChunkHeaderType.FULL) {
            // If previous header is null, and we expect part of the header from a previous version, default to current
            previous = header;
        }

        log.trace("On chunk stream " + chunkStreamId + " (from header byte " + Integer.toBinaryString(headerByte) + "): Got header type " + headerType);

        switch (headerType) {
            case FULL:
                header.setTimestamp(reader.readUint24());
                header.setPacketLength(reader.readUint24());
                header.setMessageType(MessageType.getById(reader.read()));
                header.setMsgStreamId(reader.readIntLittleEndian());
                break;

            case NO_MSG_STREAM_ID:
                header.setTimestamp(reader.readUint24());
                header.setPacketLength(reader.readUint24());
                header.setMessageType(MessageType.getById(reader.read()));
                header.setMsgStreamId(previous.getMsgStreamId());
                break;

            case TIMESTAMP_ONLY:
                header.setTimestamp(reader.readUint24());
                header.setPacketLength(previous.getPacketLength());
                header.setMessageType(previous.getMessageType());
                header.setMsgStreamId(previous.getMsgStreamId());
                break;

            case NO_HEADER:
                header.setTimestamp(previous.getTimestamp());
                header.setPacketLength(previous.getPacketLength());
                header.setMessageType(previous.getMessageType());
                header.setMsgStreamId(previous.getMsgStreamId());
                header.setTimestampRelative(previous.isTimestampRelative());
                break;
        }

        if (header.getTimestamp() == 0xffffff) {
            header.setTimestamp(reader.readInt());
        }

        return header;
    }

    private int getChunkStreamId(int header) throws IOException {
        int id = header & 0x3f; // Clear header type bytes

        if (id == 0) {
            return reader.read() + 64;
        }

        if (id == 1) {
            // Suddenly, littleendian
            return 64 + (reader.read() | (reader.read() << 8));
        }

        return id;
    }


    private RtmpEvent parsePacket(RtmpPacket packet) throws IOException {
        if (packet.getHeader().getMessageType() == null) {
            log.debug("Error: Unset message type");
            byte[] buffer = new byte[1024];
            while (reader.read(buffer) != -1) {
                for (String line: Util.hexdump(buffer)) {
                    log.debug(line);
                }
            }

            log.debug("End of buffer dump");
        }

        switch (packet.getHeader().getMessageType()) {
            // Control message
            case SET_CHUNK_SIZE:
                return parsePacket(packet, r -> new SetChunkSize(r.readInt()));
            case ABORT_MESSAGE:
                return parsePacket(packet, r -> new AbortMessage(r.readInt()));
            case ACKNOWLEDGEMENT:
                return parsePacket(packet, r -> new Acknowledgement(r.readInt()));
            case USER_CONTROL_MESSAGE:
                return parsePacket(packet, r -> {
                    int type = r.readUnsignedShort();
                    List<Integer> values = new ArrayList<>();
                    while (r.available() >= 4) {
                        values.add(r.readInt());
                    }
                    return new UserControlMessage(UserControlMessage.Type.values()[type], values);
                });
            case WINDOW_ACKNOWLEDGEMENT_SIZE:
                return parsePacket(packet, r -> new WindowAcknowledgementSize(r.readInt()));
            case SET_PEER_BANDWIDTH:
                return parsePacket(packet, r -> new SetPeerBandwidth(r.readInt(), r.read()));

            // Multimedia messages
            case AUDIO:
                return parsePacket(packet, r -> new AudioData(packet.getBuffer()));
            case VIDEO:
                return parsePacket(packet, r -> new VideoData(packet.getBuffer()));

            // Amf messages
            case DATA_AMF3:
                return parsePacket(packet, r -> parseInvokeOrData(r, new NotificationAmf3()));
            case SHARED_OBJ_AMF3:
                return null; // FIXME
            case INVOKE_AMF3:
                return parsePacket(packet, r -> {
                    int val = r.read(); // Skip leading 0
                    return parseInvokeOrData(r, new InvokeAmf3());
                });
            case DATA_AMF0:
                return parsePacket(packet, r -> parseInvokeOrData(r, new NotificationAmf0()));
            case SHARED_OBJ_AMF0:
                return null; // FIXME
            case INVOKE_AMF0:
                return parsePacket(packet, r -> parseInvokeOrData(r, new InvokeAmf0()));
            case AGGREGATE:
                return null; // FIXME
        }

        return null;
    }

    private RtmpEvent parsePacket(RtmpPacket packet, EventCreator creator) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(packet.getBuffer());
        AmfReader reader = new AmfReader(in, this.reader);

        RtmpEvent event = creator.create(reader);
        event.setHeader(packet.getHeader());

        return event;
    }

    private RtmpEvent parseInvokeOrData(AmfReader reader, Command command) throws IOException {
        String methodName = reader.decodeAmf0();

        Double invokeId = reader.decodeAmf0();
        command.setInvokeId(invokeId == null ? 0 : invokeId.intValue());
        command.setConnectionParams(reader.decodeAmf0());

        List<Object> params = new ArrayList<>();
        while (reader.available() > 0) {
            Object obj = reader.decodeAmf0();
            params.add(obj);
            //break;
        }

        command.setMethod(new Command.Method(methodName, params.toArray()));
        return command;
    }


    private @FunctionalInterface interface EventCreator {
        public RtmpEvent create(AmfReader reader) throws IOException;
    }

}
