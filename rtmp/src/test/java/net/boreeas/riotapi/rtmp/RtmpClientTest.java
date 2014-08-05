package net.boreeas.riotapi.rtmp;

import junit.framework.TestCase;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import net.boreeas.riotapi.QueueType;
import net.boreeas.riotapi.Season;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.com.riotgames.platform.game.GameMode;
import net.boreeas.riotapi.com.riotgames.platform.login.impl.ClientVersionMismatchException;
import net.boreeas.riotapi.com.riotgames.platform.matchmaking.GameQueueConfig;
import net.boreeas.riotapi.com.riotgames.platform.matchmaking.MatchMakerParams;
import net.boreeas.riotapi.com.riotgames.platform.matchmaking.SearchingForMatchNotification;
import net.boreeas.riotapi.com.riotgames.platform.summoner.runes.SummonerRuneInventory;
import net.boreeas.riotapi.com.riotgames.platform.summoner.spellbook.RunePageBook;
import net.boreeas.riotapi.loginqeue.LoginQueue;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Log4j
public class RtmpClientTest extends TestCase {

    private static Properties testConfig = new Properties();;
    private static Shard shard = Shard.EUW;
    private static RtmpClient client;
    private static long summonerId;
    private static long accountId;

    static {
        staticSetup();
    }

    @SneakyThrows
    private static void staticSetup() {

        testConfig.load(new InputStreamReader(new FileInputStream("testconfig.properties")));

        client = new DefaultRtmpClient(shard.prodUrl, Shard.RTMPS_PORT, true);
        //client = new DefaultRtmpClient("localhost", 2099, true);

        String user = testConfig.getProperty("user");
        String pass = testConfig.getProperty("pass");
        String authKey = new LoginQueue(shard).waitInQueue(user, pass).await().getToken();

        try {
            client.connect();
            client.authenticate(user, pass, authKey, "4.13.14_07_25_15_10");
        } catch (ClientVersionMismatchException ex) {
            log.info("Reconnecting with version " + ex.getCurrentVersion());
            client.authenticate(user, pass, new LoginQueue(shard).waitInQueue(user, pass).await().getToken(), ex.getCurrentVersion());
        }

        summonerId = client.getLoginDataPacket().getAllSummonerData().getSummoner().getSumId();
        accountId = client.getLoginDataPacket().getAllSummonerData().getSummoner().getAcctId();
    }

    public void testGetAccountState() {
        client.accountService.getAccountState();
    }

    public void testGetMasteryBook() {
        client.bookService.getMasteryBook(summonerId);
    }

    public void testGetSpellBook() {
        RunePageBook book = client.bookService.getSpellBook(summonerId);
    }

    public void testSaveMasteryBook() {
        client.bookService.saveMasteryBook(client.bookService.getMasteryBook(summonerId));
    }

    public void testSaveSpellbook() {
        System.out.println("Inspect: BookService.saveSpellBook");
        System.out.println(client.bookService.saveSpellBook(client.bookService.getSpellBook(summonerId)));
    }

    public void testSelectDefaultSpellBookPage() {
        client.bookService.selectDefaultSpellBookPage(client.bookService.getSpellBook(summonerId).getBookPages().get(0));
    }

    public void testGetAvailableChampions() {
        client.inventoryService.getAvailableChampions();
    }

    public void testGetSummonerActiveBoosts() {
        client.inventoryService.getSummonerActiveBoosts();
    }

    public void testGetPointsBalance() {
        client.lcdsRerollService.getPointsBalance();
    }

    public void testGetAllLeaguesForPlayer() {
        client.leaguesServiceProxy.getAllLeaguesForPlayer(summonerId);
    }

    public void testGetAllMyLeagues() {
        client.leaguesServiceProxy.getAllMyLeagues();
    }

    public void testGetChallengerLeague() {
        client.leaguesServiceProxy.getChallengerLeague(QueueType.RANKED_SOLO_5v5);
    }

    public void testGetMyLeaguePositions() {
        client.leaguesServiceProxy.getMyLeaguePositions();
    }

    public void testGetLeaguesForTeam() {
        client.leaguesServiceProxy.getLeaguesForTeam(testConfig.getProperty("lookupTeamname"));
    }

    public void testGetStoreUrl() {
        client.loginService.getStoreUrl();
    }

