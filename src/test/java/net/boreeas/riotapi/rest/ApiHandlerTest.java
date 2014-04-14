package net.boreeas.riotapi.rest;

import junit.framework.TestCase;
import net.boreeas.riotapi.Shard;

import java.util.List;

/**
 * Created on 4/12/2014.
 */
public class ApiHandlerTest extends TestCase {

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
        Thread.sleep(1500);
        List<BasicChampData> euwBasicChampDatas = handler.getBasicChampData();
        assertNotNull(euwBasicChampDatas);
        assertFalse(euwBasicChampDatas.isEmpty());
    }

    public void testGetFreeToPlayChampions() throws Exception {
        Thread.sleep(1500);
        assertNotNull(handler.getFreeToPlayChampions());
        assertEquals(handler.getFreeToPlayChampions().size(), 10);
    }

    public void testGetChampionBasicData() throws Exception {
        Thread.sleep(1500);
        handler.getBasicChampData(1);
    }

    public void testGetRecentGames() throws Exception {
        Thread.sleep(1500);
        handler.getRecentGames(SUMMONER_ID_2);
    }

    public void testGetLeagues() throws Exception {
        Thread.sleep(1500);
        handler.getLeagues(SUMMONER_ID_2);
    }

    public void testGetLeagueEntries() throws Exception {
        Thread.sleep(1500);
        handler.getLeagueEntries(SUMMONER_ID_1);
    }

    public void testGetLeaguesByTeam() throws Exception {
        Thread.sleep(1500);
        handler.getLeaguesByTeam(TEAM_ID);
    }

    public void testGetLeagueEntriesByTeam() throws Exception {
        Thread.sleep(1500);
        handler.getLeagueEntriesByTeam(TEAM_ID);
    }

    public void testGetChallenger() throws Exception {
        Thread.sleep(1500);
        handler.getChallenger(Queue.RANKED_SOLO_5v5);
    }

    public void testGetChampion() throws Exception {
        handler.getChampion(1, ChampData.ALL);
    }

    public void testGetChampions() throws Exception {
        handler.getChampions(ChampData.ALL);
    }

    public void testGetItems() {
        handler.getItemList(ItemData.ALL);
    }

    public void testGetItem() {
        handler.getItem(2003, ItemData.CONSUME_ON_FULL);
    }

    public void testGetMasteries() {
        handler.getMasteries(MasteryData.ALL);
    }

    public void testGetMastery() {
        handler.getMastery(4353, MasteryData.SANITIZED_DESCRIPTION);
    }

    public void testGetRealm() {
        handler.getRealm();
    }

    public void testGetRunes() {
        handler.getRuneList(ItemData.ALL);
    }

    public void testGetRune() {
        handler.getRune(5235, ItemData.HIDE_FROM_ALL);
    }

    public void testGetSummonerSpells() {
        handler.getSummonerSpells(SpellData.COOLDOWN_BURN);
    }

    public void testGetSummonerSpell() {
        handler.getSummonerSpell(1, SpellData.ALL);
    }

    public void testGetRankedStats() throws InterruptedException {
        Thread.sleep(1500);
        handler.getRankedStats(SUMMONER_ID_1);
    }

    public void testGetStatSummary() throws InterruptedException {
        Thread.sleep(1500);
        handler.getStatsSummary(SUMMONER_ID_1, Season.SEASON3);
    }

    public void testGetSummoners() throws InterruptedException {
        Thread.sleep(1500);
        handler.getSummoners(SUMMONER_NAME_1, SUMMONER_NAME_2);
    }

    public void testGetSummonersById() throws InterruptedException {
        Thread.sleep(1500);
        handler.getSummoners(SUMMONER_ID_1, SUMMONER_ID_2);
    }

    public void testGetSummoner() throws InterruptedException {
        Thread.sleep(1500);
        handler.getSummoner(SUMMONER_NAME_2);
    }

    public void testGetSummonerById() throws InterruptedException {
        Thread.sleep(1500);
        handler.getSummoner(SUMMONER_ID_2);
    }

    public void testGetRunePages() throws InterruptedException {
        Thread.sleep(1500);
        handler.getRunePages(SUMMONER_ID_2);
        handler.getRunePagesMultipleUsers(SUMMONER_ID_2, SUMMONER_ID_1);
    }

    public void testGetMasteryPages() throws InterruptedException {
        Thread.sleep(1500);
        handler.getMasteryPages(SUMMONER_ID_1);
        handler.getMasteryPagesMultipleUsers(SUMMONER_ID_1, SUMMONER_ID_2);
    }

    public void testGetNames() throws InterruptedException {
        Thread.sleep(1500);
        handler.getSummonerName(SUMMONER_ID_2);
        handler.getSummonerNames(SUMMONER_ID_1, SUMMONER_ID_2);
    }

    public void testGetTeams() throws InterruptedException {
        Thread.sleep(1500);
        handler.getTeams(SUMMONER_ID_1);
        handler.getTeams(TEAM_ID);
    }
}
