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
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.rtmp.messages.*;
import net.boreeas.riotapi.rtmp.messages.control.*;
import net.boreeas.riotapi.rtmp.serialization.AmfReader;
import net.boreeas.riotapi.rtmp.serialization.AmfWriter;
import net.boreeas.riotapi.rtmp.serialization.AnonymousAmfObject;
import net.boreeas.riotapi.rtmp.serialization.ObjectEncoding;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created on 5/24/2014.
 */
@Log4j
public abstract class RtmpClient {

    public static final int RTMP_VERSION = 3;
    private static final int PAYLOAD_SIZE = 1536;
    public static final int INVOKE_STREAM = 3;
    public static final int DEFAULT_MSG_STREAM = 0;

    // Connection params
    @Getter private String host;
    @Getter private int port;
    @Getter private boolean useSSL;
    private Socket socket;

    // Amf I/O
    private RtmpPacketReader reader;
    private Thread readerThread;
    private RtmpPacketWriter writer;
    @Getter @Setter private ObjectEncoding objectEncoding = ObjectEncoding.AMF3;

    // Connection data
    @Getter private boolean isConnected = false;
    private long timeOffset;
    private AtomicInteger invokeId = new AtomicInteger(1);
    private String clientId;

    // Async await
    private Map<Integer, InvokeCallback> callbacks = new HashMap<>();


    public RtmpClient(String host, int port, boolean useSSL) throws URISyntaxException {
        this.host = host;
        this.port = port;
        this.useSSL = useSSL;
    }

    public abstract void onReadException(Exception ex);
    public abstract void onAsyncWriteException(IOException ex);
    public abstract void extendedOnPacket(RtmpEvent packet);
    protected void onError(Exception ex) {
        log.error("Unknown exception occurred", ex);
        disconnect();
        releaseCallbacks(ex);
        onReadException(ex);
    }

    // Release waiting threads if we fail for some reason
    private void releaseCallbacks(Object o) {
        synchronized (callbacks) {
            for (Map.Entry<Integer, InvokeCallback> cb: callbacks.entrySet()) {
                cb.getValue().release(o);
            }
        }
    }

    public int getTimeDelta() {
        return (int) ((System.currentTimeMillis() - this.timeOffset) & 0xFFFFFFFF);
    }

    public InvokeCallback getInvokeCallback(int invokeId) {
        synchronized (callbacks) {
            if (!callbacks.containsKey(invokeId)) {
                callbacks.put(invokeId, new InvokeCallback());
            }
        }

        return callbacks.get(invokeId);
    }

    public Object waitForInvokeReply(int id) throws InterruptedException {
        return getInvokeCallback(id).waitForReply();
    }

    public void onPacket(RtmpEvent packet) {

        if (packet instanceof Command) {
            onInvoke((Command) packet);

        } else if (packet instanceof UserControlMessage) {
            UserControlMessage ucm = (UserControlMessage) packet;

            if (ucm.getControlMessageType() == UserControlMessage.Type.PING_REQUEST) {
                writeProtocolControlMessageAsync(
                        new UserControlMessage(UserControlMessage.Type.PING_RESPONSE, ucm.getValues())
                );
            } else {
                log.info("Unknown UserControlMessage: " + ucm.getControlMessageType() + "/" + ucm.getValues());
            }
        } else if (packet instanceof SetPeerBandwidth) {
            writeProtocolControlMessageAsync(new WindowAcknowledgementSize(((SetPeerBandwidth) packet).getWidth()));
        }

        extendedOnPacket(packet);
    }

    private void onInvoke(Command cmd) {
        Command.Method method = cmd.getMethod();
        Object params = method.getParams().length == 1 ? method.getParams()[0] : method.getParams();

        int invokeId = cmd.getInvokeId();
        InvokeCallback callback = getInvokeCallback(invokeId);

        Object result = params;
        if (method.getName().equals("_result") && params instanceof AcknowledgeMessage) {

            result = ((AcknowledgeMessage) params).getBody();
        } else if (method.getName().equals("_error")) {

            if (params instanceof ErrorMessage) {
                result = new InvokeException((ErrorMessage) params);
            } else {
                result = new InvokeException();
            }
        } else if (method.getName().equals("receive")) {
            AsyncMessage async = (AsyncMessage) params;

            String subtopic = Objects.toString(async.getHeaders().get(AsyncMessage.SUBTOPIC));
            String clientId = async.getClientId();
            Object body = async.getBody();

            result = new AsyncMessageEvent(clientId, subtopic, body);
        } else if (method.getName().equals("onstatus")) {

            log.info("Onstatus: " + Objects.toString(method.getParams()) + "/" + method.getStatus() + "/Success=" + method.isSuccess());
        } else {

            log.info("Unknown command: " + method.getName() + "/" + Arrays.toString(method.getParams()) + "/" + method.getStatus() + "/Success=" + method.isSuccess()
            + "\n\t\t\t" + params.getClass() + " <- " + method.getClass());
        }

        if (callback != null) {
            callback.release(result);
        }
    }

