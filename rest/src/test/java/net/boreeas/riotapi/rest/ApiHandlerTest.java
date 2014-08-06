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

/**
 * Created on 4/12/2014.
 */
public class ApiHandlerTest extends TestCase {
/*
    public static final String API_KEY = "changeme";
    public static final String TEAM_ID = "changeme";
    public static final int SUMMONER_ID_1 = -1;
    public static final int SUMMONER_ID_2 = -1;
    public static final String SUMMONER_NAME_1 = "changeme";
    public static final String SUMMONER_NAME_2 = "changeme";

    private ApiHandler handler;

    public void setUp() throws Exception {
        super.setUp();
        handler = new ApiHandler(Shard.EUW, API_KEY);
    }

    public void testGetBasicChampData() throws Exception {
        try {
            Thread.sleep(1500);
            List<BasicChampData> euwBasicChampDatas = handler.getBasicChampData();
            assertNotNull(euwBasicChampDatas);
            assertFalse(euwBasicChampDatas.isEmpty());
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetFreeToPlayChampions() throws Exception {
        try {
            Thread.sleep(1500);
            assertNotNull(handler.getFreeToPlayChampions());
            assertEquals(handler.getFreeToPlayChampions().size(), 10);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetChampionBasicData() throws Exception {
        try {
            Thread.sleep(1500);
            handler.getBasicChampData(1);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetRecentGames() throws Exception {
        try {
            Thread.sleep(1500);
            handler.getRecentGames(SUMMONER_ID_2);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetLeagues() throws Exception {
        try {
            Thread.sleep(1500);
            handler.getLeagues(SUMMONER_ID_2);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetLeagueEntries() throws Exception {
        try {
            Thread.sleep(1500);
            handler.getLeagueEntries(SUMMONER_ID_1);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetLeaguesByTeam() throws Exception {
        try {
            Thread.sleep(1500);
            handler.getLeagues(TEAM_ID);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetLeagueEntriesByTeam() throws Exception {
        try {
            Thread.sleep(1500);
            handler.getLeagueEntries(TEAM_ID);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetChallenger() throws Exception {
        try {
            Thread.sleep(1500);
            handler.getChallenger(QueueType.RANKED_SOLO_5v5);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetChampion() throws Exception {
        try {
            handler.getChampion(1, ChampData.ALL);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetChampions() throws Exception {
        try {
            handler.getChampions(ChampData.ALL);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetItems() {
        try {
            handler.getItemList(ItemData.ALL);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetItem() {
        try {
            handler.getItem(2003, ItemData.CONSUME_ON_FULL);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetMasteries() {
        try {
            handler.getMasteries(MasteryData.ALL);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetMastery() {
        try {
            handler.getMastery(4353, MasteryData.SANITIZED_DESCRIPTION);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetRealm() {
        try {
            handler.getRealm();
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetRunes() {
        try {
            handler.getRuneList(ItemData.ALL);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetRune() {
        try {
            handler.getRune(5235, ItemData.HIDE_FROM_ALL);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetSummonerSpells() {
        try {
            handler.getSummonerSpells(SpellData.COOLDOWN_BURN);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetSummonerSpell() {
        try {
            handler.getSummonerSpell(1, SpellData.ALL);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetRankedStats() throws InterruptedException {
        try {
            Thread.sleep(1500);
            handler.getRankedStats(SUMMONER_ID_1);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetStatSummary() throws InterruptedException {
        try {
            Thread.sleep(1500);
            handler.getStatsSummary(SUMMONER_ID_1, Season.SEASON3);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetSummoners() throws InterruptedException {
        try {
            Thread.sleep(1500);
            handler.getSummoners(SUMMONER_NAME_1, SUMMONER_NAME_2);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetSummonersById() throws InterruptedException {
        try {
            Thread.sleep(1500);
            handler.getSummoners(SUMMONER_ID_1, SUMMONER_ID_2);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetSummoner() throws InterruptedException {
        try {
            Thread.sleep(1500);
            handler.getSummoner(SUMMONER_NAME_2);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetSummonerById() throws InterruptedException {
        try {
            Thread.sleep(1500);
            handler.getSummoner(SUMMONER_ID_2);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetRunePages() throws InterruptedException {
        try {
            Thread.sleep(1500);
            handler.getRunePages(SUMMONER_ID_2);
            handler.getRunePagesMultipleUsers(SUMMONER_ID_2, SUMMONER_ID_1);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetMasteryPages() throws InterruptedException {
        try {
            Thread.sleep(1500);
            handler.getMasteryPages(SUMMONER_ID_1);
            handler.getMasteryPagesMultipleUsers(SUMMONER_ID_1, SUMMONER_ID_2);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetNames() throws InterruptedException {
        try {
            Thread.sleep(1500);
            handler.getSummonerName(SUMMONER_ID_2);
            handler.getSummonerNames(SUMMONER_ID_1, SUMMONER_ID_2);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }

    public void testGetTeams() throws InterruptedException {
        try {
            Thread.sleep(1500);
            handler.getTeamsBySummoner(SUMMONER_ID_1);
            handler.getTeamsBySummoner(TEAM_ID);
        } catch (RequestException ex) {
            System.out.println("### PASS with RequestException");
            System.out.println(ex.getMessage());
        }
    }
//*/

    public void test() {}
}
