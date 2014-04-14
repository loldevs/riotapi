package net.boreeas.riotapi.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.boreeas.riotapi.RiotUtil;
import net.boreeas.riotapi.Shard;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * Created on 4/12/2014.
 */
public class ApiHandler {

    private static final String API_BASE_URL = "https://prod.api.pvp.net/api/lol";
    private static final String API_KR_BASE_URL = "https://asia.api.pvp.net/api/lol";

    private Gson gson = new Gson();
    private WebTarget championInfoTarget;
    private WebTarget gameInfoTarget;
    private WebTarget leagueInfoTarget;
    private WebTarget staticDataTarget;
    private WebTarget statsTarget;
    private WebTarget summonerInfoTarget;
    private WebTarget teamInfoTarget;

    public ApiHandler(Shard shard, String token) {

        String region = shard.name;

        WebTarget defaultTarget;
        WebTarget defaultStaticTarget;
        if (shard == Shard.KR) { // KR uses seperate server for everything except static-data
            Client c = ClientBuilder.newClient();
            defaultTarget = c.target(API_KR_BASE_URL).queryParam("api_key", token).path(region);
            defaultStaticTarget = c.target(API_BASE_URL).queryParam("api_key", token).path("static-data").path(region);
        } else {
            WebTarget base = ClientBuilder.newClient().target(API_BASE_URL).queryParam("api_key", token);
            defaultTarget = base.path(region);
            defaultStaticTarget = base.path("static-data").path(region);
        }

        championInfoTarget  = defaultTarget.path("v1.2").path("champion");
        gameInfoTarget      = defaultTarget.path("v1.3").path("game/by-summoner");
        leagueInfoTarget    = defaultTarget.path("v2.3").path("league");
        statsTarget         = defaultTarget.path("v1.3").path("stats/by-summoner");
        summonerInfoTarget  = defaultTarget.path("v1.4").path("summoner");
        teamInfoTarget      = defaultTarget.path("v2.2").path("team");

        staticDataTarget    = defaultStaticTarget.path("v1.2");
    }

    public List<BasicChampData> getBasicChampData() {
        return gson.fromJson($(championInfoTarget), BasicChampDataListDto.class).champions;
    }

    public List<BasicChampData> getFreeToPlayChampions() {
        WebTarget tgt = championInfoTarget.queryParam("freeToPlay", true);
        return gson.fromJson($(tgt), BasicChampDataListDto.class).champions;
    }

    public BasicChampData getBasicChampData(int id) {
        return gson.fromJson($(championInfoTarget.path(""+id)), BasicChampData.class);
    }

    public List<Game> getRecentGames(long summoner) {
        WebTarget tgt = gameInfoTarget.path(summoner + "/recent");
        return gson.fromJson($(tgt), RecentGamesDto.class).games;
    }

    public List<League> getLeagues(long summoner) {
        Type type = new TypeToken<List<League>>(){}.getType();
        WebTarget tgt = leagueInfoTarget.path("by-summoner/" + summoner);
        return gson.fromJson($(tgt), type);
    }

    public List<LeagueItem> getLeagueEntries(long summoner) {
        Type type = new TypeToken<List<LeagueItem>>(){}.getType();
        WebTarget tgt = leagueInfoTarget.path("by-summoner/" + summoner).path("entry");
        return gson.fromJson($(tgt), type);
    }


    public List<League> getLeaguesByTeam(String teamId) {
        Type type = new TypeToken<List<League>>(){}.getType();
        WebTarget tgt = leagueInfoTarget.path("by-team").path(teamId);
        return gson.fromJson($(tgt), type);
    }

    public List<LeagueItem> getLeagueEntriesByTeam(String teamId) {
        Type type = new TypeToken<List<LeagueItem>>(){}.getType();
        WebTarget tgt = leagueInfoTarget.path("by-team").path(teamId).path("entry");
        return gson.fromJson($(tgt), type);
    }

    public League getChallenger(Queue queue) {
        WebTarget tgt = leagueInfoTarget.path("challenger").queryParam("type", queue.name);
        return gson.fromJson($(tgt), League.class);
    }

    public ChampionList getChampionListDto() {
        WebTarget tgt = staticDataTarget.path("champion");
        return gson.fromJson($(tgt), ChampionList.class);
    }

