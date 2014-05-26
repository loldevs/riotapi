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

package net.boreeas.riotapi.rtmp.p2;

import lombok.Getter;
import net.boreeas.riotapi.rtmp.p2.messages.control.Command;
import net.boreeas.riotapi.rtmp.p2.messages.control.Invoke;
import net.boreeas.riotapi.rtmp.p2.messages.control.InvokeAmf0;
import net.boreeas.riotapi.rtmp.p2.serialization.AmfReader;
import net.boreeas.riotapi.rtmp.p2.serialization.AmfWriter;
import net.boreeas.riotapi.rtmp.p2.serialization.AnonymousAmfObject;
import net.boreeas.riotapi.rtmp.p2.serialization.ObjectEncoding;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 5/24/2014.
 */
public abstract class RtmpClient {

    public static final int RTMP_VERSION = 3;
    private static final int PAYLOAD_SIZE = 1536;
    public static final int INVOKE_STREAM = 3;
    public static final int DEFAULT_MSG_STREAM = 0;

    @Getter private String host;
    @Getter private int port;
    @Getter private boolean useSSL;
    private Socket socket;


    private RtmpPacketReader reader;
    private Thread readerThread;
    private RtmpPacketWriter writer;
    @Getter private boolean isConnected = false;

    private long timeOffset;
    private AtomicInteger invokeId = new AtomicInteger(3);


    public RtmpClient(String host, int port, boolean useSSL) throws URISyntaxException {
        this.host = host;
        this.port = port;
        this.useSSL = useSSL;
    }

    public abstract void onReadException(Exception ex);
    public abstract void onAsyncWriteException(IOException ex);
    public abstract void onPacket(RtmpPacket packet);

    public int getTimeDelta() {
        return (int) ((System.currentTimeMillis() - this.timeOffset) & 0xFFFFFFFF);
    }

    public void disconnect() {
        isConnected = false;
        reader.interrupt();
        writer.close();
    }

    public void connect() throws IOException {
        this.socket = useSSL ? SSLSocketFactory.getDefault().createSocket(host, port) : new Socket(host, port);

        AmfWriter writer = new AmfWriter(socket.getOutputStream());
        AmfReader reader = new AmfReader(socket.getInputStream());
        doHandshake(writer, reader);

        this.reader = new RtmpPacketReader(reader, this::onReadException, this::onPacket);
        this.writer = new RtmpPacketWriter(writer, ObjectEncoding.AMF3);
        readerThread = new Thread(this.reader);
        readerThread.setDaemon(true);
        readerThread.start();


    }

    private void doHandshake(AmfWriter writer, AmfReader reader) throws IOException {
        Random random = new Random();

        writer.write(RTMP_VERSION);

        // C1

        byte[] c1Payload = new byte[PAYLOAD_SIZE];
        random.nextBytes(c1Payload);
        for (int i = 0; i < 8; i++) c1Payload[i] = 0;

        writer.write(c1Payload);

        int serverVersion = reader.read();
        if (serverVersion != RTMP_VERSION) {
            throw new RtmpVersionMismatchException(serverVersion, RTMP_VERSION);
        }

        // S1

        byte[] buffer = new byte[PAYLOAD_SIZE];
        reader.read(buffer);

        long zeroTime = buffer[0] << 24 | buffer[1] << 16 | buffer[2] << 8 | buffer[3];
        this.timeOffset = System.currentTimeMillis() - zeroTime;

        // C2

        int c2TimeStamp = getTimeDelta();
        buffer[4] = (byte) (c2TimeStamp >> 24);
        buffer[5] = (byte) (c2TimeStamp >> 16);
        buffer[6] = (byte) (c2TimeStamp >> 8);
        buffer[7] = (byte) c2TimeStamp;

        writer.write(buffer);

        // S2

        reader.read(buffer);
        for (int i = 8; i < buffer.length; i++) {
            if (buffer[i] != c1Payload[i]) {
                throw new ProtocolException("Handshake payload mismatch at " + i);
            }
        }
    }



    public void sendInvoke(String command, Object... args) throws IOException {
        writer.write(createAmf0InvokeSkeleton(command, args), INVOKE_STREAM, DEFAULT_MSG_STREAM);
    }

    public void sendAsyncInvoke(String command, Object... args) {
        writer.writeAsync(createAmf0InvokeSkeleton(command, args), INVOKE_STREAM, DEFAULT_MSG_STREAM, this::onAsyncWriteException);
    }

    public int getNextInvokeId() {
        return invokeId.getAndIncrement();
    }

    public Invoke createAmf0InvokeSkeleton(String command, Object... args) {
        Invoke result = new InvokeAmf0();
        result.setMethod(new Command.Method(command, args));
        result.setInvokeId(getNextInvokeId());

        return result;
    }

    private void sendConnectInvoke() throws IOException {
        Invoke invoke = createAmf0InvokeSkeleton("connect", new Object[0]);

        AnonymousAmfObject connParams = new AnonymousAmfObject();
        connParams.set("pageUrl", pageUrl);
        connParams.set("objectEncoding", (double) objectEncoding.ordinal());
        connParams.set("capabilities", 15);
        connParams.set("audioCodecs", 1639);
        connParams.set("flashVer", "WIN 9,0,115,0");
        connParams.set("swfUrl", swfUrl);
        connParams.set("videoFunction", 1);
        connParams.set("fpad", false);
        connParams.set("videoCodecs", 252);
        connParams.set("tcUrl", tcUrl);
        connParams.set("app", null);

        invoke.setConnectionParams(connParams);

        writer.write(invoke, INVOKE_STREAM, DEFAULT_MSG_STREAM);
    }
}
