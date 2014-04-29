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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.rtmp.amf.AmfInputStream;
import net.boreeas.riotapi.rtmp.amf.AmfOutputStream;
import net.boreeas.riotapi.rtmp.packets.RtmpAbort;
import net.boreeas.riotapi.rtmp.packets.RtmpAckWindowSize;
import net.boreeas.riotapi.rtmp.packets.RtmpConnect;
import net.boreeas.riotapi.rtmp.packets.RtmpPacket;
import net.boreeas.riotapi.rtmp.packets.RtmpSetChunkSize;
import net.boreeas.riotapi.rtmp.packets.RtmpSetPeerBandwidth;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created on 4/18/2014.
 */
@Log
public abstract class RtmpClient {

    private static final byte RTMPS_VERSION = 3;

    private Random random = new Random();

    private String host;
    private int port;
    private boolean useSSL;
    private Socket socket;
    private AmfOutputStream out;
    private AmfInputStream in;
    private RtmpPacketReader reader;
    private RtmpPacketWriter writer;
    private Map<Integer, ChunkStream> chunkStreams = new HashMap<>();

    // "connect" invoke data
    @Setter @Getter private String targetApp;
    @Setter @Getter private String flashVer = "WIN 10,1,85,3";
    @Setter @Getter private String swfUrl;
    @Setter @Getter private String tcUrl;
    @Setter @Getter private boolean fpad;
    @Setter @Getter private int audioCodecs = 0x0FFF;
    @Setter @Getter private int videoCodecs = 0x00FF;
    @Setter @Getter private int videoFunction = 1;
    @Setter @Getter private String  pageUrl;
    @Setter @Getter private int objectEncoding = 3;

    private long timeOffset;
    private int nextTransactionId = 1;
    private int nextChunkStreamId = 3;
    private boolean useAmf3 = true;

    public RtmpClient(String host, int port, boolean useSSL) {
        this.host = host;
        this.port = port;
        this.useSSL = useSSL;
        this.tcUrl = (useSSL ? "rtmps://" : "rtmp://") + host + ":" + port + "/" + targetApp;
    }

    public RtmpClient(Shard shard) {
        this(shard.prodUrl, Shard.rtmpsPort, true);
        this.targetApp = Shard.rtmpsAppPath;
    }

    /**
     * Opens the connection, executes a handshake, and sends
     * the "connect" invoke
     */
    public void connect() throws IOException {
        if (useSSL) {
            socket = SSLSocketFactory.getDefault().createSocket(host, port);
        } else {
            socket = new Socket(host, port);
        }

        out = new AmfOutputStream(socket.getOutputStream());
        in = new AmfInputStream(socket.getInputStream());
        reader = new RtmpPacketReader(in);
        writer = new RtmpPacketWriter(out);

        doHandshake();
        onHandshakeCompleted();

        doConnect();

        // Start reader thread
        new Thread(() -> readPackets()).start();
    }


    private void doConnect() throws IOException {
        RtmpConnect.ConnectInfo ci = new RtmpConnect.ConnectInfo(targetApp, flashVer, swfUrl, tcUrl, fpad,
                audioCodecs, videoCodecs, videoFunction, pageUrl, objectEncoding);

        System.out.println("Sending connect " + ci);

        RtmpConnect connect = new RtmpConnect(ci, new FlexCommandMessage(FlexCommandMessage.PING));
        connect.setFmt(RtmpPacket.FMT_FULL_HEADER);
        connect.setChunkStreamId(3);
        connect.setTimestamp((int) getTimeDelta());

        writer.writePacket(connect);
    }

    private void doHandshake() throws IOException {
        /*
            Handshake
            C0 (version)        ---->
            C1 (random bytes)   ---->
                                <----   S0 (version)
                                <----   S1 (random bytes)
            C2 (copy S1)        ---->
                                <----   S2 (copy C1)
        */

        // C0
        out.write(RTMPS_VERSION);

        // C1
        byte[] c1Payload = new byte[1536];
        random.nextBytes(c1Payload);
        for (int i = 0; i < 8; i++) {
            c1Payload[i] = 0;
        }

        out.write(c1Payload);


        // Validate S0
        byte serverVersion = (byte) in.read();
        if (serverVersion != 3) {
            throw new RuntimeException("RTMPS version " + serverVersion + " is not supported");
        }

        // S1
        byte[] s1Payload = new byte[1536];
        in.read(s1Payload);

        timeOffset = System.currentTimeMillis() - ((s1Payload[0] << 24) | (s1Payload[1] << 16) | (s1Payload[2] << 8) | s1Payload[3]);

        // C2 (echo S1)
        int c2Timestamp = (int) getTimeDelta();
        s1Payload[4] = (byte) (c2Timestamp >> 24);
        s1Payload[5] = (byte) (c2Timestamp >> 16);
        s1Payload[6] = (byte) (c2Timestamp >>  8);
        s1Payload[7] = (byte) (c2Timestamp);


        // Validate S2
        byte[] s2Payload = new byte[1536];
        in.read(s2Payload);

        for (int i = 8; i < c1Payload.length; i++) {
            if (c1Payload[i] != s2Payload[i]) {
                throw new RuntimeException("S2 payload verification failed at offset " + i);
            }
        }
    }

    private void readPackets() {

        while (!socket.isClosed()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                break;
            }

            try {
                RtmpPacket packet = reader.readNextPacket();
                if (packet.getType() == RtmpSetChunkSize.TYPE) {
                    reader.setChunkSize(((RtmpSetChunkSize) packet).getChunksize());
                    writer.setChunkSize(((RtmpSetChunkSize) packet).getChunksize());
                } else if (packet.getType() == RtmpAckWindowSize.TYPE) {
                    throw new RuntimeException("Set Ack Window Size not supported");
                } else if (packet.getType() == RtmpSetPeerBandwidth.TYPE) {
                    log.warning("Bandwidth limiting is not supported");
                } else if (packet.getType() == RtmpAbort.TYPE) {
                    chunkStreams.remove(((RtmpAbort) packet).getChunkId());
                }

                int csId = packet.getChunkStreamId();
                int msId = packet.getMessageStreamId();
                if (getChunkStream(csId) != null && getChunkStream(csId).getMessageStream(msId) != null) {
                    getChunkStream(csId).getMessageStream(msId).onPacketReceived(packet);
                } else {
                    onUnhandledPacket(packet);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public long getTimeDelta() {
        return (System.currentTimeMillis() - timeOffset) & 0xFFFFFFFF;
    }

    public int nextTransactionId() {
        return nextTransactionId++;
    }

    private int nextChunkStreamId() {
        return nextChunkStreamId++;
    }

    public ChunkStream createChunkStream() {
        int id = nextChunkStreamId();
        chunkStreams.put(id, new ChunkStream(id, timeOffset, useAmf3, writer));
        return getChunkStream(id);
    }

    public ChunkStream getChunkStream(int id) {
        return chunkStreams.get(id);
    }

    public Collection<ChunkStream> getChunkStreams() {
        return Collections.unmodifiableCollection(chunkStreams.values());
    }

    protected abstract void onHandshakeCompleted();
    protected abstract void onUnhandledPacket(RtmpPacket packet);
}
