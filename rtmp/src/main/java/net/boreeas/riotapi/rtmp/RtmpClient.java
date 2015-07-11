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

import com.gvaneyck.rtmp.DummySSLSocketFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.com.riotgames.platform.account.impl.AccountState;
import net.boreeas.riotapi.com.riotgames.platform.clientfacade.domain.LoginDataPacket;
import net.boreeas.riotapi.com.riotgames.platform.login.AuthenticationCredentials;
import net.boreeas.riotapi.com.riotgames.platform.login.Session;
import net.boreeas.riotapi.rtmp.messages.*;
import net.boreeas.riotapi.rtmp.messages.control.*;
import net.boreeas.riotapi.rtmp.serialization.*;
import net.boreeas.riotapi.rtmp.services.*;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created on 5/24/2014.
 */
@Log4j
public abstract class RtmpClient implements AutoCloseable {

    public static final int RTMP_VERSION = 3;
    private static final int PAYLOAD_SIZE = 1536;
    private static final int HEARTBEAT_INTERVAL = 120; // Interval for heartbeats in seconds
    public static final int INVOKE_STREAM = 3;
    public static final int DEFAULT_MSG_STREAM = 0;

    // Connection params
    @Getter private String host;
    @Getter private int port;
    @Getter private boolean useSSL;
    private Socket socket;

    // Amf I/O
    private RtmpPacketReader reader;
    private RtmpPacketWriter writer;
    private Thread readerThread;
    private Thread writerThread;
    @Getter @Setter private ObjectEncoding objectEncoding = ObjectEncoding.AMF3;

    // Async messages
    private Map<Consumer<AsyncMessageEvent>, Predicate<AsyncMessageEvent>> asyncMessageListeners = new ConcurrentHashMap<>();
    private String broadcastChannel;
    private String gameNewsChannel;
    private String clientNewsChannel;

    // Connection data
    @Getter private boolean isConnected = false;
    private long timeOffset;
    private AtomicInteger invokeId = new AtomicInteger(1);
    private String localClientId;
    @Getter private Session session;
    @Getter private LoginDataPacket loginDataPacket;
    private ScheduledExecutorService heartbeatExecutor = Executors.newScheduledThreadPool(1);
    private int heartbeats = 1;
    @Setter @Getter private boolean debug;

    // Async await
    private Map<Integer, InvokeCallback> callbacks = new HashMap<>();

    // <editor-fold desc="Services">
    /**
     * Services user authentication as well as the lcds heartbeat.
     */
    public final LoginService loginService;
    /**
     * Answers account related queries.
     */
    public final AccountService accountService;
    /**
     * Contains the kudos system and login packet.
     */
    public final ClientFacadeService clientFacadeService;
    /**
     * Attach a player or a group of players to a matchmaking queue.
     */
    public final MatchmakerService matchmakerService;
    /**
     * Set account-related preferences.
     */
    public final PlayerPreferencesService playerPreferencesService;
    /**
     * Control owned champions and summoner spells.
     */
    public final InventoryService inventoryService;
    /**
     * Control owned runes.
     */
    public final SummonerRuneService summonerRuneService;
    /**
     * Control runes and mastery pages.
     */
    public final BookService bookService;
    /**
     * Query league related information.
     */
    public final LeaguesServiceProxy leaguesServiceProxy;
    /**
     * Manage ranked teams.
     */
    public final SummonerTeamService summonerTeamService;
    /**
     * Control user accounts.
     */
    public final SummonerService summonerService;
    /**
     * Retrieve stats for a player.
     */
    public final PlayerStatsService playerStatsService;
    /**
     * Reroll champions in ARAM.
     */
    public final LcdsRerollService lcdsRerollService;
    /**
     * Manage champion select and custom games.
     */
    public final GameService gameService;
    /**
     * Control summoner icons.
     */
    public final SummonerIconService summonerIconService;
    /**
     * Trade champions in ranked and aram.
     */
    public final LcdsChampionTradeService lcdsChampionTradeService;
    /**
     * Manage a group finder lobby.
     */
    public final LcdsServiceProxy lcdsServiceProxy;

