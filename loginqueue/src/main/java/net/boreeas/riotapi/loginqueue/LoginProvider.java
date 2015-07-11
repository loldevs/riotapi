/*
 * Copyright 2015 The LolDevs team (https://github.com/loldevs)
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

import net.boreeas.riotapi.loginqueue.newlq.IngameCredentials;

/**
 * @author Malte Schütze
 */
public interface LoginProvider {

    /**
     * Try to retrieve the auth token from the login queue
     * @param username The username to log in with
     * @param password The password to log in with
     * @return The auth result, being either <tt>OK</tt> and {@link IngameCredentials} or <tt>QUEUE</tt> with
     * queue information
     */
    public AuthResult getAuthToken(String username, String password);

    /**
     * Create a timer that regularly tries to poll the login queue
     * @param username The username
     * @param password The password
     * @return The queue timer
     */
    public QueueTimer waitInQueue(String username, String password);

    /**
     * Wait in the login queue until the result is <tt>OK</tt>
     * @param username The username
     * @param password The password
     * @return The formatted auth token
     */
    public String waitInQueueBlocking(String username, String password);
}
