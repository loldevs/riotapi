package net.boreeas.riotapi.rtmp;

import junit.framework.TestCase;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.loginqeue.LoginQueue;
import net.boreeas.riotapi.rtmp.serialization.DefaultRtmpClient;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

@Log4j
public class RtmpClientTest extends TestCase {

    private static Properties testConfig = new Properties();;
    private static Shard shard = Shard.NA;
    private static RtmpClient client;

    static {
        staticSetup();
    }

    @SneakyThrows
    private static void staticSetup() {

        testConfig.load(new InputStreamReader(new FileInputStream("testconfig.properties")));

        client = new DefaultRtmpClient(shard.prodUrl, Shard.RTMPS_PORT, true);
        //client = new DefaultRtmpClient("localhost", 8443, false);


        String user = testConfig.getProperty("user");
        String pass = testConfig.getProperty("pass");
        String authKey = new LoginQueue(shard).waitInQueue(user, pass).await().getToken();

        client.connect();
        client.authenticate(user, pass, authKey, "<invalid version>");
    }

    public void testSession() {
        System.out.println(client.getSession());
    }

    public void testLoginPacket() {
        System.out.println(client.getLoginDataPacket());
    }
}