/*
 * Copyright 2014 Malte Schütze
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.boreeas.riotapi.rest;

import junit.framework.TestCase;
import net.boreeas.riotapi.RequestException;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.com.riotgames.platform.game.QueueType;
import net.boreeas.riotapi.constants.Season;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * Created on 4/12/2014.
 */
public class ApiHandlerTest extends TestCase {

    static final Properties properties = new Properties();

    public static String API_KEY;
    public static String TEAM_ID;
    public static int SUMMONER_ID_1;
    public static int SUMMONER_ID_2;
    public static String SUMMONER_NAME_1;
    public static String SUMMONER_NAME_2;

    static {
        try {
            properties.load(new FileInputStream(new File("testconfig.properties")));
            API_KEY = properties.getProperty("rest.apikey");
            TEAM_ID = properties.getProperty("rest.teamid");
            SUMMONER_ID_1 = Integer.parseInt(properties.getProperty("rest.summId1"));
            SUMMONER_ID_2 = Integer.parseInt(properties.getProperty("rest.summId2"));
            SUMMONER_NAME_1 = properties.getProperty("rest.summName1");
            SUMMONER_NAME_2 = properties.getProperty("rest.summName2");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ThrottledApiHandler handler;

    public void setUp() throws Exception {
        super.setUp();
        handler = ThrottledApiHandler.developmentDefault(Shard.EUW, API_KEY);
    }

    public void testGetBasicChampData() throws Exception {
        try {
            List<BasicChampData> euwBasicChampDatas = handler.getBasicChampData().get(1, MINUTES);
            assertNotNull(euwBasicChampDatas);
            assertFalse(euwBasicChampDatas.isEmpty());
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetFreeToPlayChampions() throws Exception {
        try {
            Future<List<BasicChampData>> ftpChamps = handler.getFreeToPlayChampions();
            assertNotNull(ftpChamps.get(1, MINUTES));
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetChampionBasicData() throws Exception {
        try {
            handler.getBasicChampData(1).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetRecentGames() throws Exception {
        try {
            handler.getRecentGames(SUMMONER_ID_2).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetLeagues() throws Exception {
        try {
            handler.getLeagues(SUMMONER_ID_2).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }

    }

    public void testGetLeagueEntries() throws Exception {
        try {
            handler.getLeagueEntries(SUMMONER_ID_1).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetLeaguesByTeam() throws Exception {
        try {
            handler.getLeagues(TEAM_ID).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetLeagueEntriesByTeam() throws Exception {
        try {
            handler.getLeagueEntries(TEAM_ID).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetChallenger() throws Exception {
        try {
            handler.getChallenger(QueueType.RANKED_SOLO_5x5).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetChampion() throws Exception {
        try {
            handler.getChampion(1, ChampData.ALL).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetChampions() throws Exception {
        try {
            handler.getChampions(ChampData.ALL).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetItems() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getItemList(ItemData.ALL).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetItem() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getItem(2003, ItemData.CONSUME_ON_FULL).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetMasteries() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getMasteries(MasteryData.ALL).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetMastery() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getMastery(4353, MasteryData.SANITIZED_DESCRIPTION).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetRealm() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getRealm().get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetRunes() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getRuneList(ItemData.ALL).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetRune() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getRune(5235, ItemData.HIDE_FROM_ALL).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetSummonerSpells() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getSummonerSpells(SpellData.COOLDOWN_BURN).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetSummonerSpell() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getSummonerSpell(1, SpellData.ALL).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetRankedStats() throws InterruptedException, TimeoutException, ExecutionException {
        try {
            handler.getRankedStats(SUMMONER_ID_1).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetStatSummary() throws InterruptedException, TimeoutException, ExecutionException {
        try {
            handler.getStatsSummary(SUMMONER_ID_1, Season.SEASON3).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetSummoners() throws InterruptedException, TimeoutException, ExecutionException {
        try {
            handler.getSummoners(SUMMONER_NAME_1, SUMMONER_NAME_2).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetSummonersById() throws InterruptedException, TimeoutException, ExecutionException {
        try {
            handler.getSummoners(SUMMONER_ID_1, SUMMONER_ID_2).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetSummoner() throws InterruptedException, TimeoutException, ExecutionException {
        try {
            handler.getSummoner(SUMMONER_NAME_2).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetSummonerById() throws InterruptedException, TimeoutException, ExecutionException {
        try {
            handler.getSummoner(SUMMONER_ID_2).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetRunePages() throws InterruptedException, TimeoutException, ExecutionException {
        try {
            handler.getRunePages(SUMMONER_ID_2).get(1, MINUTES);
            handler.getRunePagesMultipleUsers(SUMMONER_ID_2, SUMMONER_ID_1).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }

    }

    public void testGetMasteryPages() throws InterruptedException, TimeoutException, ExecutionException {
        try {
            handler.getMasteryPages(SUMMONER_ID_1).get(1, MINUTES);
            handler.getMasteryPagesMultipleUsers(SUMMONER_ID_1, SUMMONER_ID_2).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }

    }

    public void testGetNames() throws InterruptedException, TimeoutException, ExecutionException {
        try {
            handler.getSummonerName(SUMMONER_ID_2).get(1, MINUTES);
            handler.getSummonerNames(SUMMONER_ID_1, SUMMONER_ID_2).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetTeams() throws InterruptedException, TimeoutException, ExecutionException {
        try {
            handler.getTeams(SUMMONER_ID_1).get(1, MINUTES);
            handler.getTeams(TEAM_ID).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetMatchHistory() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getMatchHistory(SUMMONER_ID_1).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetMatchDetails() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getMatch(handler.getMatchHistory(SUMMONER_ID_2).get(1, MINUTES).get(0).getMatchId()).get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testConcat() {
        assertEquals("1", concat(1));
        assertEquals("1,2,3", concat(1, 2, 3));
    }

    private String concat(long... values) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (long v : values) {
            if (first) {
                first = false;
            } else {
                builder.append(",");
            }

            builder.append(v);
        }

        return builder.toString();
    }

    public void testGetShards() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getShards().get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetShardStatus() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            List<ShardData> shardDatas = handler.getShards().get(1, MINUTES);
            for (ShardData data : shardDatas) {
                System.out.println(data);
                handler.getShardStatus(Shard.get(data.getSlug()));
            }
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (ex.getErrorType() == null) System.out.println("Unknown error code: " + ex.getErrorCode());
            if (ex.getErrorCode() < 500 || ex.getErrorCode() > 599) throw ex;
        }
    }

    public void testGetMaps() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getMaps().get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetVersions() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getVersions().get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetLocales() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getLocales().get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetLocalizedMessages() throws InterruptedException, ExecutionException, TimeoutException {
        try {
            handler.getLocalizedMessages().get(1, MINUTES);
        } catch (RequestException ex) {
            // 5xx errors indicate serverside issues
            if (isNotServerside(ex)) throw ex;
        }
    }

    public void testGetFeaturedGames() {
        try {
            handler.featuredGamesHandler.getFeaturedGames();
        } catch (RuntimeException ex) {
            // Runtime -> Execution -> Request
            if (throwForward(ex)) {
                throw ex;
            }
        }
    }

    private boolean throwForward(RuntimeException ex) {
        return !(ex.getCause().getCause() instanceof RequestException) || isNotServerside((RequestException) ex.getCause().getCause());
    }

    private boolean isNotServerside(RequestException ex) {
        return ex.getErrorType().code < 500 || ex.getErrorType().code > 599;
    }
}
