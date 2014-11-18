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

package net.boreeas.riotapi.constants;

/**
 * @author Malte Schütze
 */
public enum SummonerSpell {
    REVIVE(0x05C8B3A5),
    SMITE(0x065E8695),
    EXHAUST(0x08A8BAE4),
    BARRIER(0x0CCFB982),
    TELEPORT(0x004F1364),
    GHOST(0x064ACC95),
    HEAL(0x0364AF1C),
    CLEANSE(0x064D2094),
    CLARITY(0x03657421),
    IGNITE(0x06364F24),
    PROMOTE(0x0410FF72),
    CLAIRVOYANCE(0x09896765),
    FLASH(0x06496EA8),
    TEST(0x0103D94C);


    public final int spectatorHash;

    private SummonerSpell(int spectatorHash) {
        this.spectatorHash = spectatorHash;
    }

    public static SummonerSpell getByHash(int hash) {
        for (SummonerSpell spell: values()) {
            if (spell.spectatorHash == hash) {
                return spell;
            }
        }

        throw new IllegalArgumentException("Unknown summoner spell spectatorHash " + hash);
    }
}
