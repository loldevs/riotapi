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

package net.boreeas.riotapi.constants;

/**
 * The ids for different queues. Not in {@link net.boreeas.riotapi.com.riotgames.platform.game.QueueType} since no
 * direct 1-1 mapping exists.
 * @author Malte Schütze
 */
public class QueueId {
    /**
     * For summoners rift team builder games
     */
    public final int SR_5v5_NORMAL_CAP = 61;
    /**
     * For summoners rift normal blind games
     */
    public final int SR_5v5_NORMAL_BLIND = 2;
    /**
     * For summoner rift normal draft games
     */
    public final int SR_5v5_NORMAL_DRAFT = 14;
    /**
     * For summoners right ranked solo and duo queue
     */
    public final int SR_5v5_RANKED_SOLO = 4;
    /**
     * For twisted treeline normal blind games
     */
    public final int TT_3v3_NORMAL_BLIND = 8;
    /**
     * For crystal scar normal blind games
     */
    public final int ODIN_5v5_NORMAL_BLIND = 16;
    /**
     * For crystal scar normal draft games
     */
    public final int ODIN_5v5_NORMAL_DRAFT = 17;
    /**
     * For howling abyss normal aram games
     */
    public final int ARAM_5v5_NORMAL_ARAM= 65;
    /**
     * For summoners rift intro bot games
     */
    public final int SR_5v5_BOT_INTRO = 31;
    /**
     * For summoners rift easy bot games
     */
    public final int SR_5v5_BOT_EASY = 31;
    /**
     * For summoners rift medium bot games
     */
    public final int SR_5v5_BOT_MEDIUM = 31;
    /**
     * For twisted treeline easy and medium bot games
     */
    public final int TT_3v3_BOT_EASY_AND_MEDIUM = 52;
    /**
     * For crystal scar easy and medium bot games
     */
    public final int ODIN_5v5_BOT_EASY_AND_MEDIUM = 25;
}
