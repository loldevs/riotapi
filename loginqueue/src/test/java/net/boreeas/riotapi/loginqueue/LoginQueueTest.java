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

package net.boreeas.riotapi.loginqueue;

import junit.framework.TestCase;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.com.riotgames.platform.account.management.InvalidCredentialsException;

import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

public class LoginQueueTest extends TestCase {

    public void testGetAuthKeyInvalidCredentials() throws Exception {
        try {
            new LoginQueue(Shard.NA).getAuthToken("Foo", "");
            fail();
        } catch (InvalidCredentialsException ex) {
        }
    }

    public void testQueueWait() throws Exception {
        System.out.println(URLEncoder.encode(Shard.NA.loginQueue, "UTF-8"));
        try {
            AuthResult result = new LoginQueue(Shard.NA).waitInQueue("foo", "").await(2, TimeUnit.SECONDS);
            fail();
        } catch (InvalidCredentialsException ex) {
        }
    }

    public void testQueueEarlyReturn() throws Exception {
        try {
            AuthResult result = new LoginQueue(Shard.NA).waitInQueue("foo", "").await(0, TimeUnit.SECONDS);
            fail();
        } catch (IllegalStateException ex) {
        }
    }
}