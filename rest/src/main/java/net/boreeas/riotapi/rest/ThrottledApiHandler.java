/*
 * Copyright 2014 Malte Schütze
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

package net.boreeas.riotapi.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.com.riotgames.leagues.pojo.LeagueItem;
import net.boreeas.riotapi.com.riotgames.leagues.pojo.LeagueList;
import net.boreeas.riotapi.com.riotgames.platform.summoner.spellbook.RunePage;

import java.util.*;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created on 4/23/2014.
 */
public class ThrottledApiHandler {

    public static final int PERIOD = 50; // 0.05s

    private float[] limits;
    private Queue<ApiFuture> pending = new LinkedList<>();
    private Timer timer = new Timer(true);

    private ApiHandler handler;


    public ThrottledApiHandler(Shard shard, String token, Limit... lim) {
        this.handler = new ApiHandler(shard, token);

        limits = new float[lim.length];
        for (int i = 0; i < lim.length; i++) {

            final int ii = i;
            final float ticksToMax = (lim[ii].unit.toMillis(lim[ii].timeDeltaToMax) / (float) PERIOD);
            final float delta = lim[ii].maxValue / ticksToMax;

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    limits[ii] += delta;
                    if (limits[ii] > lim[ii].maxValue) {
                        limits[ii] = lim[ii].maxValue;
                    }
                }
            }, 0, PERIOD);
        }

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                while (limitsOpen() && !pending.isEmpty()) {
                    ApiFuture future = pending.poll();
                    executeFutureRequest(future);
                    for (int i = 0; i < limits.length; i++) {
                        limits[i]--;
                    }
                }
            }
        }, 0, PERIOD);
    }

    private void executeFutureRequest(ApiFuture future) {
        Thread thread = new Thread(() -> {
            try {
                future.setValue(future.getRequest().call());
            } catch (Exception e) {
                future.setErr(e);
            }

            future.getSignal().countDown();
        });


        thread.setDaemon(true);
        thread.start();
    }


    private boolean limitsOpen() {
        for (float f: limits) {
            if (f < 1) return false;
        }

        return true;
    }

    private synchronized void scheduleNext(ApiFuture t) {

        if (limitsOpen()) {
            executeFutureRequest(t);
            for (int i = 0; i < limits.length; i++) {
                limits[i]--;
            }
        } else {
            pending.add(t);
        }
    }

    /* ****************************
        delegation to api handler
     *************************** */



    /**
     * Get basic champion data (id, freeToPlay) for all champions
     * @return Basic champion data
     * @see <a href=https://developer.riotgames.com/api/methods#!/617/1923>Official API documentation</a>
     */
    public Future<List<BasicChampData>> getBasicChampData() {
        return new ApiFuture<>(() -> handler.getBasicChampData());
    }

    /**
     * Get basic champion data for the selected champion
     * @param id The id of the champion
     * @return Basic champion data
     * @see <a href=https://developer.riotgames.com/api/methods#!/617/1922>Official API documentation</a>
     */
    public Future<BasicChampData> getBasicChampData(int id) {
        return new ApiFuture<>(() -> handler.getBasicChampData(id));
    }

    /**
     * Get basic champion data for all free-to-play champions
     * @return Basic champion data
     * @see <a href=https://developer.riotgames.com/api/methods#!/617/1923>Official API documentation</a>
     */
    public Future<List<BasicChampData>> getFreeToPlayChampions() {
        return new ApiFuture<>(() -> handler.getFreeToPlayChampions());
    }

    /**
     * Get a listing of recent games for the summoner
     * @param summoner The id of the summoner
     * @return A list of recently played games
     * @see <a href=https://developer.riotgames.com/api/methods#!/618/1924>Official API documentation</a>
     */
    public Future<List<Game>> getRecentGames(long summoner) {
        return new ApiFuture<>(() -> handler.getRecentGames(summoner));
    }

    /**
     * Get a listing of leagues for the the summoner
     * @param summoner The id of the summoner
     * @return A list of leagues
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1862>Official API documentation</a>
     */
    public Future<List<LeagueList>> getLeagues(long summoner) {
        return new ApiFuture<>(() -> handler.getLeagues(summoner));
    }

    /**
     * Get a listing of all league entries in the summoner's leagues
     * @param summoner The id of the summoner
     * @return A list of league entries
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1863>Official API documentation</a>
     */
    public Future<List<LeagueItem>> getLeagueEntries(long summoner) {
        return new ApiFuture<>(() -> handler.getLeagueEntries(summoner));
    }

    /**
     * Get a listing of leagues for the specified team
     * @param teamId The id of the team
     * @return A list of leagues
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1860>Official API documentation</a>
     */
    public Future<List<LeagueList>> getLeaguesByTeam(String teamId) {
        return new ApiFuture<>(() -> handler.getLeaguesByTeam(teamId));
    }

    /**
     * Get a listing of all league entries in the team's leagues
     * @param teamId The id of the team
     * @return A list of league entries
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1861>Official API documentation</a>
     */
    public Future<List<LeagueItem>> getLeagueEntriesByTeam(String teamId) {
        return new ApiFuture<>(() -> handler.getLeagueEntriesByTeam(teamId));
    }

    /**
     * Get the region's challenger league
     * @param queue The queue type for which to retrieve the league information
     * @return The queue's challenger league
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1864>Official API documentation</a>
     */
    public Future<LeagueList> getChallenger(QueueType queue) {
        return new ApiFuture<>(() -> handler.getChallenger(queue));
    }

    /**
     * Get champion information for all champions
     * @return Champion information for all champions
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2171>Official API documentation</a>
     */
    public ChampionList getChampionListDto() {
        return handler.getChampionListDto();
    }

    /**
     * <p>
     * Get champion information for all champions.
     * </p>
     * This method does not count towards the rate limit
     * @param champData Additional information to retrieve
     * @return The information for all champions
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2171>Official API documentation</a>
     */
    public ChampionList getChampionListDto(ChampData champData) {
        return handler.getChampionListDto(champData);
    }

    /**
     * <p>
     * Get champion information for all champions
     * </p>
     * This method does not count towards the rate limit
     * @param locale Locale code for returned data
     * @param version Data dragon version for returned data
     * @param dataById If specified as true, the returned data map will use the champions' IDs as the keys.
     * @param champData Additional information to retrieve
     * @return The information for all champions
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2171>Official API documentation</a>
     */
    public ChampionList getChampionListDto(String locale, String version, boolean dataById, ChampData champData) {
        return handler.getChampionListDto(locale, version, dataById, champData);
    }

    /**
     * <p>
     * All champions in the game.
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return All champions in the game
     */
    public Collection<Champion> getChampions() {
        return handler.getChampions();
    }

    /**
     * <p>
     * All champions in the game.
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param champData Additional information to retrieve
     * @return All champions in the game
     */
    public Collection<Champion> getChampions(ChampData champData) {
        return handler.getChampions(champData);
    }

    /**
     * <p>
     * All champions in the game.
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param locale Locale code for returned data
     * @param version Data dragon version for returned data
     * @param dataById If specified as true, the returned data map will use the champions' IDs as the keys.
     * @param champData Additional information to retrieve
     * @return All champions in the game
     */
    public Collection<Champion> getChampions(ChampData champData, String version, String locale, boolean dataById) {
        return handler.getChampions(champData, version, locale, dataById);
    }

    /**
     * <p>
     * Get information about the specified champion
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the champion
     * @return The champion
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2169>Official API documentation</a>
     */
    public Champion getChampion(int id) {
        return handler.getChampion(id);
    }

    /**
     * <p>
     * Get information about the specified champion
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the champion
     * @param champData Additional information to retrieve
     * @return The champion
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2169>Official API documentation</a>
     */
    public Champion getChampion(int id, ChampData champData) {
        return handler.getChampion(id, champData);
    }

    /**
     * <p>
     * Get information about the specified champion
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param locale Locale code for returned data
     * @param version Data dragon version for returned data
     * @param id The id of the champion
     * @param champData Additional information to retrieve
     * @return The champion
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2169>Official API documentation</a>
     */
    public Champion getChampion(int id, ChampData champData, String version, String locale) {
        return handler.getChampion(id, champData, version, locale);
    }

    /**
     * <p>
     * Get a listing of items in the game
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return The list of items
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2166>Official API documentation</a>
     */
    public ItemList getItemList() {
        return handler.getItemList();
    }

    /**
     * <p>
     * Get a listing of items in the game
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param data Additional information to retrieve
     * @return The list of items
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2166>Official API documentation</a>
     */
    public ItemList getItemList(ItemData data) {
        return handler.getItemList(data);
    }

    /**
     * <p>
     * Get a listing of items in the game
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @param data Additional information to retrieve
     * @return The list of items
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2166>Official API documentation</a>
     */
    public ItemList getItemList(ItemData data, String version, String locale) {
        return handler.getItemList(data, version, locale);
    }

    /**
     * <p>
     * Retrieve a specific item
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the item
     * @return The item
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2176>Official API documentation</a>
     */
    public Item getItem(int id) {
        return handler.getItem(id);
    }

    /**
     * <p>
     * Retrieve a specific item
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the item
     * @param data Additional information to retrieve
     * @return The item
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2176>Official API documentation</a>
     */
    public Item getItem(int id, ItemData data) {
        return handler.getItem(id, data);
    }

    /**
     * <p>
     * Retrieve a specific item
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the item
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The item
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2176>Official API documentation</a>
     */
    public Item getItem(int id, ItemData data, String version, String locale) {
        return handler.getItem(id, data, version, locale);
    }

    /**
     * <p>
     * Get a listing of all masteries
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return The masteries
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2173>Official API documentation</a>
     */
    public MasteryList getMasteries() {
        return handler.getMasteries();
    }

    /**
     * <p>
     * Get a listing of all masteries
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param data Additional information to retrieve
     * @return The masteries
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2173>Official API documentation</a>
     */
    public MasteryList getMasteries(MasteryData data) {
        return handler.getMasteries(data);
    }

    /**
     * <p>
     * Get a listing of all masteries
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The masteries
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2173>Official API documentation</a>
     */
    public MasteryList getMasteries(MasteryData data, String version, String locale) {
        return handler.getMasteries(data, version, locale);
    }

    /**
     * <p>
     * Get a specific mastery
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the mastery
     * @return The mastery
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2175>Official API documentation</a>
     */
    public Mastery getMastery(int id) {
        return handler.getMastery(id);
    }

    /**
     * <p>
     * Get a specific mastery
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the mastery
     * @param data Additional information to retrieve
     * @return The mastery
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2175>Official API documentation</a>
     */
    public Mastery getMastery(int id, MasteryData data) {
        return handler.getMastery(id, data);
    }

    /**
     * <p>
     * Get a specific mastery
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the mastery
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The mastery
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2175>Official API documentation</a>
     */
    public Mastery getMastery(int id, MasteryData data, String version, String locale) {
        return handler.getMastery(id, data, version, locale);
    }

    /**
     * <p>
     * Get realm information for this region
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return Realm information
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2170>Official API documentation</a>
     */
    public Realm getRealm() {
        return handler.getRealm();
    }

    /**
     * <p>
     * Get a list of all runes
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return All runes
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2172>Official API documentation</a>
     */
    public RuneList getRuneList() {
        return handler.getRuneList();
    }

    /**
     * <p>
     * Get a list of all runes
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param data Additional information to retrieve
     * @return All runes
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2172>Official API documentation</a>
     */
    public RuneList getRuneList(ItemData data) {
        return handler.getRuneList(data);
    }

    /**
     * <p>
     * Get a list of all runes
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return All runes
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2172>Official API documentation</a>
     */
    public RuneList getRuneList(ItemData data, String version, String locale) {
        return handler.getRuneList(data, version, locale);
    }

    /**
     * <p>
     * Retrieve a specific runes
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the runes
     * @return The runes
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2168>Official API documentation</a>
     */
    public Item getRune(int id) {
        return handler.getRune(id);
    }

    /**
     * <p>
     * Retrieve a specific runes
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the runes
     * @param data Additional information to retrieve
     * @return The runes
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2168>Official API documentation</a>
     */
    public Item getRune(int id, ItemData data) {
        return handler.getRune(id, data);
    }

    /**
     * <p>
     * Retrieve a specific runes
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the runes
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The runes
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2168>Official API documentation</a>
     */
    public Item getRune(int id, ItemData data, String version, String locale) {
        return handler.getRune(id, data, version, locale);
    }

    /**
     * <p>
     * Get a list of all summoner spells as returned by the API
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public SummonerSpellList getSummonerSpellListDto() {
        return handler.getSummonerSpellListDto();
    }

    /**
     * <p>
     * Get a list of all summoner spells as returned by the API
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param data Additional information to retrieve
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public SummonerSpellList getSummonerSpellListDto(SpellData data) {
        return handler.getSummonerSpellListDto(data);
    }

    /**
     * <p>
     * Get a list of all summoner spells as returned by the API
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @param dataById If specified as true, the returned data map will use the spells' IDs as the keys.
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public SummonerSpellList getSummonerSpellListDro(SpellData data, String version, String locale, boolean dataById) {
        return handler.getSummonerSpellListDro(data, version, locale, dataById);
    }

    /**
     * <p>
     * Get a list of all summoner spells as Java Collection
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public Collection<SummonerSpell> getSummonerSpells() {
        return handler.getSummonerSpells();
    }

    /**
     * <p>
     * Get a list of all summoner spells as Java Collection
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param data Additional information to retrieve
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public Collection<SummonerSpell> getSummonerSpells(SpellData data) {
        return handler.getSummonerSpells(data);
    }

    /**
     * <p>
     * Get a list of all summoner spells as Java Collection
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @param dataById If specified as true, the returned data map will use the spells' IDs as the keys.
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public Collection<SummonerSpell> getSummonerSpells(SpellData data, String version, String locale, boolean dataById) {
        return handler.getSummonerSpells(data, version, locale, dataById);
    }

    /**
     * <p>
     * Retrieve a specific summoner spell
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the spell
     * @return The spell
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2167>Official API documentation</a>
     */
    public SummonerSpell getSummonerSpell(int id) {
        return handler.getSummonerSpell(id);
    }

    /**
     * <p>
     * Retrieve a specific summoner spell
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the spell
     * @param data Additional information to retrieve
     * @return The spell
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2167>Official API documentation</a>
     */
    public SummonerSpell getSummonerSpell(int id, SpellData data) {
        return handler.getSummonerSpell(id, data);
    }

    /**
     * <p>
     * Retrieve a specific summoner spell
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param id The id of the spell
     * @param data Additional information to retrieve
     * @param version Data dragon version for returned data
     * @param locale Locale code for returned data
     * @return The spell
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2167>Official API documentation</a>
     */
    public SummonerSpell getSummonerSpell(int id, SpellData data, String version, String locale) {
        return handler.getSummonerSpell(id, data, version, locale);
    }

    /**
     * Get ranked stats for a player
     * @param summoner The id of the summoner
     * @return Ranked stats
     * @see <a href=https://developer.riotgames.com/api/methods#!/622/1937>Official API documentation</a>
     */
    public Future<RankedStats> getRankedStats(long summoner) {
        return new ApiFuture<>(() -> handler.getRankedStats(summoner));
    }

    /**
     * Get ranked stats for a player in a specific season
     * @param summoner The id of the summoner
     * @param season The season
     * @return Ranked stats
     * @see <a href=https://developer.riotgames.com/api/methods#!/622/1937>Official API documentation</a>
     */
    public Future<RankedStats> getRankedStats(long summoner, Season season) {
        return new ApiFuture<>(() -> handler.getRankedStats(summoner, season));
    }

    /**
     * Get player stats for the player
     * @param summoner The id of the summoner
     * @return The player's stats
     * @see <a href=https://developer.riotgames.com/api/methods#!/622/1938>Official API documentation</a>
     */
    public Future<List<PlayerStats>> getStatsSummary(long summoner) {
        return new ApiFuture<>(() -> handler.getStatsSummary(summoner));
    }

    /**
     * Get player stats for the player
     * @param summoner The id of the summoner
     * @param season The season
     * @return The player's stats
     * @see <a href=https://developer.riotgames.com/api/methods#!/622/1938>Official API documentation</a>
     */
    public Future<List<PlayerStats>> getStatsSummary(long summoner, Season season) {
        return new ApiFuture<>(() -> handler.getStatsSummary(summoner, season));
    }

    /**
     * Get summoner information for the summoners with the specified names
     * @param names The names of the players
     * @return A map, mapping standardized player names to summoner information
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1930>Official API documentation</a>
     * @see net.boreeas.riotapi.Util#standardizeSummonerName(java.lang.String)
     */
    public Future<Map<String, Summoner>> getSummoners(String... names) {
        return new ApiFuture<>(() -> handler.getSummoners(names));
    }

    /**
     * Get summoner information for the specified summoner
     * @param name The name of the summoner
     * @return Summoner information
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1930>Official API documentation</a>
     */
    public Future<Summoner> getSummoner(String name) {
        return new ApiFuture<>(() -> handler.getSummoner(name));
    }

    /**
     * Get summoner information for the summoners with the specified ids
     * @param ids The ids of the summoners
     * @return A map, mapping player ids to summoner information
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1931>Official API documentation</a>
     */
    public Future<Map<Integer, Summoner>> getSummoners(Integer... ids) {
        return new ApiFuture<>(() -> handler.getSummoners(ids));
    }

    /**
     * Get summoner information for the summoner with the specified id
     * @param id The id of the summoner
     * @return Summoner information
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1931>Official API documentation</a>
     */
    public Future<Summoner> getSummoner(int id) {
        return new ApiFuture<>(() -> handler.getSummoner(id));
    }

    /**
     * Retrieve mastery pages for multiple users
     * @param ids The ids of the users
     * @return A map, mapping player ids to their respective mastery pages
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1933>Official API documentation</a>
     */
    public Future<Map<Integer, Set<MasteryPage>>> getMasteryPagesMultipleUsers(Integer... ids) {
        return new ApiFuture<>(() -> handler.getMasteryPagesMultipleUsers(ids));
    }

    /**
     * Retrieve the mastery pages of a user
     * @param id The user's id
     * @return The user's mastery pages
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1933>Official API documentation</a>
     */
    public Future<Set<MasteryPage>> getMasteryPages(int id) {
        return new ApiFuture<>(() -> handler.getMasteryPages(id));
    }

    /**
     * Retrieve summoner names for the specified ids
     * @param ids The ids to lookup
     * @return A map, mapping user ids to summoner names
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1934>Official API documentation</a>
     */
    public Future<Map<Integer, String>> getSummonerNames(Integer... ids) {
        return new ApiFuture<>(() -> handler.getSummonerNames(ids));
    }

    /**
     * Retrieve the summoner name of the specified user
     * @param id The id of the user
     * @return The summoner name of the user
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1934>Official API documentation</a>
     */
    public Future<String> getSummonerName(int id) {
        return new ApiFuture<>(() -> handler.getSummonerName(id));
    }

    /**
     * Retrieve runes pages for multiple users
     * @param ids The ids of the users
     * @return A map, mapping user ids to their respective runes pages
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1932>Official API documentation</a>
     */
    public Future<Map<Integer, Set<RunePage>>> getRunePagesMultipleUsers(Integer... ids) {
        return new ApiFuture<>(() -> handler.getRunePagesMultipleUsers(ids));
    }

    /**
     * Retrieve the runes pages of a user
     * @param id The user's id
     * @return The user's runes page
     * @see <a href=https://developer.riotgames.com/api/methods#!/620/1932>Official API documentation</a>
     */
    public Future<Set<RunePage>> getRunePages(int id) {
        return new ApiFuture<>(() -> handler.getRunePages(id));
    }

    /**
     * Retrieve the ranked teams of a user
     * @param id The user's id
     * @return The ranked teams of the user
     * @see <a href=https://developer.riotgames.com/api/methods#!/594/1865>Official API documentation</a>
     */
    public Future<List<RankedTeam>> getTeams(int id) {
        return new ApiFuture<>(() -> handler.getTeams(id));
    }

    /**
     * Retrieve information for the specified ranked teams
     * @param teamIds The ids of the teams
     * @return A map, mapping team ids to team information
     * @see <a href=https://developer.riotgames.com/api/methods#!/594/1866>Official API documentation</a>
     */
    public Future<Map<String, RankedTeam>> getTeams(String... teamIds) {
        return new ApiFuture<>(() -> handler.getTeams(teamIds));
    }

    /**
     * Retrieve summoner ids for the specified names
     * @param names The names of the users
     * @return Their respective ids
     */
    public Future<List<Long>> getSummonerIds(String... names) {
        return new ApiFuture<>(() -> handler.getSummonerIds(names));
    }

    /**
     * <p>
     * Retrieve the summoner id for the specified user
     * @param name The name of the user
     * @return Their respective ids
     */
    public Future<Long> getSummonerId(String name) {
        return new ApiFuture<>(() -> handler.getSummonerId(name));
    }

    /**
     * <p>
     * Retrieve currently supported game versions.
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return A list of supported game versions
     * @see <a href=https://developer.riotgames.com/api/methods#!/710/2527>Official API documentation</a>
     */
    public List<String> getVersions() {
        return handler.getVersions();
    }

    /**
     * Stops the threads handling pending requests. No further requests will
     * be executed, but any running thread will complete first
     */
    public void stop() {
        timer.cancel();
    }


    @AllArgsConstructor
    public static class Limit {
        public final int timeDeltaToMax;
        public final TimeUnit unit;
        public final int maxValue;
    }

    private class ApiFuture<T> implements Future<T> {

        private boolean cancelled;
        @Getter private Callable<T> request;
        @Setter private T value;
        @Setter private Exception err;
        @Getter private CountDownLatch signal = new CountDownLatch(1);

        public ApiFuture(Callable<T> request) {
            this.request = request;
            ThrottledApiHandler.this.scheduleNext(this);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            cancelled = true;
            return true;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public boolean isDone() {
            return value != null || err != null;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            if (isCancelled()) {
                throw new ExecutionException("Task was cancelled", null);
            }

            signal.await();

            if (err != null) {
                throw new ExecutionException(err);
            }

            return value;
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (isCancelled()) {
                throw new ExecutionException("Task was cancelled", null);
            }

            signal.await(timeout, unit);

            if (err != null) {
                throw new ExecutionException(err);
            }

            return value;
        }

        public Exception getErr() {
            return err;
        }
    }


    /**
     * Returns a throttled api handler with the current development request limits,
     * which is 10 requests per 10 seconds and 500 requests per 10 minutes
     * @param shard The target server
     * @param token The api key
     * @return The api handler
     */
    public static ThrottledApiHandler developmentDefault(Shard shard, String token) {
        return new ThrottledApiHandler(shard, token,
                new Limit(10, TimeUnit.SECONDS, 10),
                new Limit(10, TimeUnit.MINUTES, 500));
    }
}
