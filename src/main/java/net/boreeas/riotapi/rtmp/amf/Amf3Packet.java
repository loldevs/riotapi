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

package net.boreeas.riotapi.rtmp.amf;

import lombok.Getter;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an AMF3 packet
 * Created on 4/15/2014.
 */
public class Amf3Packet {
    @Getter private int version;
    private Amf3Header[] headers;
    private Amf3Message[] messages;

    Amf3Packet() {
    }

    public int getHeaderCount() {
        return headers.length;
    }

    public int getMessageCount() {
        return messages.length;
    }

    /**
     * Get a header from this packet
     * @param i The index of the header
     * @return The <i>i</i>th header
     */
    public Amf3Header getHeader(int i) {
        return headers[i];
    }

    /**
     * Get a message from this packet
     * @param i The index of the message
     * @return The <i>i</i>th message
     */
    public Amf3Message getMessage(int i) {
        return messages[i];
    }







    /**
     * Parse an AMF3 packet from an input stream
     * @param in The stream to read from
     * @return The AMF3 packet from the stream
     * @throws IOException
     */
    public static Amf3Packet fromInputStream(DataInputStream in) throws IOException {
        Amf3Packet packet = new Amf3Packet();

        packet.version = in.readUnsignedShort();
        packet.headers = new Amf3Header[in.readUnsignedShort()];
        for (int i = 0; i < packet.headers.length; i++) {
            packet.headers[i] = Amf3Header.fromInputStream(in);
        }

        packet.messages = new Amf3Message[in.readUnsignedShort()];
        for (int i = 0; i < packet.messages.length; i++) {
            packet.headers[i] = Amf3Header.fromInputStream(in);
        }

        return packet;
    }

    /**
     * A builder for AMF3 packets
     */
    public static class AMF3PacketBuilder {
        private int version;
        private List<Amf3Message> msgs = new ArrayList<>();
        private List<Amf3Header> headers = new ArrayList<>();

        public AMF3PacketBuilder(int version) {
            this.version = version;
        }

        public AMF3PacketBuilder addMsg(Amf3Message msg) {
            msgs.add(msg);
            return this;
        }

        public AMF3PacketBuilder addHeader(Amf3Header header) {
            headers.add(header);
            return this;
        }

        public Amf3Packet packet() {
            Amf3Packet packet = new Amf3Packet();

            packet.version = version;
            packet.messages = new Amf3Message[msgs.size()];
            msgs.toArray(packet.messages);

            packet.headers = new Amf3Header[headers.size()];
            headers.toArray(packet.headers);

            return packet;
        }
    }
}
