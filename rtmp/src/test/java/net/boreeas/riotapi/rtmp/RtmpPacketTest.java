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

import junit.framework.TestCase;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.com.riotgames.platform.login.AuthenticationCredentials;
import net.boreeas.riotapi.loginqueue.LoginQueue;
import net.boreeas.riotapi.rtmp.messages.FlexMessage;
import net.boreeas.riotapi.rtmp.messages.RemotingMessage;
import net.boreeas.riotapi.rtmp.messages.control.Command;
import net.boreeas.riotapi.rtmp.messages.control.Invoke;
import net.boreeas.riotapi.rtmp.messages.control.InvokeAmf3;
import net.boreeas.riotapi.rtmp.serialization.AmfReader;
import net.boreeas.riotapi.rtmp.serialization.AmfWriter;
import net.boreeas.riotapi.rtmp.serialization.ObjectEncoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

/**
 * Created on 7/21/2014.
 */
public class RtmpPacketTest extends TestCase {

    private static byte[] string2bytes(String s) {
        String[] parts = s.split(" ");
        byte[] buf = new byte[parts.length];

        for (int i = 0; i < parts.length; i++) {
            buf[i] = (byte) (Short.parseShort(parts[i], 16) & 0xff);
        }

        return buf;
    }

    private static byte chr2byte(char a, char b) {
        return (byte) (((a - '0') << 4) | (b - '0'));
    }

    public void testReserializeAuth() throws InterruptedException, IOException {
        AuthenticationCredentials cred = new AuthenticationCredentials();
        cred.setClientVersion("4.12.FOO");
        cred.setAuthToken(new LoginQueue(Shard.NA).waitInQueue("riotapitestacc1", "riotapitestacc1").await());
        cred.setLocale("en_US");
        //String addr = Util.getConnectionInfoIpAddr();
        //cred.setIpAddress(addr);
        cred.setDomain("lolclient.lol.riotgames.com");
        cred.setOperatingSystem("WIN");


        AuthenticationCredentials credentials;
        Invoke invoke = sendAsyncRpc("my-rtmps", "loginService", "login", cred);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        AmfWriter writer = new AmfWriter(bout);
        RtmpPacketWriter rtmp = new RtmpPacketWriter(writer, ObjectEncoding.AMF3, err -> fail(err.toString()));

        rtmp.write(invoke, 2, 0);

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        AmfReader reader = new AmfReader(bin);
        RtmpPacketReader rtmpRead = new RtmpPacketReader(reader, err -> fail(err.toString()), this::printPacket);
        rtmpRead.run();
    }


    public void testCredentials() {
        byte[] buf = string2bytes("03 00 02 e3 00 02 b5 11 00 00 00 00 00 05 00 40 " +
                "00 00 00 00 00 00 00 05 11 0a 81 13 4f 66 6c 65 " +
                "78 2e 6d 65 73 73 61 67 69 6e 67 2e 6d 65 73 73 " +
                "61 67 65 73 2e 52 65 6d 6f 74 69 6e 67 4d 65 73 " +
                "73 61 67 65 0f 68 65 61 64 65 72 73 13 74 69 6d 65 73 74 61 6d 70 09 62 6f 64 79 13 6f 70 65 72 61 74 69 6f 6e 0d 73 6f 75 72 63 65 13 6d 65 73 73 61 67 65 49 64 11 63 6c 69 65 6e 74 49 64 15 74 69 6d 65 54 6f 4c 69 76 65 17 64 c3 65 73 74 69 6e 61 74 69 6f 6e 0a 0b 01 21 44 53 52 65 71 75 65 73 74 54 69 6d 65 6f 75 74 04 3c 09 44 53 49 64 06 49 33 33 34 46 38 43 45 32 2d 32 44 31 43 2d 39 31 30 32 2d 34 36 33 46 2d 46 38 41 30 35 34 39 43 37 32 41 43 15 44 53 45 6e 64 70 6f 69 6e 74 06 11 6d 79 2d 72 74 6d 70 73 01 04 00 09 03 01 0a 81 33 6d 63 6f 6d 2e 72 69 6f 74 67 61 6d 65 73 2e 70 6c 61 74 66 6f 72 6d c3 2e 6c 6f 67 69 6e 2e 41 75 74 68 65 6e 74 69 63 61 74 69 6f 6e 43 72 65 64 65 6e 74 69 61 6c 73 1f 6f 70 65 72 61 74 69 6e 67 53 79 73 74 65 6d 11 75 73 65 72 6e 61 6d 65 25 70 61 72 74 6e 65 72 43 72 65 64 65 6e 74 69 61 6c 73 0d 6c 6f 63 61 6c 65 0d 64 6f 6d 61 69 6e 13 61 75 74 68 54 6f 6b 65 6e 17 6f 6c 64 50 61 73 73 77 6f 72 64 1b 63 6c 69 65 6e 74 56 65 72 73 69 6f 6e 11 70 c3 61 73 73 77 6f 72 64 1d 73 65 63 75 72 69 74 79 41 6e 73 77 65 72 13 69 70 41 64 64 72 65 73 73 06 1d 4c 6f 4c 52 54 4d 50 53 43 6c 69 65 6e 74 06 1f 72 69 6f 74 61 70 69 74 65 73 74 61 63 63 31 01 06 0b 65 6e 5f 55 53 06 37 6c 6f 6c 63 6c 69 65 6e 74 2e 6c 6f 6c 2e 72 69 6f 74 67 61 6d 65 73 2e 63 6f 6d 06 81 41 79 55 36 56 30 38 61 4a 34 69 67 35 75 6f 47 4f 31 77 62 59 4b 7a 39 c3 51 63 76 63 41 62 79 58 45 54 74 57 63 4e 41 33 79 33 35 64 30 45 33 49 30 39 36 57 6c 4f 68 47 38 2d 5a 73 64 4a 76 76 33 61 47 4d 7a 48 31 4b 78 35 6b 34 79 76 70 41 47 64 4b 38 6b 46 62 37 2d 43 76 52 61 6c 54 78 6d 01 06 11 34 2e 31 32 2e 46 4f 4f 06 1f 72 69 6f 74 61 70 69 74 65 73 74 61 63 63 31 01 06 1d 32 30 39 2e 31 33 33 2e 35 32 2e 32 33 32 06 0b 6c 6f 67 69 6e 01 06 49 c3 30 36 33 33 39 35 31 37 2d 34 44 36 33 2d 34 33 36 38 2d 43 38 36 41 2d 32 32 46 34 45 36 42 43 34 31 44 30 01 04 00 06 19 6c 6f 67 69 6e 53 65 72 76 69 63 65 ");
        ByteArrayInputStream bin = new ByteArrayInputStream(buf);
        AmfReader reader = new AmfReader(bin);
        RtmpPacketReader rtmpReader = new RtmpPacketReader(reader);

        rtmpReader.setOnPacket(this::printPacket);
        rtmpReader.setOnError(ex -> fail(ex.getMessage()));
        rtmpReader.run();
    }

