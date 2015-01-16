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
    public static final int SR_5v5_NORMAL_CAP = 61;
    /**
     * For summoners rift normal blind games
     */
    public static final int SR_5v5_NORMAL_BLIND = 2;
    /**
     * For summoner rift normal draft games
     */
    public static final int SR_5v5_NORMAL_DRAFT = 14;
    /**
     * For summoners rift ranked solo and duo queue
     */
    public static final int SR_5v5_RANKED_SOLO = 4;
    /**
     * For summoners rift ranked team games
     */
    public static final int SR_5v5_RANKED_TEAM = 42;
    /**
     * For twisted treeline normal blind games
     */
    public static final int TT_3v3_NORMAL_BLIND = 8;
    /**
     * For twisted treeline ranked team games
     */
    public static final int TT_3v3_RANKED_TEAM = 41;
    /**
     * For crystal scar normal blind games
     */
    public static final int ODIN_5v5_NORMAL_BLIND = 16;
    /**
     * For crystal scar normal draft games
     */
    public static final int ODIN_5v5_NORMAL_DRAFT = 17;
    /**
     * For howling abyss normal aram games
     */
    public static final int ARAM_5v5_NORMAL_ARAM= 65;
    /**
     * For summoners rift intro bot games
     */
    public static final int SR_5v5_BOT_INTRO = 31;
    /**
     * For summoners rift easy bot games
     */
    public static final int SR_5v5_BOT_EASY = 31;
    /**
     * For summoners rift medium bot games
     */
    public static final int SR_5v5_BOT_MEDIUM = 31;
    /**
     * For twisted treeline easy and medium bot games
     */
    public static final int TT_3v3_BOT_EASY_AND_MEDIUM = 52;
    /**
     * For crystal scar easy and medium bot games
     */
    public static final int ODIN_5v5_BOT_EASY_AND_MEDIUM = 25;
    /**
     * For nemesis draft featured games
     */
    public static final int FEATURED_COUNTER_PICK = 310;
    /**
     * For one-for-all featured games
     */
    public static final int FEATURED_ONE_FOR_ALL = 70;
    /**
     * For one-for-all in mirror mode featured games.
     */
    public static final int FEATURED_ONE_FOR_ALL_MIRROR_MODE = 78;
    /**
     * For one-versus-one featured games
     */
    public static final int FEATURED_SHOWDOWN_1v1 = 72;
    /**
     * For two-versus-two featured games
     */
    public static final int FEATURED_SHOWDOWN_2v2 = 73;
    /**
     * For hexakill on summoners rift featured games
     */
    public static final int FEATURED_HEXAKILL_SR = 75;
    /**
     * For hexakill on twisted treeline featured games
     */
    public static final int FEATURED_HEXAKILL_TT = 98;
    /**
     * For urf featured games
     */
    public static final int FEATURED_URF = 76;
    /**
     * For ascension featured games
     */
    public static final int FEATURED_ASCENSION = 96;
    /**
     * For poroking featured games
     */
    public static final int FEATURED_POROKING = 300;
    /**
     * For stage one doom bots
     */
    public static final int FEATURED_DOOM_BOTS_STAGE_1 = 91;
    /**
     * For stage two doom bots
     */
    public static final int FEATURED_DOOM_BOTS_STAGE_2 = 92;
    /**
     * For stage five doom bots
     */
    public static final int FEATURED_DOOM_BOTS_STAGE_5 = 93;
    /**
     * For URF versus bots
     */
    public static final int FEATURED_URF_BOTS = 83;
}
