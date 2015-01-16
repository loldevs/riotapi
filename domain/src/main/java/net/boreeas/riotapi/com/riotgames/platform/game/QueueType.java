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

package net.boreeas.riotapi.com.riotgames.platform.game;

import com.google.gson.annotations.SerializedName;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

/**
 * All queue types. May contain overlaps, since different names are used by different parts of the API.
 * Created on 4/12/2014.
 */
@Serialization(name = "com.riotgames.platform.game.QueueType", deserializeOnly = true)
public enum QueueType {
    ASCENSION               (Flags.FEATURED),
    ARAM                    (Flags.ARAM),
    ARAM_5x5                (Flags.ARAM),
    ARAM_UNRANKED_1x1       (Flags.ARAM),
    ARAM_UNRANKED_2x2       (Flags.ARAM),
    ARAM_UNRANKED_3x3       (Flags.ARAM),
    ARAM_UNRANKED_5x5       (Flags.ARAM),
    ARAM_UNRANKED_6x6       (Flags.ARAM),
    BOT                     (Flags.BOT),
    BOT_3x3                 (Flags.BOT),
    BOT_5x5                 (Flags.BOT),
    BOT_5x5_BEGINNER        (Flags.BOT),
    BOT_5x5_INTERMEDIATE    (Flags.BOT),
    BOT_5x5_INTRO           (Flags.BOT),
    BOT_ODIN_5x5            (Flags.BOT | Flags.DOMINION),
    BOT_TT_3x3              (Flags.BOT),
    BOT_URF_5x5             (Flags.BOT),
    CAP1x1                  (Flags.TEAM_BUILDER),
    CAP5x5                  (Flags.TEAM_BUILDER),
    CLASSIC,
    COUNTER_PICK            (Flags.FEATURED | Flags.DRAFT),
    CUSTOM,
    FEATURED                (Flags.FEATURED),
    FEATURED_BOT            (Flags.BOT | Flags.FEATURED),
    FIRSTBLOOD              (Flags.FEATURED),
    FIRSTBLOOD_1x1          (Flags.FEATURED),
    FIRSTBLOOD_2x2          (Flags.FEATURED),
    GROUP_FINDER_5x5        (Flags.TEAM_BUILDER),
    KINGPORO                (Flags.FEATURED),
    KING_PORO               (Flags.FEATURED),
    KING_PORO_5x5           (Flags.FEATURED),
    @SerializedName("KINGPORO-5X5") // Don't even ask
    KING_PORO_5x5_2         (Flags.FEATURED),
    NIGHTMARE_BOT           (Flags.BOT | Flags.FEATURED),
    NIGHTMARE_BOT_5x5_RANK1 (Flags.BOT | Flags.FEATURED),
    NIGHTMARE_BOT_5x5_RANK2 (Flags.BOT | Flags.FEATURED),
    NIGHTMARE_BOT_5x5_RANK5 (Flags.BOT | Flags.FEATURED),
    NONE,
    NORMAL,
    NORMAL_3x3,
    NORMAL_5x5_BLIND,
    NORMAL_5x5_DRAFT        (Flags.DRAFT),
    ODIN                    (Flags.DOMINION),
    ODIN_5x5_BLIND          (Flags.DOMINION),
    ODIN_5x5_DRAFT          (Flags.DOMINION | Flags.DRAFT),
    ODIN_RANKED_PREMADE     (Flags.RANKED | Flags.DRAFT | Flags.PREMADE | Flags.DOMINION),
    ODIN_RANKED_SOLO        (Flags.RANKED | Flags.DRAFT | Flags.DOMINION),
    ODIN_UNRANKED           (Flags.DOMINION),
    ONEFORALL               (Flags.FEATURED),
    ONEFORALL_5x5           (Flags.FEATURED),
    RANKED_PREMADE_3x3      (Flags.RANKED | Flags.DRAFT | Flags.PREMADE),
    RANKED_PREMADE_5x5      (Flags.RANKED | Flags.DRAFT | Flags.PREMADE),
    RANKED_SOLO_1x1         (Flags.RANKED | Flags.DRAFT),
    RANKED_SOLO_3x3         (Flags.RANKED | Flags.DRAFT),
    RANKED_SOLO_5x5         (Flags.RANKED | Flags.DRAFT),
    RANKED_TEAM_3x3         (Flags.RANKED | Flags.DRAFT | Flags.PREMADE),
    RANKED_TEAM_5x5         (Flags.RANKED | Flags.DRAFT | Flags.PREMADE),
    SR_6x6                  (Flags.FEATURED),
    TUTORIAL,
    URF                     (Flags.FEATURED),
    URF_5x5                 (Flags.FEATURED),
    URF_BOT                 (Flags.FEATURED | Flags.BOT);

    private final int flags;

    private QueueType() {
        this(0);
    }

    private QueueType(int flags) {
        this.flags = flags;
    }

    /**
     * @return Whether this queue is played against bots.
     */
    public boolean isBots() {
        return (flags & Flags.BOT) != 0;
    }

    /**
     * @return Whether this queue is ranked.
     */
    public boolean isRanked() {
        return (flags & Flags.RANKED) != 0;
    }

    /**
     * @return Whether the pick mode for this game mode is draft.
     */
    public boolean isDraft() {
        return (flags & Flags.DRAFT) != 0;
    }

    /**
     * @return Whether this is a featured, limited-time queue.
     */
    public boolean isFeatured() {
        return (flags & Flags.FEATURED) != 0;
    }

    /**
     * @return Whether this is an ARAM queue.
     */
    public boolean isAram() {
        return (flags & Flags.ARAM) != 0;
    }

    /**
     * @return Whether this is a teambuilder queue.
     */
    public boolean isTeambuilder() {
        return (flags & Flags.TEAM_BUILDER) != 0;
    }

    /**
     * @return Whether this is a dominion queue.
     */
    public boolean isDominion() {
        return (flags & Flags.DOMINION) != 0;
    }

    /**
     * Tests whether this queue has all specified flags set.
     * @see net.boreeas.riotapi.com.riotgames.platform.game.QueueType.Flags
     * @param flags The flags to test.
     * @return <code>false</code> if at least one specified flag has not been set, <code>true</code> otherwise.
     */
    public boolean hasFlags(int flags) {
        return (this.flags & flags) >= flags;
    }


    public static QueueType getByName(String name) {
        for (QueueType type: values()) {
            if (type.name().equals(name)) {
                return type;
            }
        }

        return null;
    }

    public static class Flags {
        public static final int BOT         = 1;
        public static final int RANKED      = 1 << 1;
        public static final int DRAFT       = 1 << 2;
        public static final int FEATURED    = 1 << 3;
        public static final int ARAM        = 1 << 4;
        public static final int PREMADE     = 1 << 5;
        public static final int DOMINION    = 1 << 6;
        public static final int TEAM_BUILDER= 1 << 7;
    }
}