    private void printPacket(RtmpEvent rtmpEvent) {

        Command cmd = (Command) rtmpEvent;
        System.out.println("invokeId: " + cmd.getInvokeId());
        System.out.println("buffer:   " + cmd.getBuffer());
        System.out.println("connParams:" + cmd.getConnectionParams());
        System.out.println("header:   " + cmd.getHeader());
        System.out.println("timestamp:" + cmd.getTimeStamp());
        System.out.println("type:     " + cmd.getType());

        System.out.println("\nBODY:");
        Command.Method method  = cmd.getMethod();
        System.out.println("name: " + method.getName());
        System.out.println("status:" + method.getStatus());
        System.out.println("params:" + Arrays.toString(method.getParams()));


        System.out.println("\nParams:");
        RemotingMessage remotingMessage = (RemotingMessage) method.getParams()[0];
        System.out.println("operation: " + remotingMessage.getOperation());
        System.out.println("source:    " + remotingMessage.getSource());
        System.out.println("clId:      " + remotingMessage.getClientId());
        System.out.println("destination: " + remotingMessage.getDestination());
        System.out.println("headers:   " + remotingMessage.getHeaders());
        System.out.println("messageId: " + remotingMessage.getMessageId());
        System.out.println("timestamp: " + remotingMessage.getTimestamp());
        System.out.println("timeToLive:" + remotingMessage.getTimeToLive());
        System.out.println("body:      " + remotingMessage.getBody());

        System.out.println("\nBody");
        AuthenticationCredentials auth = (AuthenticationCredentials) ((Map) remotingMessage.getBody()).get(0);
        printCred(auth);


    }

    public static void printCred(AuthenticationCredentials auth) {
        System.out.println("token:      " + auth.getAuthToken());
        System.out.println("clientVers: " + auth.getClientVersion());
        System.out.println("domain:     " + auth.getDomain());
        //System.out.println("ipAddr:     " + auth.getIpAddress());
        System.out.println("locale:     " + auth.getLocale());
        System.out.println("oldPass:    " + auth.getOldPassword());
        System.out.println("opSys:      " + auth.getOperatingSystem());
        System.out.println("partnerCred:" + auth.getPartnerCredentials());
        System.out.println("pass:       " + auth.getPassword());
        System.out.println("secAnswer:  " + auth.getSecurityAnswer());
        System.out.println("user:       " + auth.getUsername());
    }


    private RemotingMessage createRemotingMessage(String endpoint, String service, String method, Object... args) {

        RemotingMessage message = new RemotingMessage(null, method);
        message.getHeaders().put(FlexMessage.ENDPOINT, endpoint);
        message.getHeaders().put(FlexMessage.LOCAL_CLIENT_ID, UUID.randomUUID());
        message.getHeaders().put(FlexMessage.REQUEST_TIMEOUT, 60);

        message.setDestination(service);
        message.setBody(args);
        return message;
    }


    public Invoke sendAsyncRpc(String endpoint, String service, String method, Object... args) throws IOException {
        RemotingMessage message = createRemotingMessage(endpoint, service, method, args);
        Invoke invoke = createAmf3InvokeSkeleton(null, message);

        return invoke;
    }


    public Invoke createAmf3InvokeSkeleton(String command, Object... args) {
        Invoke invoke = new InvokeAmf3();
        invoke.setInvokeId(2);
        invoke.setMethod(new Command.Method(command, args));

        return invoke;
    }
}
