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

package net.boreeas.riotapi.loginqueue.oldlq;

import lombok.Getter;
import net.boreeas.riotapi.loginqueue.AuthResult;
import net.boreeas.riotapi.loginqueue.Ticker;

import java.util.Collections;
import java.util.List;

/**
 * Created by malte on 7/12/2014.
 */
@Getter
public class AuthResultImpl implements AuthResult {

    private Status status;

    private String token;
    private IngameCredentials inGameCredentials;

    private int delay;
    private int node;
    private List<Ticker> tickers;

    public AuthResultImpl(String token, IngameCredentials inGameCredentials) {
        this.status = Status.OK;
        this.token = token;
        this.inGameCredentials = inGameCredentials;
    }

    public AuthResultImpl(int delay, int node, List<Ticker> tickers) {
        this.status = Status.QUEUE;
        this.delay = delay;
        this.node = node;
        this.tickers = tickers;
    }

    @Override
    public List<Ticker> getTickers() {
        return Collections.unmodifiableList(tickers);
    }

    @Override
    public int getPosition() {
        Ticker ticker = tickers.stream().filter(t -> t.getNode() == node).findFirst().get();
        return ticker.getId() - ticker.getCurrent();
    }

    @Override
    public String getToken() {
        if (status == Status.OK) {
            return token;
        }

        throw new IllegalStateException("Still in queue");
    }
}
