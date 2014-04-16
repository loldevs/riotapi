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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles sending GET request to the riot api server
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

    /**
     * Create a new ApiHandler object
     * @param shard The target region
     * @param token The api key
     */
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

    /**
     * Get basic champion data (id, freeToPlay) for all champions
     * @return Basic champion data
     * @see <a href=https://developer.riotgames.com/api/methods#!/617/1923>Official API documentation</a>
     */
    public List<BasicChampData> getBasicChampData() {
        return gson.fromJson($(championInfoTarget), BasicChampDataListDto.class).champions;
    }

    /**
     * Get basic champion data for all free-to-play champions
     * @return Basic champion data
     * @see <a href=https://developer.riotgames.com/api/methods#!/617/1923>Official API documentation</a>
     */
    public List<BasicChampData> getFreeToPlayChampions() {
        WebTarget tgt = championInfoTarget.queryParam("freeToPlay", true);
        return gson.fromJson($(tgt), BasicChampDataListDto.class).champions;
    }

    /**
     * Get basic champion data for the selected champion
     * @param id The id of the champion
     * @return Basic champion data
     * @see <a href=https://developer.riotgames.com/api/methods#!/617/1922>Official API documentation</a>
     */
    public BasicChampData getBasicChampData(int id) {
        return gson.fromJson($(championInfoTarget.path(""+id)), BasicChampData.class);
    }

    /**
     * Get a listing of recent games for the summoner
     * @param summoner The id of the summoner
     * @return A list of recently played games
     * @see <a href=https://developer.riotgames.com/api/methods#!/618/1924>Official API documentation</a>
     */
    public List<Game> getRecentGames(long summoner) {
        WebTarget tgt = gameInfoTarget.path(summoner + "/recent");
        return gson.fromJson($(tgt), RecentGamesDto.class).games;
    }

    /**
     * Get a listing of leagues for the the summoner
     * @param summoner The id of the summoner
     * @return A list of leagues
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1862>Official API documentation</a>
     */
    public List<League> getLeagues(long summoner) {
        Type type = new TypeToken<List<League>>(){}.getType();
        WebTarget tgt = leagueInfoTarget.path("by-summoner/" + summoner);
        return gson.fromJson($(tgt), type);
    }

    /**
     * Get a listing of all league entries in the summoner's leagues
     * @param summoner The id of the summoner
     * @return A list of league entries
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1863>Official API documentation</a>
     */
    public List<LeagueItem> getLeagueEntries(long summoner) {
        Type type = new TypeToken<List<LeagueItem>>(){}.getType();
        WebTarget tgt = leagueInfoTarget.path("by-summoner/" + summoner).path("entry");
        return gson.fromJson($(tgt), type);
    }

    /**
     * Get a listing of leagues for the specified team
     * @param teamId The id of the team
     * @return A list of leagues
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1860>Official API documentation</a>
     */
    public List<League> getLeaguesByTeam(String teamId) {
        Type type = new TypeToken<List<League>>(){}.getType();
        WebTarget tgt = leagueInfoTarget.path("by-team").path(teamId);
        return gson.fromJson($(tgt), type);
    }

    /**
     * Get a listing of all league entries in the team's leagues
     * @param teamId The id of the team
     * @return A list of league entries
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1861>Official API documentation</a>
     */
    public List<LeagueItem> getLeagueEntriesByTeam(String teamId) {
        Type type = new TypeToken<List<LeagueItem>>(){}.getType();
        WebTarget tgt = leagueInfoTarget.path("by-team").path(teamId).path("entry");
        return gson.fromJson($(tgt), type);
    }

    /**
     * Get the region's challenger league
     * @param queue The queue type for which to retrieve the league information
     * @return The queue's challenger league
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1864>Official API documentation</a>
     */
    public League getChallenger(Queue queue) {
        WebTarget tgt = leagueInfoTarget.path("challenger").queryParam("type", queue.name);
        return gson.fromJson($(tgt), League.class);
    }

    /**
     * Get champion information for all champions
     * @return Champion information for all champions
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2171>Official API documentation</a>
     */
    public ChampionList getChampionListDto() {
        WebTarget tgt = staticDataTarget.path("champion");
        return gson.fromJson($(tgt), ChampionList.class);
    }

    /**
     * Get champion information for all champions.
     * <p/>
     * This method does not count towards the rate limit
     * @param champData Additional information to retrieve
     * @return The information for all champions
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2171>Official API documentation</a>
     */
    public ChampionList getChampionListDto(ChampData champData) {
        WebTarget tgt = staticDataTarget.path("champion").queryParam("champData", champData.name);
        return gson.fromJson($(tgt), ChampionList.class);
    }

    /**
     * Get champion information for all champions
     * <p/>
     * This method does not count towards the rate limit
     * @param locale Locale code for returned data
     * @param version Data dragon version for returned data
     * @param dataById If specified as true, the returned data map will use the champions' IDs as the keys.
     * @param champData Additional information to retrieve
     * @return The information for all champions
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2171>Official API documentation</a>
     */
    public ChampionList getChampionListDto(String locale, String version, boolean dataById, ChampData champData) {
        WebTarget tgt = staticDataTarget.path("champion")
                .queryParam("locale", locale)
                .queryParam("version", version)
                .queryParam("dataById", dataById)
                .queryParam("champData", champData.name);
        return gson.fromJson($(tgt), ChampionList.class);
    }

    /**
     * All champions in the game.
     * <p/>
     * This method does not count towards the rate limit
     * @return All champions in the game
     */
    public Collection<Champion> getChampions() {
        return getChampionListDto().getChampions();
    }

    /**
     * All champions in the game.
     * <p/>
     * This method does not count towards the rate limit
     * @param champData Additional information to retrieve
     * @return All champions in the game
     */
    public Collection<Champion> getChampions(ChampData champData) {
        return getChampionListDto(champData).getChampions();
    }

    /**
     * All champions in the game.
     * <p/>
     * This method does not count towards the rate limit
     * @param locale Locale code for returned data
     * @param version Data dragon version for returned data
     * @param dataById If specified as true, the returned data map will use the champions' IDs as the keys.
     * @param champData Additional information to retrieve
     * @return All champions in the game
     */
    public Collection<Champion> getChampions(ChampData champData, String version, String locale, boolean dataById) {
        return getChampionListDto(locale, version, dataById, champData).getChampions();
    }

    /**
     * Get information about the specified champion
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the champion
     * @return The champion
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2169>Official API documentation</a>
     */
    public Champion getChampion(int id) {
        WebTarget tgt = staticDataTarget.path("champion/" + id);
        return gson.fromJson($(tgt), Champion.class);
    }

    /**
     * Get information about the specified champion
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the champion
     * @param champData Additional information to retrieve
     * @return The champion
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2169>Official API documentation</a>
     */
    public Champion getChampion(int id, ChampData champData) {
        WebTarget tgt = staticDataTarget.path("champion/" + id).queryParam("champData", champData.name);
        return gson.fromJson($(tgt), Champion.class);
    }

    /**
     * Get information about the specified champion
     * <p/>
     * This method does not count towards the rate limit
     * @param locale Locale code for returned data
     * @param version Data dragon version for returned data
     * @param id The id of the champion
     * @param champData Additional information to retrieve
     * @return The champion
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2169>Official API documentation</a>
     */
    public Champion getChampion(int id, ChampData champData, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("champion/" + id)
                .queryParam("champData", champData.name)
                .queryParam("locale", locale)
                .queryParam("version", version);
        return gson.fromJson($(tgt), Champion.class);
    }

    /**
     * Get a listing of items in the game
     * <p/>
     * This method does not count towards the rate limit
     * @return The list of items
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2166>Official API documentation</a>
     */
    public ItemList getItemList() {
        WebTarget tgt = staticDataTarget.path("item");
        return gson.fromJson($(tgt), ItemList.class);
    }

    /**
     * Get a listing of items in the game
     * <p/>
     * This method does not count towards the rate limit
     * @param data Additional information to retrieve
     * @return The list of items
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2166>Official API documentation</a>
     */
    public ItemList getItemList(ItemData data) {
        WebTarget tgt = staticDataTarget.path("item").queryParam("itemListData", data.name);
        return gson.fromJson($(tgt), ItemList.class);
    }

    /**
     * Get a listing of items in the game
     * <p/>
     * This method does not count towards the rate limit
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @param data Additional information to retrieve
     * @return The list of items
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2166>Official API documentation</a>
     */
    public ItemList getItemList(ItemData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("item")
                .queryParam("itemListData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), ItemList.class);
    }

    /**
     * Retrieve a specific item
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the item
     * @return The item
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2176>Official API documentation</a>
     */
    public Item getItem(int id) {
        WebTarget tgt = staticDataTarget.path("item/" + id);
        return gson.fromJson($(tgt), Item.class);
    }

    /**
     * Retrieve a specific item
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the item
     * @param data Additional information to retrieve
     * @return The item
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2176>Official API documentation</a>
     */
    public Item getItem(int id, ItemData data) {
        WebTarget tgt = staticDataTarget.path("item/" + id).queryParam("itemData", data.name);
        return gson.fromJson($(tgt), Item.class);
    }

    /**
     * Retrieve a specific item
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the item
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The item
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2176>Official API documentation</a>
     */
    public Item getItem(int id, ItemData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("item/" + id)
                .queryParam("itemData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), Item.class);
    }

    /**
     * Get a listing of all masteries
     * <p/>
     * This method does not count towards the rate limit
     * @return The masteries
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2173>Official API documentation</a>
     */
    public MasteryList getMasteries() {
        WebTarget tgt = staticDataTarget.path("mastery");
        return gson.fromJson($(tgt), MasteryList.class);
    }

    /**
     * Get a listing of all masteries
     * <p/>
     * This method does not count towards the rate limit
     * @param data Additional information to retrieve
     * @return The masteries
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2173>Official API documentation</a>
     */
    public MasteryList getMasteries(MasteryData data) {
        WebTarget tgt = staticDataTarget.path("mastery").queryParam("masteryListData", data.name);
        return gson.fromJson($(tgt), MasteryList.class);
    }

    /**
     * Get a listing of all masteries
     * <p/>
     * This method does not count towards the rate limit
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The masteries
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2173>Official API documentation</a>
     */
    public MasteryList getMasteries(MasteryData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("mastery")
                .queryParam("masterListData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), MasteryList.class);
    }

    /**
     * Get a specific mastery
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the mastery
     * @return The mastery
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2175>Official API documentation</a>
     */
    public Mastery getMastery(int id) {
        WebTarget tgt = staticDataTarget.path("mastery/" + id);
        return gson.fromJson($(tgt), Mastery.class);
    }

    /**
     * Get a specific mastery
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the mastery
     * @param data Additional information to retrieve
     * @return The mastery
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2175>Official API documentation</a>
     */
    public Mastery getMastery(int id, MasteryData data) {
        WebTarget tgt = staticDataTarget.path("mastery/" + id).queryParam("masteryData", data.name);
        return gson.fromJson($(tgt), Mastery.class);
    }

    /**
     * Get a specific mastery
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the mastery
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The mastery
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2175>Official API documentation</a>
     */
    public Mastery getMastery(int id, MasteryData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("mastery/" + id)
                .queryParam("masterListData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), Mastery.class);
    }

    /**
     * Get realm information for this region
     * <p/>
     * This method does not count towards the rate limit
     * @return Realm information
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2170>Official API documentation</a>
     */
    public Realm getRealm() {
        WebTarget tgt = staticDataTarget.path("realm");
        return gson.fromJson($(tgt), Realm.class);
    }

    /**
     * Get a list of all runes
     * <p/>
     * This method does not count towards the rate limit
     * @return All runes
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2172>Official API documentation</a>
     */
    public RuneList getRuneList() {
        WebTarget tgt = staticDataTarget.path("rune");
        return gson.fromJson($(tgt), RuneList.class);
    }

    /**
     * Get a list of all runes
     * <p/>
     * This method does not count towards the rate limit
     * @param data Additional information to retrieve
     * @return All runes
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2172>Official API documentation</a>
     */
    public RuneList getRuneList(ItemData data) {
        WebTarget tgt = staticDataTarget.path("rune").queryParam("runeListData", data.name);
        return gson.fromJson($(tgt), RuneList.class);
    }

    /**
     * Get a list of all runes
     * <p/>
     * This method does not count towards the rate limit
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return All runes
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2172>Official API documentation</a>
     */
    public RuneList getRuneList(ItemData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("rune")
                .queryParam("runeListData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), RuneList.class);
    }

    /**
     * Retrieve a specific rune
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the rune
     * @return The rune
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2168>Official API documentation</a>
     */
    public Item getRune(int id) {
        WebTarget tgt = staticDataTarget.path("rune/" + id);
        return gson.fromJson($(tgt), Item.class);
    }

    /**
     * Retrieve a specific rune
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the rune
     * @param data Additional information to retrieve
     * @return The rune
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2168>Official API documentation</a>
     */
    public Item getRune(int id, ItemData data) {
        WebTarget tgt = staticDataTarget.path("rune/" + id).queryParam("runeData", data.name);
        return gson.fromJson($(tgt), Item.class);
    }

    /**
     * Retrieve a specific rune
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the rune
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The rune
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2168>Official API documentation</a>
     */
    public Item getRune(int id, ItemData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("rune/" + id)
                .queryParam("runeData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), Item.class);
    }

    /**
     * Get a list of all summoner spells as returned by the API
     * <p/>
     * This method does not count towards the rate limit
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public SummonerSpellList getSummonerSpellListDto() {
        WebTarget tgt = staticDataTarget.path("summoner-spell");
        return gson.fromJson($(tgt), SummonerSpellList.class);
    }

    /**
     * Get a list of all summoner spells as returned by the API
     * <p/>
     * This method does not count towards the rate limit
     * @param data Additional information to retrieve
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public SummonerSpellList getSummonerSpellListDto(SpellData data) {
        WebTarget tgt = staticDataTarget.path("summoner-spell")
                .queryParam("spellData", data.name);
        return gson.fromJson($(tgt), SummonerSpellList.class);
    }

    /**
     * Get a list of all summoner spells as returned by the API
     * <p/>
     * This method does not count towards the rate limit
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public SummonerSpellList getSummonerSpellListDro(SpellData data, String version, String locale, boolean dataById) {
        WebTarget tgt = staticDataTarget.path("summoner-spell")
                .queryParam("spellData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale)
                .queryParam("dataById", dataById);
        return gson.fromJson($(tgt), SummonerSpellList.class);
    }

    /**
     * Get a list of all summoner spells as Java Collection
     * <p/>
     * This method does not count towards the rate limit
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public Collection<SummonerSpell> getSummonerSpells() {
        return getSummonerSpellListDto().getSpells();
    }

    /**
     * Get a list of all summoner spells as Java Collection
     * <p/>
     * This method does not count towards the rate limit
     * @param data Additional information to retrieve
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public Collection<SummonerSpell> getSummonerSpells(SpellData data) {
        return getSummonerSpellListDto(data).getSpells();
    }

    /**
     * Get a list of all summoner spells as Java Collection
     * <p/>
     * This method does not count towards the rate limit
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public Collection<SummonerSpell> getSummonerSpells(SpellData data, String version, String locale, boolean dataById) {
        return getSummonerSpellListDro(data, version, locale, dataById).getSpells();
    }

    /**
     * Retrieve a specific summoner spell
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the spell
     * @return The spell
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2167>Official API documentation</a>
     */
    public SummonerSpell getSummonerSpell(int id) {
        WebTarget tgt = staticDataTarget.path("summoner-spell/" + id);
        return gson.fromJson($(tgt), SummonerSpell.class);
    }

    /**
     * Retrieve a specific summoner spell
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the spell
     * @param data Additional information to retrieve
     * @return The spell
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2167>Official API documentation</a>
     */
    public SummonerSpell getSummonerSpell(int id, SpellData data) {
        WebTarget tgt = staticDataTarget.path("summoner-spell/" + id).queryParam("spellData", data.name);
        return gson.fromJson($(tgt), SummonerSpell.class);
    }

    /**
     * Retrieve a specific summoner spell
     * <p/>
     * This method does not count towards the rate limit
     * @param id The id of the spell
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The spell
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2167>Official API documentation</a>
     */
    public SummonerSpell getSummonerSpell(int id, SpellData data, String version, String locale) {
        WebTarget tgt = staticDataTarget.path("summoner-spell/" + id)
                .queryParam("spellData", data.name)
                .queryParam("version", version)
                .queryParam("locale", locale);
        return gson.fromJson($(tgt), SummonerSpell.class);
    }

    /**
     * Get ranked stats for a player
     * @param summoner The id of the summoner
     * @return Ranked stats
     * @see <a href=https://developer.riotgames.com/api/methods#!/622/1937>Official API documentation</a>
     */
    public RankedStats getRankedStats(long summoner) {
        WebTarget tgt = statsTarget.path(summoner + "/ranked");
        return gson.fromJson($(tgt), RankedStats.class);
    }

    /**
     * Get ranked stats for a player in a specific season
     * @param summoner The id of the summoner
     * @param season The season
     * @return Ranked stats
     * @see <a href=https://developer.riotgames.com/api/methods#!/622/1937>Official API documentation</a>
     */
    public RankedStats getRankedStats(long summoner, Season season) {
        WebTarget tgt = statsTarget.path(summoner + "/ranked").queryParam("season", season);
        return gson.fromJson($(tgt), RankedStats.class);
    }

    /**
     * Get player stats for the player
     * @param summoner The id of the summoner
     * @return The player's stats
     * @see <a href=https://developer.riotgames.com/api/methods#!/622/1938>Official API documentation</a>
     */
    public List<PlayerStats> getStatsSummary(long summoner) {
        WebTarget tgt = statsTarget.path(summoner + "/summary");
        return gson.fromJson($(tgt), PlayerStatsSummaryListDto.class).playerStatSummaries;
    }

    /**
     * Get player stats for the player
     * @param summoner The id of the summoner
     * @param season The season
     * @return The player's stats
     * @see <a href=https://developer.riotgames.com/api/methods#!/622/1938>Official API documentation</a>
     */
    public List<PlayerStats> getStatsSummary(long summoner, Season season) {
        WebTarget tgt = statsTarget.path(summoner + "/summary").queryParam("season", season);
        return gson.fromJson($(tgt), PlayerStatsSummaryListDto.class).playerStatSummaries;
    }

    /**
     * Get summoner information for the summoners with the specified names
     * @param names The names of the players
     * @return A map, mapping standardized player names to summoner information
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1930>Official API documentation</a>
     * @see net.boreeas.riotapi.RiotUtil#standardizeSummonerName(java.lang.String)
     */
    public Map<String, Summoner> getSummoners(String... names) {
        Type type = new TypeToken<Map<String, Summoner>>(){}.getType();
        WebTarget tgt = summonerInfoTarget.path("by-name").path(String.join(",", names));
        return gson.fromJson($(tgt), type);
    }

    /**
     * Get summoner information for the specified summoner
     * @param name The name of the summoner
     * @return Summoner information
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1930>Official API documentation</a>
     */
    public Summoner getSummoner(String name) {
        return getSummoners(name).get(RiotUtil.standardizeSummonerName(name));
    }

    /**
     * Get summoner information for the summoners with the specified ids
     * @param ids The ids of the summoners
     * @return A map, mapping player ids to summoner information
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1931>Official API documentation</a>
     */
    public Map<Integer, Summoner> getSummoners(Integer... ids) {
        Type type = new TypeToken<Map<String, Summoner>>(){}.getType();
        WebTarget tgt = summonerInfoTarget.path(Arrays.asList(ids).toString().replaceAll("[\\[\\] ]", ""));

        Map<String, Summoner> result = gson.fromJson($(tgt), type);
        Map<Integer, Summoner> asIntMap = new HashMap<>();
        result.forEach((id, summoner) -> asIntMap.put(Integer.parseInt(id), summoner));

        return asIntMap;
    }

    /**
     * Get summoner information for the summoner with the specified id
     * @param id The id of the summoner
     * @return Summoner information
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1931>Official API documentation</a>
     */
    public Summoner getSummoner(int id) {
        return getSummoners(id).get(id);
    }

    /**
     * Retrieve mastery pages for multiple users
     * @param ids The ids of the users
     * @return A map, mapping player ids to their respective mastery pages
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1933>Official API documentation</a>
     */
    public Map<Integer, Set<MasteryPage>> getMasteryPagesMultipleUsers(Integer... ids) {
        Type type = new TypeToken<Map<String, MasteryPagesDto>>(){}.getType();
        String idString = Arrays.asList(ids).toString().replaceAll("[\\[\\] ]", "");
        WebTarget tgt = summonerInfoTarget.path(idString).path("masteries");

        Map<String, MasteryPagesDto> tmpResult = gson.fromJson($(tgt), type);
        Map<Integer, Set<MasteryPage>> result = new HashMap<>();
        tmpResult.forEach((id, masteryPagesDto) -> result.put(Integer.parseInt(id), masteryPagesDto.pages));

        return result;
    }

    /**
     * Retrieve the mastery pages of a user
     * @param id The user's id
     * @return The user's mastery pages
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1933>Official API documentation</a>
     */
    public Set<MasteryPage> getMasteryPages(int id) {
        return getMasteryPagesMultipleUsers(id).get(id);
    }

    /**
     * Retrieve summoner names for the specified ids
     * @param ids The ids to lookup
     * @return A map, mapping user ids to summoner names
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1934>Official API documentation</a>
     */
    public Map<Integer, String> getSummonerNames(Integer... ids) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        String idString = Arrays.asList(ids).toString().replaceAll("[\\[\\] ]", "");
        WebTarget tgt = summonerInfoTarget.path(idString).path("name");

        Map<String, String> tmpResult = gson.fromJson($(tgt), type);
        Map<Integer, String> result = new HashMap<>();
        tmpResult.forEach((id, name) -> result.put(Integer.parseInt(id), name));

        return result;
    }

    /**
     * Retrieve the summoner name of the specified user
     * @param id The id of the user
     * @return The summoner name of the user
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1934>Official API documentation</a>
     */
    public String getSummonerName(int id) {
        return getSummonerNames(id).get(id);
    }

    /**
     * Retrieve rune pages for multiple users
     * @param ids The ids of the users
     * @return A map, mapping user ids to their respective rune pages
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1932>Official API documentation</a>
     */
    public Map<Integer, Set<RunePage>> getRunePagesMultipleUsers(Integer... ids) {
        Type type = new TypeToken<Map<String, RunePagesDto>>(){}.getType();
        String idString = Arrays.asList(ids).toString().replaceAll("[\\[\\] ]", "");
        WebTarget tgt = summonerInfoTarget.path(idString).path("runes");

        Map<String, RunePagesDto> tmpResult = gson.fromJson($(tgt), type);
        Map<Integer, Set<RunePage>> result = new HashMap<>();
        tmpResult.forEach((id, runePagesDto) -> result.put(Integer.parseInt(id), runePagesDto.pages));

        return result;
    }

    /**
     * Retrieve the rune pages of a user
     * @param id The user's id
     * @return The user's rune page
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1932>Official API documentation</a>
     */
    public Set<RunePage> getRunePages(int id) {
        return getRunePagesMultipleUsers(id).get(id);
    }

    /**
     * Retrieve the ranked teams of a user
     * @param id The user's id
     * @return The ranked teams of the user
     * @see <a href=https://developer.riotgames.com/api/methods#!/594/1865>Official API documentation</a>
     */
    public List<RankedTeam> getTeams(int id) {
        Type type = new TypeToken<List<RankedTeam>>(){}.getType();
        WebTarget tgt = teamInfoTarget.path("by-summoner/" + id);

        return gson.fromJson($(tgt), type);
    }

    /**
     * Retrieve information for the specified ranked teams
     * @param teamIds The ids of the teams
     * @return A map, mapping team ids to team information
     * @see <a href=https://developer.riotgames.com/api/methods#!/594/1866>Official API documentation</a>
     */
    public Map<String, RankedTeam> getTeams(String... teamIds) {
        Type type = new TypeToken<Map<String, RankedTeam>>(){}.getType();
        WebTarget tgt = teamInfoTarget.path(String.join(",", teamIds));

        return gson.fromJson($(tgt), type);
    }

    /**
     * Retrieve summoner ids for the specified names
     * @param names The names of the users
     * @return Their respective ids
     */
    public List<Long> getSummonerIds(String... names) {
        return getSummoners(names).values().stream().<Long>map((s) -> s.getId()).collect(Collectors.toList());
    }

    /**
     * Retrieve the summoner id for the specified user
     * @param name The name of the user
     * @return Their respective ids
     */
    public long getSummonerId(String name) {
        return getSummoner(name).getId();
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