    /**
     * Manage premade teams and game invitations.
     */
    public final LcdsGameInvitationService lcdsGameInvitationService;
    // </editor-fold>

    public RtmpClient(String host, int port, boolean useSSL) {
        this.host = host;
        this.port = port;
        this.useSSL = useSSL;
        this.loginService = new LoginService(this);
        this.accountService = new AccountService(this);
        this.clientFacadeService = new ClientFacadeService(this);
        this.matchmakerService = new MatchmakerService(this);
        this.playerPreferencesService = new PlayerPreferencesService(this);
        this.inventoryService = new InventoryService(this);
        this.summonerRuneService = new SummonerRuneService(this);
        this.bookService = new BookService(this);
        this.leaguesServiceProxy = new LeaguesServiceProxy(this);
        this.summonerTeamService = new SummonerTeamService(this);
        this.summonerService = new SummonerService(this);
        this.playerStatsService = new PlayerStatsService(this);
        this.lcdsRerollService = new LcdsRerollService(this);
        this.gameService = new GameService(this);
        this.summonerIconService = new SummonerIconService(this);
        this.lcdsChampionTradeService = new LcdsChampionTradeService(this);
        this.lcdsServiceProxy = new LcdsServiceProxy(this);
        this.lcdsGameInvitationService = new LcdsGameInvitationService(this);
    }


    // <editor-fold desc="Callbacks">
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
    private void releaseCallbacks(Exception ex) {
        synchronized (callbacks) {
            for (Map.Entry<Integer, InvokeCallback> cb: callbacks.entrySet()) {
                cb.getValue().release(ex);
            }
        }
    }

