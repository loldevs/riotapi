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

import net.boreeas.riotapi.rtmp.RtmpClient;
import net.boreeas.riotapi.rtmp.RtmpEvent;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * A thin wrapper around RtmpClient that implements the
 * mandatory callbacks as no-ops
 * Created by malte on 7/10/2014.
 */
public class DefaultRtmpClient extends RtmpClient {

    public DefaultRtmpClient(String host, int port, boolean useSSL) {
        super(host, port, useSSL);
    }

    @Override
    public void onReadException(Exception ex) {

    }

    @Override
    public void onAsyncWriteException(IOException ex) {

    }

    @Override
    public void extendedOnPacket(RtmpEvent packet) {

    }
}
