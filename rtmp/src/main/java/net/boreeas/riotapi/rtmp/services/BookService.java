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

package net.boreeas.riotapi.rtmp.services;

import lombok.AllArgsConstructor;
import net.boreeas.riotapi.com.riotgames.platform.summoner.masterybook.MasteryBook;
import net.boreeas.riotapi.rtmp.RtmpClient;
import net.boreeas.riotapi.com.riotgames.platform.summoner.spellbook.RunePage;
import net.boreeas.riotapi.com.riotgames.platform.summoner.spellbook.RunePageBook;

/**
 * Handle runes and masteries
 */
@AllArgsConstructor
public class BookService {
    public static final String SPELL_BOOK_SERVICE = "spellBookService";
    public static final String MASTERY_BOOK_SERVICE = "masteryBookService";
    private RtmpClient client;

    /**
     * Retrieve rune page info for the target player
     * @param summonerId The player's id
     * @return The rune pages
     */
    public RunePageBook getSpellBook(long summonerId) {
        return client.sendRpcAndWait(SPELL_BOOK_SERVICE, "getSpellBook", summonerId);
    }

    /**
     * Select the rune page that should be used by default
     * @param page The page that should be used
     * @return The page?
     */
    public RunePage selectDefaultSpellBookPage(RunePage page) {
        return client.sendRpcAndWait(SPELL_BOOK_SERVICE, "selectDefaultSpellBookPage", page);
    }

    /**
     * Save current rune page info
     * @param spellBook The rune pages
     * @return The rune pages?
     */
    public RunePageBook saveSpellBook(RunePageBook spellBook) {
        return client.sendRpcAndWait(SPELL_BOOK_SERVICE, "saveSpellBook", spellBook);
    }

    /**
     * Retrieve masteries for the target playwer
     * @param summonerId The id of the player
     * @return The masteries
     */
    public MasteryBook getMasteryBook(long summonerId) {
        return client.sendRpcAndWait(MASTERY_BOOK_SERVICE, "getMasteryBook", summonerId);
    }

    /**
     * Save current masteries
     * @param masteryBook The masteries to save
     * @return The masteries?
     */
    public MasteryBook saveMasteryBook(MasteryBook masteryBook) {
        return client.sendRpcAndWait(MASTERY_BOOK_SERVICE, "saveMasteryBook", masteryBook);
    }
}