    public int getTimeDelta() {
        return (int) ((System.currentTimeMillis() - this.timeOffset) & 0xFFFFFFFFL);
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
                writeProtocolControlMessage(
                        new UserControlMessage(UserControlMessage.Type.PING_RESPONSE, ucm.getValues())
                );
            } else {
                log.info("Unknown UserControlMessage: " + ucm.getControlMessageType() + "/" + ucm.getValues());
            }
        } else if (packet instanceof SetPeerBandwidth) {
            writeProtocolControlMessage(new WindowAcknowledgementSize(((SetPeerBandwidth) packet).getWidth()));
        }

        extendedOnPacket(packet);
    }

    private void onInvoke(Command cmd) {
        Command.Method method = cmd.getMethod();
        Object params = method.getParams().length == 1 ? method.getParams()[0] : method.getParams();

        int invokeId = cmd.getInvokeId();
        InvokeCallback callback = getInvokeCallback(invokeId);

        Object result = params;
        switch (method.getName()) {
            case "_result":

                if (params instanceof AcknowledgeMessage) {
                    result = ((AcknowledgeMessage) params).getBody();
                }
                break;
            case "receive":
                AsyncMessage async = (AsyncMessage) params;

                String subtopic = Objects.toString(async.getHeaders().get(AsyncMessage.SUBTOPIC));
                String clientId = async.getClientId();
                Object body = async.getBody();

                result = new AsyncMessageEvent(clientId, subtopic, body);
                onAsyncMessageEvent((AsyncMessageEvent) result);
                break;
            case "onstatus":

                log.info("Onstatus: " + Objects.toString(method.getParams()) + "/" + method.getStatus() + "/Success=" + method.isSuccess());
                break;
            default:

                log.info("Unknown command: " + method.getName() + "/" + Arrays.toString(method.getParams()) + "/" + method.getStatus() + "/Success=" + method.isSuccess()
                        + "\n\t\t\tParams = " + params.getClass()
                        + "\n\t\t\tMethod = " + method.getClass());
                break;
        }

        if (callback != null) {
            callback.release(result);
        }
    }

    private void onAsyncMessageEvent(AsyncMessageEvent event) {
        boolean hit = false;
        for (Map.Entry<Consumer<AsyncMessageEvent>, Predicate<AsyncMessageEvent>> listener: asyncMessageListeners.entrySet()) {
            if (listener.getValue().test(event)) {

                Consumer<AsyncMessageEvent> consumer = listener.getKey();
                consumer.accept(event);
                hit = true;
            }
        }

        if (!hit) {
            log.warn("Unhandled async message " + event);
        }
    }

    public void addAsyncChannelListener(Consumer<AsyncMessageEvent> consumer, Predicate<AsyncMessageEvent> filter) {
        asyncMessageListeners.put(consumer, filter);
    }

    public void addAsyncChannelListener(Consumer<AsyncMessageEvent> consumer) {
        addAsyncChannelListener(consumer, msg -> true);
    }

    public void addAsyncChannelListener(Consumer<AsyncMessageEvent> consumer, String channel) {
        addAsyncChannelListener(consumer, msg -> msg.getClientId().equals(channel));
    }

    public void addAsyncChannelListener(Consumer<AsyncMessageEvent> consumer, String channel, Predicate<AsyncMessageEvent> filter) {
        addAsyncChannelListener(consumer, filter.and(msg -> msg.getClientId().equals(channel)));
    }

    public void removeAsyncChannelListener(Consumer<AsyncMessageEvent> consumer) {
        asyncMessageListeners.remove(consumer);
    }
    // </editor-fold>

    public void connect() throws IOException, InterruptedException {
        connect(false);
    }

    public void connect(boolean ignoreCertificates) throws IOException, InterruptedException {
        log.info("Connecting to " + host + ":" + port);
        this.socket = useSSL ? SSLSocketFactory.getDefault().createSocket(host, port) : new Socket(host, port);

        AmfWriter writer;
        AmfReader reader;
        if (debug) {
            writer = new AmfWriter(new DumpingOutputStream(socket.getOutputStream()));
            reader = new AmfReader(new DumpingInputStream(socket.getInputStream()));
        } else {
            writer = new AmfWriter(socket.getOutputStream());
            reader = new AmfReader(socket.getInputStream());
        }

        try {
            doHandshake(writer, reader);
        } catch (SSLHandshakeException ex) {
            disconnect();

            if (ignoreCertificates) {
                log.error("Error executing SSL handshake, reconnecting and ignoring certificate chain");
                this.socket = new DummySSLSocketFactory().createSocket(host, port);
                writer = new AmfWriter(socket.getOutputStream());
                reader = new AmfReader(socket.getInputStream());
                heartbeatExecutor = Executors.newScheduledThreadPool(1);

                doHandshake(writer, reader);

            } else {
                throw ex;
            }
        }

        this.reader = new RtmpPacketReader(reader, this::onError, this::onPacket);
        this.writer = new RtmpPacketWriter(writer, ObjectEncoding.AMF3, this::onAsyncWriteException);
        readerThread = new Thread(this.reader, "RtmpClient reader thread");
        readerThread.setDaemon(true);
        readerThread.start();
        writerThread = new Thread(this.writer, "RtmpClient writer thread");
        writerThread.setDaemon(true);
        writerThread.start();

        try {
            int id = sendConnectInvoke(null, null, (useSSL ? "rtmps://" : "rtmp://") + host + ":" + port);
            Object reply = waitForInvokeReply(id);
            localClientId = ((AmfObject) reply).get("id") + "";
            log.info("Client Id: " + localClientId);

            isConnected = true;

        } catch (InterruptedException ex) {
            log.error("Got interrupted, disconnecting: " + ex);
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
        int read = 0;
        do {
            read += reader.read(buffer, read, buffer.length - read);
        } while (read != buffer.length);

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

        read = 0;
        do {
            read += reader.read(buffer, read, buffer.length - read);
        } while (read != buffer.length);

        for (int i = 8; i < buffer.length; i++) {
            if (buffer[i] != c1Payload[i]) {
                throw new ProtocolException("Handshake payload mismatch at " + i);
            }
        }
    }

    public void disconnect() {
        close();
    }

    @Override
    public void close() {
        isConnected = false;
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
            if (heartbeatExecutor != null) heartbeatExecutor.shutdownNow();
        } catch (IOException e) {}
    }


    public void authenticate(String user, String password, String authToken, String clientVersion) {
        authenticate(user, password, authToken, clientVersion, "en_GB");
    }

    public Session authenticate(String user, String password, String authToken, String clientVersion, String locale) {
        AuthenticationCredentials credentials = new AuthenticationCredentials();
        credentials.setUsername(user);
        credentials.setPassword(password);
        credentials.setClientVersion(clientVersion);
        credentials.setAuthToken(authToken);
        credentials.setLocale(locale);
        //String addr = Util.getConnectionInfoIpAddr();
        //credentials.setIpAddress(addr);
        credentials.setMacAddress("");
        credentials.setDomain("lolclient.lol.riotgames.com");
        credentials.setOperatingSystem("Windows 8");

        log.info("Login service call: " + user + "/***/" + authToken+ " on client version " + clientVersion + " (locale " + locale + ")");

        this.session = loginService.login(credentials);

        log.info("Rtmp login: " + user.toLowerCase() + "/" + session.getToken());
        login(user.toLowerCase(), session.getToken());


        this.broadcastChannel = "bc-" + session.getAccountSummary().getAccountId();
        this.gameNewsChannel = "gn-" + session.getAccountSummary().getAccountId(); // Game news?
        this.clientNewsChannel = "cn-" + session.getAccountSummary().getAccountId(); // Client news?
        subscribe("my-rtmps", "messagingDestination", "bc", broadcastChannel);
        subscribe("my-rtmps", "messagingDestination", gameNewsChannel, gameNewsChannel);
        subscribe("my-rtmps", "messagingDestination", clientNewsChannel, clientNewsChannel);


        log.info("Retrieving data packet");
        this.loginDataPacket = clientFacadeService.getLoginDataPacket();

        AccountState state = accountService.getAccountState();
        log.info("Login complete - account state: " + state);
        if (state != AccountState.ENABLED) {
            throw new RtmpException("Invalid account state: " + state);
        }

        // Setup pings
        heartbeatExecutor.scheduleAtFixedRate(() -> loginService.performLcdsHeartBeat(loginDataPacket.getAllSummonerData().getSummoner().getAcctId(), session.getToken(), heartbeats++),
                0, HEARTBEAT_INTERVAL, TimeUnit.SECONDS
        );

        matchmakerService.getAvailableQueues();
        inventoryService.getSummonerActiveBoosts();
        inventoryService.getAvailableChampions();
        summonerRuneService.getSummonerRuneInventory(loginDataPacket.getAllSummonerData().getSummoner().getSumId());
        leaguesServiceProxy.getMyLeaguePositions();
        playerPreferencesService.loadPreferencesByKey("KEY_BINDINGS", Double.NaN, false);
        bookService.getMasteryBook(loginDataPacket.getAllSummonerData().getSummoner().getSumId());
        lcdsGameInvitationService.getPendingInvitations();
        summonerTeamService.createPlayer(); // Apparently necessary for some calls to return


        return session;
    }

    public String getBroadcastChannel() {
        if (broadcastChannel == null) throw new IllegalStateException("Broadcast channel is unknown. Possible cause: Connection not finished");
        return broadcastChannel;
    }

    public String getGameNewsChannel() {
        if (gameNewsChannel == null) throw new IllegalStateException("Game news channel is unknown. Possible cause: Connection not finished");
        return gameNewsChannel;
    }

    public String getClientNewsChannel() {
        if (clientNewsChannel == null) throw new IllegalStateException("Client news channel is unknown. Possible cause: Connection not finished");
        return clientNewsChannel;
    }














    private void send(Invoke invoke) {
        if (!isConnected()) {
            throw new RtmpException("Not connected");
        }
        sendOverrideConnect(invoke);
    }

    private void sendOverrideConnect(Invoke invoke) {
        writer.write(invoke, INVOKE_STREAM, DEFAULT_MSG_STREAM);
    }


    public int sendInvoke(String service, Object... args) {
        Invoke invoke = createAmf3InvokeSkeleton(service, args);
        send(invoke);
        return invoke.getInvokeId();
    }


    /**
     * <p>
     * Send a remote procedure call.
     * </p>
     * <p>
     *     Note that due to varargs ambiguity, this method will not work if the first argument to the call is a string.
     *     In that case, use the explicit {@link #sendRpcWithEndpoint(String, String, String, Object...)} with endpoint
     *     "my-rtmps" instead, or use {@link #sendRpcToDefault(String, String, Object...)}
     * </p>
     * @param service The service handling the call
     * @param method The method to call
     * @param args Optional arguments to the call
     * @return The invoke id callback
     * @deprecated Use the explicit {@link #sendRpcToDefault(String, String, Object...)} instead
     */
    @Deprecated
    public int sendRpc(String service, String method, Object... args) {
        return sendRpc("my-rtmps", service, method, args);
    }

    /**
     * <p>
     * Send a remote procedure call.
     * </p>
     * @param service The service handling the call
     * @param method The method to call
     * @param args Optional arguments to the call
     * @return The callback getting called once the rpc returns a result
     */
    public InvokeCallback sendRpcToDefault(String service, String method, Object... args) {
        return sendRpcWithEndpoint("my-rtmps", service, method, args);
    }

    /**
     * Send a remote procedure call.
     * @param endpoint The endpoint of the call
     * @param service The service handling the call
     * @param method The method to call
     * @param args Optional args to the call
     * @return The id of the callback
     * @deprecated Due to method resolution ambiguities, this method is due to be removed within the next couple
     * of releases. Use the explicit {@link #sendRpcWithEndpoint(String, String, String, Object...)} instead.
     */
    @Deprecated
    public int sendRpc(String endpoint, String service, String method, Object... args) {
        RemotingMessage message = createRemotingMessage(endpoint, service, method, args);
        Invoke invoke = createAmf3InvokeSkeleton(null, message);

        send(invoke);
        return invoke.getInvokeId();
    }

    /**
     * Send a remote procedure call.
     * @param endpoint The endpoint of the call
     * @param service The service handling the call
     * @param method The method to call
     * @param args Optional args to the call
     * @return The callback getting called once the rpc returns a result
     */
    public InvokeCallback sendRpcWithEndpoint(String endpoint, String service, String method, Object... args) {
        RemotingMessage message = createRemotingMessage(endpoint, service, method, args);
        Invoke invoke = createAmf3InvokeSkeleton(null, message);
        InvokeCallback callback = getInvokeCallback(invoke.getInvokeId());

        send(invoke);
        return callback;
    }



    public <T> T sendRpcAndWait(String service, String method, Object... args) {

        try {
            InvokeCallback callback = sendRpcToDefault(service, method, args);
            Object o = callback.waitForReply();

            if (o instanceof InvokeException) {

                if (((InvokeException) o).getCause() instanceof RuntimeException) {
                    throw (RuntimeException) ((InvokeException) o).getCause();
                }
                throw (InvokeException) o;

            } else if (o instanceof ErrorMessage) {

                if (((ErrorMessage) o).getRootCause() instanceof RuntimeException) {
                    throw (RuntimeException) ((ErrorMessage) o).getRootCause();
                }
                throw new InvokeException((ErrorMessage) o);

            } else if (o instanceof Exception) {
                throw new RtmpException((Exception) o);
            }

            return (T) o;
        } catch (final InterruptedException ex) {
            throw new RtmpException(ex);
        }

    }


    private RemotingMessage createRemotingMessage(String endpoint, String service, String method, Object... args) {
        if (objectEncoding != ObjectEncoding.AMF3) {
            throw new IllegalStateException("RPC requires AMF3");
        }

        RemotingMessage message = new RemotingMessage(null, method);
        message.setDestination(service);
        message.setBody(args);

        message.getHeaders().put(FlexMessage.ENDPOINT, endpoint);
        message.getHeaders().put(FlexMessage.LOCAL_CLIENT_ID, localClientId);
        message.getHeaders().put(FlexMessage.REQUEST_TIMEOUT, 60);

        return message;
    }

    public int getNextInvokeId() {
        return invokeId.getAndIncrement();
    }

    public Invoke createAmf3InvokeSkeleton(String command, Object... args) {
        Invoke invoke = new InvokeAmf3();
        invoke.setInvokeId(getNextInvokeId());
        invoke.setMethod(new Command.Method(command, args));
        invoke.setTimeStamp(getTimeDelta());

        return invoke;
    }

    public Invoke createAmf0InvokeSkeleton(String command, Object... args) {
        Invoke result = new InvokeAmf0();
        result.setMethod(new Command.Method(command, args));
        result.setInvokeId(getNextInvokeId());
        result.setTimeStamp(getTimeDelta());

        return result;
    }

    private int sendConnectInvoke(String pageUrl, String swfUrl, String tcUrl) {

        CommandMessage message = getCommandMessage(null, null, null, null, CommandMessage.Operation.CLIENT_PING);
        message.getHeaders().put(FlexMessage.LOCAL_CLIENT_ID, "my-rtmps");


        Invoke invoke = createAmf0InvokeSkeleton("connect", false, "nil", "", message);

        AnonymousAmfObject connParams = new AnonymousAmfObject();
        connParams.put("pageUrl", pageUrl);
        connParams.put("objectEncoding", objectEncoding == ObjectEncoding.AMF0 ? 0.0 : 3.0);
        connParams.put("capabilities", 15);
        connParams.put("audioCodecs", 1639);
        connParams.put("flashVer", "WIN 9,0,115,0");
        connParams.put("swfUrl", swfUrl);
        connParams.put("videoFunction", 1);
        connParams.put("fpad", false);
        connParams.put("videoCodecs", 252);
        connParams.put("tcUrl", tcUrl);
        connParams.put("app", null);

        invoke.setConnectionParams(connParams);

        sendOverrideConnect(invoke);
        return invoke.getInvokeId();
    }




    private CommandMessage getCommandMessage(String endpoint, String destination, String subtopic, String clientId,
                                             CommandMessage.Operation op) {


        CommandMessage msg = new CommandMessage();
        msg.setClientId(clientId);
        msg.setOperation(op);
        msg.setDestination(destination);
        msg.getHeaders().put(FlexMessage.ENDPOINT, endpoint);
        msg.getHeaders().put(FlexMessage.LOCAL_CLIENT_ID, this.localClientId);
        msg.getHeaders().put(AsyncMessage.SUBTOPIC, subtopic);
        return msg;
    }


    public int subscribe(String endpoint, String destination, String subtopic, String clientId) {
        return sendInvoke(null, getCommandMessage(endpoint, destination, subtopic, clientId, CommandMessage.Operation.SUBSCRIBE));
    }



    public int unsubscribe(String endpoint, String destination, String subtopic, String clientId) {
        return sendInvoke(null, getCommandMessage(endpoint, destination, subtopic, clientId, CommandMessage.Operation.UNSUBSCRIBE));
    }


    @SneakyThrows(IOException.class)
    public int login(String username, String token) {
        CommandMessage msg = getCommandMessage(null, "auth", null, null, CommandMessage.Operation.LOGIN);
        byte[] authBuffer = String.format("%s:%s", username.toLowerCase(), token).getBytes("UTF-8");
        msg.setBody(new String(Base64.getEncoder().encode(authBuffer), "UTF-8"));
        return sendInvoke(null, msg);
    }


    public int logout() {
        return sendInvoke(null, getCommandMessage(null, "auth", null, null, CommandMessage.Operation.LOGOUT));
    }



    public int ping() {
        return sendInvoke(null, getCommandMessage(null, null, null, null, CommandMessage.Operation.CLIENT_PING));
    }


    public void setChunkSize(int size) {
        writeProtocolControlMessage(new SetChunkSize(size));
    }

    public void writeProtocolControlMessage(RtmpEvent evt) {
        writer.write(evt, 2, 0);
    }
}