    public ChampionList getChampionListDto(ChampData champData) {
        WebTarget tgt = staticDataTarget.path("champion").queryParam("champData", champData.name);
        return gson.fromJson($(tgt), ChampionList.class);
    }

    public ChampionList getChampionListDto(String locale, String version, boolean dataById, ChampData champData) {
        WebTarget tgt = staticDataTarget.path("champion")
                .queryParam("locale", locale)
                .queryParam("version", version)
                .queryParam("dataById", dataById)
                .queryParam("champData", champData.name);
        return gson.fromJson($(tgt), ChampionList.class);
    }

    public Collection<Champion> getChampions() {
        return getChampionListDto().getChampions();
    }

    public Collection<Champion> getChampions(ChampData champData) {
        return getChampionListDto(champData).getChampions();
    }

    public Collection<Champion> getChampions(ChampData champData, String version, String locale, boolean dataById) {
        return getChampionListDto(locale, version, dataById, champData).getChampions();
    }

    public Champion getChampion(int id) {
        WebTarget tgt = staticDataTarget.path("champion/" + id);
        return gson.fromJson($(tgt), Champion.class);
    }

    public Champion getChampion(int id, ChampData champData) {
        WebTarget tgt = staticDataTarget.path("champion/" + id).queryParam("champData", champData.name);
        return gson.fromJson($(tgt), Champion.class);
    }

    public Champion getChampion(int id, ChampData champData, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("champion/" + id)
                .queryParam("champData", champData.name)
                .queryParam("locale", locale)
                .queryParam("version", version);
        return gson.fromJson($(tgt), Champion.class);
    }

    public ItemList getItemList() {
        WebTarget tgt = staticDataTarget.path("item");
        return gson.fromJson($(tgt), ItemList.class);
    }

    public ItemList getItemList(ItemData data) {
        WebTarget tgt = staticDataTarget.path("item").queryParam("itemListData", data.name);
        return gson.fromJson($(tgt), ItemList.class);
    }

