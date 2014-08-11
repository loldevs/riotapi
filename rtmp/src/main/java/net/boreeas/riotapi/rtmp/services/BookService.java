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
 * Created by malte on 7/16/2014.
 */
@AllArgsConstructor
public class BookService {
    private static final String SPELL_BOOK_SERVICE = "spellBookService";
    private static final String MASTERY_BOOK_SERVICE = "masteryBookService";
    private RtmpClient client;

    public RunePageBook getSpellBook(long summonerId) {
        return client.sendRpcAndWait(SPELL_BOOK_SERVICE, "getSpellBook", summonerId);
    }

    public RunePage selectDefaultSpellBookPage(RunePage page) {
        return client.sendRpcAndWait(SPELL_BOOK_SERVICE, "selectDefaultSpellBookPage", page);
    }

    public RunePageBook saveSpellBook(RunePageBook spellBook) {
        return client.sendRpcAndWait(SPELL_BOOK_SERVICE, "saveSpellBook", spellBook);
    }

    public MasteryBook getMasteryBook(long summonerId) {
        return client.sendRpcAndWait(MASTERY_BOOK_SERVICE, "getMasteryBook", summonerId);
    }

    public MasteryBook saveMasteryBook(MasteryBook masteryBook) {
        return client.sendRpcAndWait(MASTERY_BOOK_SERVICE, "saveMasteryBook", masteryBook);
    }
}
