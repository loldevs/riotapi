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

package net.boreeas.riotapi.spectator.rest;

import lombok.*;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.spectator.SpectatorApiHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created on 4/28/2014.
 */
@Getter
@ToString
public class GameMetaData {

    @Getter(AccessLevel.NONE) private GameKey gameKey;
    private int gameId;
    private String platformId;
    private String gameServerAddress;
    private int port;
    @Setter private String encryptionKey;
    /**
     * Time between chunks in milliseconds
     */
    private long chunkTimeInterval;
    /**
     * Start time of the game in human-readable format
     */
    private String startTime;
    /**
     * End time of the game in human-readable format
     */
    private String endTime;
    private boolean gameEnded;
    private int lastChunkId;
    private int lastKeyframeId;
    private int endStartupChunkId;
    /**
     * Delay of the spectator stream in milliseconds
     */
    private int delayTime;
    private List<AvailableChunkInfo> pendingAvailableChunkInfo = new ArrayList<>();
    private List<AvailableKeyFrameInfo> pendingAvailableKeyFrame = new ArrayList<>();
    private long keyFrameTimeInterval;
    private String decodedEncryptionKey;
    private int startGameChunkId;
    private long gameLength;
    /**
     * Minimum delay of the spectator stream in milliseconds?
     */
    private long clientAddedLag;
    private boolean clientBackFetchingEnabled;
    private long clientBackFetchingFreq;
    private int interestScore;
    private boolean featuredGame;
    private String createTime;
    private int endGameChunkId;
    private int endGameKeyFrameId;


    @SneakyThrows
    public Date getStartTimeAsDate() {
        return SpectatorApiHandler.DATE_FMT.parse(startTime);
    }

    @SneakyThrows
    public Date getEndTimeAsDate() {
        return SpectatorApiHandler.DATE_FMT.parse(endTime);
    }

    @SneakyThrows
    public Date getCreateTimeAsDate() {
        return SpectatorApiHandler.DATE_FMT.parse(createTime);
    }

    public long getGameId() {
        return gameKey == null ? gameId : gameKey.gameId;
    }

    public Shard getPlatform() {
        return Shard.getBySpectatorPlatform(gameKey == null ? platformId : gameKey.platformId);
    }


    private class GameKey {
        private long gameId;
        private String platformId;
    }
}