    public void testLcdsHeartBeat() {
        client.loginService.performLcdsHeartBeat(client.getLoginDataPacket().getAllSummonerData().getSummoner().getAcctId(), client.getSession().getToken(), 1);
    }

    public void testGetAvailableQueues() {
        client.matchmakerService.getAvailableQueues();
    }

    public void testGetQueueInfo() {
        client.matchmakerService.getQueueInfo(client.matchmakerService.getAvailableQueues().get(0).getId());
    }

    public void testQueueAttachDetach() {
        MatchMakerParams params = new MatchMakerParams();
        List<Long> ids = client.matchmakerService.getAvailableQueues().parallelStream().map(GameQueueConfig::getId).collect(Collectors.toList());
        params.setQueueIds(ids);
        params.setBotDifficulty("BEGINNER");

        SearchingForMatchNotification search = client.matchmakerService.attachToQueue(params);
//        assertTrue(client.matchmakerService.cancelFromQueueIfPossible(client.getLoginDataPacket().getAllSummonerData().getSummoner().getAcctId()));
    }

    public void testPurgeFromQueues() {
        MatchMakerParams params = new MatchMakerParams();
        List<Long> ids = client.matchmakerService.getAvailableQueues().parallelStream().map(GameQueueConfig::getId).collect(Collectors.toList());
        params.setQueueIds(ids);
        params.setBotDifficulty("BEGINNER");

        SearchingForMatchNotification search = client.matchmakerService.attachToQueue(params);
        Object o = client.matchmakerService.purgeFromQueues();
        System.out.println("Inspect: matchmakerService.purgeFromQueues");
        System.out.println(o);
    }

    public void testProcessEloQuestionaire() {
        System.out.println("Inspect: playerStatsService.processEloQuestionaire");
        Object obj = client.playerStatsService.processEloQuestionaire("NOVICE");
        System.out.println(obj);
    }

    public void testGetAggregatedStats() {
        client.playerStatsService.getAggregatedStats(accountId, Season.SEASON4, GameMode.CLASSIC);
    }

    public void testGetRecentGames() {
        client.playerStatsService.getRecentGames(accountId);
    }

    public void testGetTeamAggregatedStats() {
        client.playerStatsService.getTeamAggregatedStats(client.summonerTeamService.findTeamByName(testConfig.getProperty("lookupTeamname")).getTeamId());
    }

    public void testGetSummonerIconInventory() {
        client.summonerIconService.getSummonerIconInventory(summonerId);
    }

    public void testGetSummonerRuneInventory() {
        SummonerRuneInventory runes = client.summonerRuneService.getSummonerRuneInventory(summonerId);
        System.out.println("Inspect: SummonerRuneInventory.json");
        System.out.println(runes.getSummonerRunesJson());
    }

    public void testGetAllPublicSummonerDataByAccount() {
        client.summonerService.getAllPublicSummonerDataByAccount(accountId);
    }

    public void testGetAllSummonerDataByAccount() {
        client.summonerService.getAllSummonerDataByAccount(accountId);
    }

    public void testGetSummonerByName() {
        client.summonerService.getSummonerByName(client.getLoginDataPacket().getAllSummonerData().getSummoner().getName());
    }

    public void testGetSummonerInternalNameByName() {
        client.summonerService.getSummonerInternalNameByName(client.getLoginDataPacket().getAllSummonerData().getSummoner().getName());
    }

    public void testUpdateProfileIcon() {
        // IconInventory is empty
        //client.summonerService.updateProfileIconId(client.summonerIconService.getSummonerIconInventory(summonerId).getSummonerIcons().get(0).getIconId());
    }

    public void testFindTeamByName() {
        client.summonerTeamService.findTeamByName(testConfig.getProperty("lookupTeamname"));
    }

    public void testFindTeamByTeamId() {
        client.summonerTeamService.findTeamById(client.summonerTeamService.findTeamByName(testConfig.getProperty("lookupTeamname")).getTeamId());
    }

    public void testIsNameValidAndAvailable() {
        client.summonerTeamService.isNameValidAndAvailable("Test Team");
    }

    public void testIsTagValidAndAvailablge() {
        client.summonerTeamService.isTagValidAndAvailable("TEST");
    }
}