    public void disconnect() {
        isConnected = false;
        reader.interrupt();
        writer.close();
    }

    public void connect() throws IOException, InterruptedException {
        log.info("Connecting to " + host + ":" + port);
        this.socket = useSSL ? SSLSocketFactory.getDefault().createSocket(host, port) : new Socket(host, port);

        //AmfWriter writer = new AmfWriter(new DumpingOutputStream(socket.getOutputStream()));
        //AmfReader reader = new AmfReader(new DumpingInputStream(socket.getInputStream()));
        AmfWriter writer = new AmfWriter(socket.getOutputStream());
        AmfReader reader = new AmfReader(socket.getInputStream());
        doHandshake(writer, reader);

        this.reader = new RtmpPacketReader(reader, this::onError, this::onPacket);
        this.writer = new RtmpPacketWriter(writer, ObjectEncoding.AMF3);
        readerThread = new Thread(this.reader, "RtmpClient reader thread");
        readerThread.setDaemon(true);
        readerThread.start();

        try {
            int id = sendConnectInvoke(null, null, (useSSL ? "rtmps://" : "rtmp://") + host + ":" + port);
            System.out.println("Connect invoke id = " + id);
            Object reply = waitForInvokeReply(id);
            
            if (reply instanceof FlexMessage) {
                this.clientId = "" + ((FlexMessage) reply).getClientId();
            }

            isConnected = true;

        } catch (InterruptedException ex) {
            disconnect();
        }
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
        int read = reader.read(buffer);
        if (read != buffer.length) throw new ProtocolException("Incomplete buffer at S2");

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

        read = reader.read(buffer);
        if (read != buffer.length) throw new ProtocolException("Incomplete buffer at S2");

        for (int i = 8; i < buffer.length; i++) {
            if (buffer[i] != c1Payload[i]) {
                throw new ProtocolException("Handshake payload mismatch at " + i);
            }
        }
    }



    public int sendInvoke(String service, Object... args) throws IOException {
        Invoke invoke = createAmf0InvokeSkeleton(service, args);
        writer.write(invoke, INVOKE_STREAM, DEFAULT_MSG_STREAM);
        return invoke.getInvokeId();
    }

    public int sendAsyncInvoke(String service, Object... args) {
        Invoke invoke = createAmf0InvokeSkeleton(service, args);
        writer.writeAsync(invoke, INVOKE_STREAM, DEFAULT_MSG_STREAM, this::onAsyncWriteException);
        return invoke.getInvokeId();
    }

    public int sendRpc(String endpoint, String destination, String method, Object... args) throws IOException {
        RemotingMessage message = createRemotingMessage(endpoint, destination, method, args);
        Invoke invoke = createAmf3InvokeSkeleton(null, message);

        writer.write(invoke, INVOKE_STREAM, DEFAULT_MSG_STREAM);
        return invoke.getInvokeId();
    }

    public int sendAsyncRpc(String endpoint, String destination, String method, Object... args) throws IOException {
        RemotingMessage message = createRemotingMessage(endpoint, destination, method, args);
        Invoke invoke = createAmf3InvokeSkeleton(null, message);

        writer.writeAsync(invoke, INVOKE_STREAM, DEFAULT_MSG_STREAM, this::onAsyncWriteException);
        return invoke.getInvokeId();
    }

    private RemotingMessage createRemotingMessage(String endpoint, String destination, String method, Object[] args) {
        if (objectEncoding != ObjectEncoding.AMF3) {
            throw new IllegalStateException("RPC requires AMF3");
        }

        RemotingMessage message = new RemotingMessage(null, method);
        message.setDestination(destination);
        message.setBody(args);
        message.getHeaders().put(FlexMessage.ENDPOINT, endpoint);
        message.getHeaders().put(FlexMessage.LOCAL_CLIENT_ID, clientId == null ? "nil" : clientId);
        return message;
    }

    public int getNextInvokeId() {
        return invokeId.getAndIncrement();
    }

