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

package net.boreeas.riotapi.loginqueue.newlq;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.com.riotgames.platform.account.management.InvalidCredentialsException;
import net.boreeas.riotapi.loginqueue.AuthResult;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class LoginQueueTest extends TestCase {

    public void testGetAuthKeyInvalidCredentials() throws Exception {
        try {
            new LoginQueue(Shard.NA).getAuthToken("Foo", "");
            Assert.fail();
        } catch (InvalidCredentialsException ex) {
        }
    }

    public void testQueueWait() throws Exception {
        try {
            AuthResult result = new LoginQueue(Shard.NA).waitInQueue("foo", "").await(2, TimeUnit.SECONDS);
            Assert.fail();
        } catch (InvalidCredentialsException ex) {
        }
    }

    public void testQueueAuth() throws Exception {
        Properties prop = new Properties();
        prop.load(new InputStreamReader(new FileInputStream("testconfig.properties")));

        String token = new LoginQueue(Shard.EUW).waitInQueueBlocking(prop.getProperty("user"), prop.getProperty("pass"));
        System.out.println("LQ token: " + token);
    }
}