    public ItemList getItemList(ItemData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("item")
                .queryParam("itemListData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), ItemList.class);
    }

    public Item getItem(int id) {
        WebTarget tgt = staticDataTarget.path("item/" + id);
        return gson.fromJson($(tgt), Item.class);
    }

    public Item getItem(int id, ItemData data) {
        WebTarget tgt = staticDataTarget.path("item/" + id).queryParam("itemData", data.name);
        return gson.fromJson($(tgt), Item.class);
    }

    public Item getItem(int id, ItemData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("item/" + id)
                .queryParam("itemData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), Item.class);
    }

    public MasteryList getMasteries() {
        WebTarget tgt = staticDataTarget.path("mastery");
        return gson.fromJson($(tgt), MasteryList.class);
    }

    public MasteryList getMasteries(MasteryData data) {
        WebTarget tgt = staticDataTarget.path("mastery").queryParam("masteryListData", data.name);
        return gson.fromJson($(tgt), MasteryList.class);
    }

    public MasteryList getMasteries(MasteryData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("mastery")
                .queryParam("masterListData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), MasteryList.class);
    }

    public Mastery getMastery(int id) {
        WebTarget tgt = staticDataTarget.path("mastery/" + id);
        return gson.fromJson($(tgt), Mastery.class);
    }

    public Mastery getMastery(int id, MasteryData data) {
        WebTarget tgt = staticDataTarget.path("mastery/" + id).queryParam("masteryData", data.name);
        return gson.fromJson($(tgt), Mastery.class);
    }

    public Mastery getMastery(int id, MasteryData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("mastery/" + id)
                .queryParam("masterListData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), Mastery.class);
    }

    public Realm getRealm() {
        WebTarget tgt = staticDataTarget.path("realm");
        return gson.fromJson($(tgt), Realm.class);
    }

    public RuneList getRuneList() {
        WebTarget tgt = staticDataTarget.path("rune");
        return gson.fromJson($(tgt), RuneList.class);
    }

    public RuneList getRuneList(ItemData data) {
        WebTarget tgt = staticDataTarget.path("rune").queryParam("runeListData", data.name);
        return gson.fromJson($(tgt), RuneList.class);
    }

    public RuneList getRuneList(ItemData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("rune")
                .queryParam("runeListData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), RuneList.class);
    }

    public Item getRune(int id) {
        WebTarget tgt = staticDataTarget.path("rune/" + id);
        return gson.fromJson($(tgt), Item.class);
    }

    public Item getRune(int id, ItemData data) {
        WebTarget tgt = staticDataTarget.path("rune/" + id).queryParam("runeData", data.name);
        return gson.fromJson($(tgt), Item.class);
    }

    public Item getRune(int id, ItemData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("rune/" + id)
                .queryParam("runeData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), Item.class);
    }

    public SummonerSpellList getSummonerSpellListDto() {
        WebTarget tgt = staticDataTarget.path("summoner-spell");
        return gson.fromJson($(tgt), SummonerSpellList.class);
    }

    public SummonerSpellList getSummonerSpellListDto(SpellData data) {
        WebTarget tgt = staticDataTarget.path("summoner-spell")
                .queryParam("spellData", data.name);
        return gson.fromJson($(tgt), SummonerSpellList.class);
    }

    public SummonerSpellList getSummonerSpellListDro(SpellData data, String version, String locale, boolean dataById) {
        WebTarget tgt = staticDataTarget.path("summoner-spell")
                .queryParam("spellData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale)
                .queryParam("dataById", dataById);
        return gson.fromJson($(tgt), SummonerSpellList.class);
    }

    public Collection<SummonerSpell> getSummonerSpells() {
        return getSummonerSpellListDto().getSpells();
    }

    public Collection<SummonerSpell> getSummonerSpells(SpellData data) {
        return getSummonerSpellListDto(data).getSpells();
    }

    public Collection<SummonerSpell> getSummonerSpells(SpellData data, String version, String locale, boolean dataById) {
        return getSummonerSpellListDro(data, version, locale, dataById).getSpells();
    }

    public SummonerSpell getSummonerSpell(int id) {
        WebTarget tgt = staticDataTarget.path("summoner-spell/" + id);
        return gson.fromJson($(tgt), SummonerSpell.class);
    }

    public SummonerSpell getSummonerSpell(int id, SpellData data) {
        WebTarget tgt = staticDataTarget.path("summoner-spell/" + id).queryParam("spellData", data.name);
        return gson.fromJson($(tgt), SummonerSpell.class);
    }

    public SummonerSpell getSummonerSpell(int id, SpellData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("summoner-spell/" + id)
                .queryParam("spellData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), SummonerSpell.class);
    }

    public RankedStats getRankedStats(long summoner) {
        WebTarget tgt = statsTarget.path(summoner + "/ranked");
        return gson.fromJson($(tgt), RankedStats.class);
    }

    public RankedStats getRankedStats(long summoner, Season season) {
        WebTarget tgt = statsTarget.path(summoner + "/ranked").queryParam("season", season);
        return gson.fromJson($(tgt), RankedStats.class);
    }

    public List<PlayerStats> getStatsSummary(long summoner) {
        WebTarget tgt = statsTarget.path(summoner + "/summary");
        return gson.fromJson($(tgt), PlayerStatsSummaryListDto.class).playerStatSummaries;
    }

    public List<PlayerStats> getStatsSummary(long summoner, Season season) {
        WebTarget tgt = statsTarget.path(summoner + "/summary").queryParam("season", season);
        return gson.fromJson($(tgt), PlayerStatsSummaryListDto.class).playerStatSummaries;
    }

    public Map<String, Summoner> getSummoners(String... names) {
        Type type = new TypeToken<Map<String, Summoner>>(){}.getType();
        WebTarget tgt = summonerInfoTarget.path("by-name").path(String.join(",", names));
        return gson.fromJson($(tgt), type);
    }

    public Summoner getSummoner(String name) {
        return getSummoners(name).get(RiotUtil.standardizeSummonerName(name));
    }

    public Map<Integer, Summoner> getSummoners(Integer... ids) {
        Type type = new TypeToken<Map<String, Summoner>>(){}.getType();
        WebTarget tgt = summonerInfoTarget.path(Arrays.asList(ids).toString().replaceAll("[\\[\\] ]", ""));

        Map<String, Summoner> result = gson.fromJson($(tgt), type);
        Map<Integer, Summoner> asIntMap = new HashMap<>();
        result.forEach((id, summoner) -> asIntMap.put(Integer.parseInt(id), summoner));

        return asIntMap;
    }

    public Summoner getSummoner(int id) {
        return getSummoners(id).get(id);
    }

    public Map<Integer, Set<MasteryPage>> getMasteryPagesMultipleUsers(Integer... ids) {
        Type type = new TypeToken<Map<String, MasteryPagesDto>>(){}.getType();
        String idString = Arrays.asList(ids).toString().replaceAll("[\\[\\] ]", "");
        WebTarget tgt = summonerInfoTarget.path(idString).path("masteries");

        Map<String, MasteryPagesDto> tmpResult = gson.fromJson($(tgt), type);
        Map<Integer, Set<MasteryPage>> result = new HashMap<>();
        tmpResult.forEach((id, masteryPagesDto) -> result.put(Integer.parseInt(id), masteryPagesDto.pages));

        return result;
    }

    public Set<MasteryPage> getMasteryPages(int id) {
        return getMasteryPagesMultipleUsers(id).get(id);
    }

    public Map<Integer, String> getSummonerNames(Integer... ids) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        String idString = Arrays.asList(ids).toString().replaceAll("[\\[\\] ]", "");
        WebTarget tgt = summonerInfoTarget.path(idString).path("name");

        Map<String, String> tmpResult = gson.fromJson($(tgt), type);
        Map<Integer, String> result = new HashMap<>();
        tmpResult.forEach((id, name) -> result.put(Integer.parseInt(id), name));

        return result;
    }

    public String getSummonerName(int id) {
        return getSummonerNames(id).get(id);
    }

    public Map<Integer, Set<RunePage>> getRunePagesMultipleUsers(Integer... ids) {
        Type type = new TypeToken<Map<String, RunePagesDto>>(){}.getType();
        String idString = Arrays.asList(ids).toString().replaceAll("[\\[\\] ]", "");
        WebTarget tgt = summonerInfoTarget.path(idString).path("runes");

        Map<String, RunePagesDto> tmpResult = gson.fromJson($(tgt), type);
        Map<Integer, Set<RunePage>> result = new HashMap<>();
        tmpResult.forEach((id, runePagesDto) -> result.put(Integer.parseInt(id), runePagesDto.pages));

        return result;
    }

    public Set<RunePage> getRunePages(int id) {
        return getRunePagesMultipleUsers(id).get(id);
    }

    public List<RankedTeam> getTeams(int id) {
        Type type = new TypeToken<List<RankedTeam>>(){}.getType();
        WebTarget tgt = teamInfoTarget.path("by-summoner/" + id);

        return gson.fromJson($(tgt), type);
    }

    public Map<String, RankedTeam> getTeams(String... teamIds) {
        Type type = new TypeToken<Map<String, RankedTeam>>(){}.getType();
        WebTarget tgt = teamInfoTarget.path(String.join(",", teamIds));

        return gson.fromJson($(tgt), type);
    }



    /**
     * Open the request to the web target and returns an InputStreamReader for the message body
     * @param target the web target to access
     * @return the reader for the message body
     */
    private InputStreamReader $(WebTarget target) {

        Response response = target.request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        if (response.getStatus() != 200) {
            throw new RequestException(RequestException.ErrorType.getByCode(response.getStatus()));
        }

        return new InputStreamReader((java.io.InputStream) response.getEntity());
    }
    /**
     * Necessary wrapper class for champion lists since they are sent by the api as a single object
     * instead of an array
     */
    private class BasicChampDataListDto {
        List<BasicChampData> champions;
    }

    /**
     * Don't expose summoner id from the recent games dto
     */
    private class RecentGamesDto {
        List<Game> games;
        long summonerId;
    }

    /**
     * Don't expose summoner id from the stats listing dto
     */
    private class PlayerStatsSummaryListDto {
        List<PlayerStats> playerStatSummaries;
        long summonerId;
    }

    private class MasteryPagesDto {
        Set<MasteryPage> pages;
        long summonerId;
    }

    private class RunePagesDto {
        Set<RunePage> pages;
        long summonerId;
    }
}