    public Invoke createAmf3InvokeSkeleton(String command, Object... args) {
        Invoke invoke = new InvokeAmf3();
        invoke.setInvokeId(getNextInvokeId());
        invoke.setMethod(new Command.Method(command, args));

        return invoke;
    }

    public Invoke createAmf0InvokeSkeleton(String command, Object... args) {
        Invoke result = new InvokeAmf0();
        result.setMethod(new Command.Method(command, args));
        result.setInvokeId(getNextInvokeId());

        return result;
    }

    private int sendConnectInvoke(String pageUrl, String swfUrl, String tcUrl) throws IOException {
        Invoke invoke = createAmf0InvokeSkeleton("connect");

        AnonymousAmfObject connParams = new AnonymousAmfObject();
        connParams.set("pageUrl", pageUrl);
        connParams.set("objectEncoding", objectEncoding == ObjectEncoding.AMF0 ? 0.0 : 3.0);
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
        return invoke.getInvokeId();
    }




    private CommandMessage getExtendedCmdMsg(String endpoint, String destination, String subtopic, String clientId,
                                             CommandMessage.Operation op) {

        CommandMessage msg = getCommandMessage(destination, clientId, op);
        msg.getHeaders().put(FlexMessage.ENDPOINT, endpoint);
        msg.getHeaders().put(FlexMessage.LOCAL_CLIENT_ID, clientId);
        msg.getHeaders().put(AsyncMessage.SUBTOPIC, subtopic);
        return msg;
    }

    private CommandMessage getCommandMessage(String destination, String clientId, CommandMessage.Operation op) {

        CommandMessage msg = new CommandMessage();
        msg.setClientId(clientId);
        msg.setCorrelationId(null);
        msg.setOperation(op);
        msg.setDestination(destination);
        return msg;
    }


    public int subscribe(String endpoint, String destination, String subtopic, String clientId) throws IOException {
        return sendInvoke(null, getExtendedCmdMsg(endpoint, destination, subtopic, clientId, CommandMessage.Operation.SUBSCRIBE));
    }

    public int subscribeAsync(String endpoint, String destination, String subtopic, String clientId) {
        return sendAsyncInvoke(null, getExtendedCmdMsg(endpoint, destination, subtopic, clientId, CommandMessage.Operation.SUBSCRIBE));
    }



    public int unsubscribe(String endpoint, String destination, String subtopic, String clientId) throws IOException {
        return sendInvoke(null, getExtendedCmdMsg(endpoint, destination, subtopic, clientId, CommandMessage.Operation.UNSUBSCRIBE));
    }

    public int unsubscribeAsync(String endpoint, String destination, String subtopic, String clientId) {
        return sendAsyncInvoke(null, getExtendedCmdMsg(endpoint, destination, subtopic, clientId, CommandMessage.Operation.UNSUBSCRIBE));
    }


    public int login(String username, String password) throws IOException {
        CommandMessage msg = getCommandMessage("", clientId, CommandMessage.Operation.LOGIN);
        msg.setBody(Base64.getEncoder().encode(String.format("%s:%s", username, password).getBytes(Charset.forName("UTF-8"))));
        return sendInvoke(null, msg);
    }

    public int loginAsync(String username, String password) {
        CommandMessage msg = getCommandMessage("", clientId, CommandMessage.Operation.LOGIN);
        msg.setBody(Base64.getEncoder().encode(String.format("%s:%s", username, password).getBytes(Charset.forName("UTF-8"))));
        return sendAsyncInvoke(null, msg);
    }


    public int logout() throws IOException {
        return sendInvoke(null, getCommandMessage("", clientId, CommandMessage.Operation.LOGOUT));
    }

    public int logoutAsync() {
        return sendAsyncInvoke(null, getCommandMessage("", clientId, CommandMessage.Operation.LOGOUT));
    }


    public int ping() throws IOException {
        return sendInvoke(null, getCommandMessage("", clientId, CommandMessage.Operation.CLIENT_PING));
    }

    public int pingAsync() {
        return sendAsyncInvoke(null, getCommandMessage("", clientId, CommandMessage.Operation.CLIENT_PING));
    }



    public void setChunkSize(int size) throws IOException {
        writeProtocolControlMessage(new SetChunkSize(size));
    }

    public void writeProtocolControlMessage(RtmpEvent evt) throws IOException {
        writer.write(evt, 2, 0);
    }

    public void writeProtocolControlMessageAsync(RtmpEvent evt) {
        writer.writeAsync(evt, 2, 0, this::onAsyncWriteException);
    }


}
