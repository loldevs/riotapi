package net.boreeas.riotapi.rtmp;

import junit.framework.TestCase;
import net.boreeas.riotapi.Shard;

import java.io.IOException;

public class RtmpClientTest extends TestCase {

    private static Shard shard = Shard.NA;

    private RtmpClient client;

    @Override
    public void setUp() throws Exception {
        client = new RtmpClient(shard.prodUrl, Shard.RTMPS_PORT, true) {
            @Override
            public void onReadException(Exception ex) {
                fail("Exception occurred");
            }


            @Override
            public void onAsyncWriteException(IOException ex) {
                fail(ex.getMessage());
            }


            @Override
            public void extendedOnPacket(RtmpEvent packet) {

            }
        };
    }

    public void testRepeatedConnect() throws IOException, InterruptedException {
        for (int i = 0; i < 10; i++) {
            System.out.println("connecting");
            client.connect();
            client.disconnect();
        }
    }

    public void testConnect() throws IOException, InterruptedException {
        System.out.println("connecting");
        client.connect();
        assertTrue(client.isConnected());
        client.disconnect();
    }

    public void testSendInvoke() throws IOException, InterruptedException {


        System.out.println("connecting");
        client.connect();

        System.out.println("client connected");

        int id = client.sendInvoke("musicalService", "findSongs");
        System.out.println("invoke id: " + id);
        Object o = client.waitForInvokeReply(id);
        assertFalse(o instanceof Throwable);
        System.out.println(o);
    }


    @Override
    public void tearDown() throws Exception {
        client.disconnect();
    }
}