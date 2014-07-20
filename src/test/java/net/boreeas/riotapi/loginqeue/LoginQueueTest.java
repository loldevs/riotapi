package net.boreeas.riotapi.loginqeue;

import com.google.gson.JsonElement;
import junit.framework.TestCase;
import net.boreeas.riotapi.Shard;

public class LoginQueueTest extends TestCase {

    public void testGetAuthKeyInvalidCredentials() throws Exception {
        try {
            new LoginQueue(Shard.NA).getAuthToken("Foo", "");
            fail();
        } catch (InvalidCredentialsException ex) {
        }
    }

    public void testQueueWait() throws Exception {
        try {
            AuthResult result = new LoginQueue(Shard.NA).waitInQueue("foo", "").await();
            fail();
        } catch (InvalidCredentialsException ex) {
        }
    }
}