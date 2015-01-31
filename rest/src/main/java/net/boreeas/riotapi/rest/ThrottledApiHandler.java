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

package net.boreeas.riotapi.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.boreeas.riotapi.Version;
import net.boreeas.riotapi.com.riotgames.platform.game.QueueType;
import net.boreeas.riotapi.constants.Season;
import net.boreeas.riotapi.Shard;
import net.boreeas.riotapi.com.riotgames.leagues.pojo.LeagueList;
import net.boreeas.riotapi.com.riotgames.leagues.pojo.LeagueItem;
import net.boreeas.riotapi.com.riotgames.platform.summoner.spellbook.RunePage;
import net.boreeas.riotapi.rest.api.CurrentGameHandler;
import net.boreeas.riotapi.rest.api.FeaturedGamesHandler;
import net.boreeas.riotapi.rest.api.LoLRestApi;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created on 4/23/2014.
 */
public class ThrottledApiHandler implements AutoCloseable, LoLRestApi {

    public static final int PERIOD = 50; // 0.05s

    private float[] limits;
    private Queue<ApiFuture> pending = new LinkedList<>();
    private Timer timer = new Timer(true);

    private ApiHandler handler;

    @Getter public final AsyncCurrentGameHandler currentGameHandler = new AsyncCurrentGameHandler();
    @Getter public final AsyncFeaturedGamesHandler featuredGamesHandler = new AsyncFeaturedGamesHandler();


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

    public class AsyncCurrentGameHandler implements CurrentGameHandler {

        @Override
        public CurrentGameInfo getCurrentGameInfo(long summoner) {
            try {
                return asyncGetCurrentGameInfo(summoner).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public Future<CurrentGameInfo> asyncGetCurrentGameInfo(long summoner) {
            return new ApiFuture<>(() -> handler.currentGameHandler.getCurrentGameInfo(summoner));
        }

        @Override
        public Version getVersion() {
            return handler.currentGameHandler.getVersion();
        }
    }

    public class AsyncFeaturedGamesHandler implements FeaturedGamesHandler {

        @Override
        public FeaturedGames getFeaturedGames() {
            try {
                return asyncGetFeaturedGames().get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public Future<FeaturedGames> asyncGetFeaturedGames() {
            return new ApiFuture<>(handler.featuredGamesHandler::getFeaturedGames);
        }

        @Override
        public Version getVersion() {
            return handler.getFeaturedGamesHandler().getVersion();
        }
    }

    // <editor-fold desc="Champion">

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

    // </editor-fold>

    // <editor-fold desc="Game">

    /**
     * Get a listing of recent games for the summoner
     * @param summoner The id of the summoner
     * @return A list of recently played games
     * @see <a href=https://developer.riotgames.com/api/methods#!/618/1924>Official API documentation</a>
     */
    public Future<List<Game>> getRecentGames(long summoner) {
        return new ApiFuture<>(() -> handler.getRecentGames(summoner));
    }

    // </editor-fold>

    // <editor-fold desc="League">

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
     * Get a listing of leagues for the specified summoners
     * @param summoners The ids of the summoners
     * @return A list of leagues
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1862>Official API documentation</a>
     */
    public Future<Map<Long, List<LeagueList>>> getLeagues(long... summoners) {
        return new ApiFuture<>(() -> handler.getLeagues(summoners));
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
     * Get a listing of all league entries in the summoners' leagues
     * @param summoners The ids of the summoners
     * @return A map, mapping summoner ids to lists of league entries for that summoner
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1863>Official API documentation</a>
     */
    public Future<Map<Long, List<LeagueItem>>> getLeagueEntries(long... summoners) {
        return new ApiFuture<>(() -> handler.getLeagueEntries(summoners));
    }

    /**
     * Get a listing of leagues for the specified team
     * @param teamId The id of the team
     * @return A list of leagues
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1860>Official API documentation</a>
     */
    public Future<List<LeagueList>> getLeagues(String teamId) {
        return new ApiFuture<>(() -> handler.getLeagues(teamId));
    }

    /**
     * Get a listing of leagues for the specified teams
     * @param teamIds The ids of the team
     * @return A mapping of team ids to lists of leagues
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1860>Official API documentation</a>
     */
    public Future<Map<String, List<LeagueList>>> getLeagues(String... teamIds) {
        return new ApiFuture<>(() -> handler.getLeagues(teamIds));
    }

    /**
     * Get a listing of all league entries in the team's leagues
     * @param teamId The id of the team
     * @return A list of league entries
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1861>Official API documentation</a>
     */
    public Future<List<LeagueItem>> getLeagueEntries(String teamId) {
        return new ApiFuture<>(() -> handler.getLeagueEntries(teamId));
    }

    /**
     * Get a listing of all league entries in the teams' leagues
     * @param teamIds The ids of the teams
     * @return A mapping of teamIds to lists of league entries
     * @see <a href=https://developer.riotgames.com/api/methods#!/593/1861>Official API documentation</a>
     */
    public Future<Map<String, List<LeagueItem>>> getLeagueEntries(String... teamIds) {
        return new ApiFuture<>(() -> handler.getLeagueEntries(teamIds));
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


    // </editor-fold>

    // <editor-fold desc="Static Data">

    /**
     * Get champion information for all champions
     * @return Champion information for all champions
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2171>Official API documentation</a>
     */
    public Future<ChampionList> getChampionListDto() {
        return new DummyFuture<>(handler.getChampionListDto());
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
    public Future<ChampionList> getChampionListDto(ChampData champData) {
        return new DummyFuture<>(handler.getChampionListDto(champData));
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
    public Future<ChampionList> getChampionListDto(String locale, String version, boolean dataById, ChampData champData) {
        return new DummyFuture<>(handler.getChampionListDto(locale, version, dataById, champData));
    }

    /**
     * <p>
     * All champions in the game.
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return All champions in the game
     */
    public Future<Collection<Champion>> getChampions() {
        return new DummyFuture<>(handler.getChampions());
    }

    /**
     * <p>
     * All champions in the game.
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @param champData Additional information to retrieve
     * @return All champions in the game
     */
    public Future<Collection<Champion>> getChampions(ChampData champData) {
        return new DummyFuture<>(handler.getChampions(champData));
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
    public Future<Collection<Champion>> getChampions(ChampData champData, String version, String locale, boolean dataById) {
        return new DummyFuture<>(handler.getChampions(champData, version, locale, dataById));
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
    public Future<Champion> getChampion(int id) {
        return new DummyFuture<>(handler.getChampion(id));
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
    public Future<Champion> getChampion(int id, ChampData champData) {
        return new DummyFuture<>(handler.getChampion(id, champData));
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
    public Future<Champion> getChampion(int id, ChampData champData, String version, String locale) {
        return new DummyFuture<>(handler.getChampion(id, champData, version, locale));
    }

    /**
     * <p>
     * Get a listing of items in the game
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return The list of items
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2166>Official API documentation</a>
     */
    public Future<ItemList> getItemList() {
        return new DummyFuture<>(handler.getItemList());
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
    public Future<ItemList> getItemList(ItemData data) {
        return new DummyFuture<>(handler.getItemList(data));
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
    public Future<ItemList> getItemList(ItemData data, String version, String locale) {
        return new DummyFuture<>(handler.getItemList(data, version, locale));
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
    public Future<Item> getItem(int id) {
        return new DummyFuture<>(handler.getItem(id));
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
    public Future<Item> getItem(int id, ItemData data) {
        return new DummyFuture<>(handler.getItem(id, data));
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
    public Future<Item> getItem(int id, ItemData data, String version, String locale) {
        return new DummyFuture<>(handler.getItem(id, data, version, locale));
    }

    /**
     * <p>
     * Get a listing of all masteries
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return The masteries
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2173>Official API documentation</a>
     */
    public Future<MasteryList> getMasteries() {
        return new DummyFuture<>(handler.getMasteries());
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
    public Future<MasteryList> getMasteries(MasteryData data) {
        return new DummyFuture<>(handler.getMasteries(data));
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
    public Future<MasteryList> getMasteries(MasteryData data, String version, String locale) {
        return new DummyFuture<>(handler.getMasteries(data, version, locale));
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
    public Future<Mastery> getMastery(int id) {
        return new DummyFuture<>(handler.getMastery(id));
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
    public Future<Mastery> getMastery(int id, MasteryData data) {
        return new DummyFuture<>(handler.getMastery(id, data));
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
    public Future<Mastery> getMastery(int id, MasteryData data, String version, String locale) {
        return new DummyFuture<>(handler.getMastery(id, data, version, locale));
    }

    /**
     * <p>
     * Get realm information for this region
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return Realm information
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2170>Official API documentation</a>
     */
    public Future<Realm> getRealm() {
        return new DummyFuture<>(handler.getRealm());
    }

    /**
     * <p>
     * Get a list of all runes
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return All runes
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2172>Official API documentation</a>
     */
    public Future<RuneList> getRuneList() {
        return new DummyFuture<>(handler.getRuneList());
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
    public Future<RuneList> getRuneList(ItemData data) {
        return new DummyFuture<>(handler.getRuneList(data));
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
    public Future<RuneList> getRuneList(ItemData data, String version, String locale) {
        return new DummyFuture<>(handler.getRuneList(data, version, locale));
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
    public Future<Item> getRune(int id) {
        return new DummyFuture<>(handler.getRune(id));
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
    public Future<Item> getRune(int id, ItemData data) {
        return new DummyFuture<>(handler.getRune(id, data));
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
    public Future<Item> getRune(int id, ItemData data, String version, String locale) {
        return new DummyFuture<>(handler.getRune(id, data, version, locale));
    }

    /**
     * <p>
     * Get a list of all summoner spells as returned by the API
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public Future<SummonerSpellList> getSummonerSpellListDto() {
        return new DummyFuture<>(handler.getSummonerSpellListDto());
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
    public Future<SummonerSpellList> getSummonerSpellListDto(SpellData data) {
        return new DummyFuture<>(handler.getSummonerSpellListDto(data));
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
    public Future<SummonerSpellList> getSummonerSpellListDro(SpellData data, String version, String locale, boolean dataById) {
        return new DummyFuture<>(handler.getSummonerSpellListDro(data, version, locale, dataById));
    }

    /**
     * <p>
     * Get a list of all summoner spells as Java Collection
     * </p>
     * This method does not count towards the rate limit and is not affected by the throttle
     * @return The summoner spells
     * @see <a href=https://developer.riotgames.com/api/methods#!/649/2174>Official API documentation</a>
     */
    public Future<Collection<SummonerSpell>> getSummonerSpells() {
        return new DummyFuture<>(handler.getSummonerSpells());
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
    public Future<Collection<SummonerSpell>> getSummonerSpells(SpellData data) {
        return new DummyFuture<>(handler.getSummonerSpells(data));
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
    public Future<Collection<SummonerSpell>> getSummonerSpells(SpellData data, String version, String locale, boolean dataById) {
        return new DummyFuture<>(handler.getSummonerSpells(data, version, locale, dataById));
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
    public Future<SummonerSpell> getSummonerSpell(int id) {
        return new DummyFuture<>(handler.getSummonerSpell(id));
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
    public Future<SummonerSpell> getSummonerSpell(int id, SpellData data) {
        return new DummyFuture<>(handler.getSummonerSpell(id, data));
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
    public Future<SummonerSpell> getSummonerSpell(int id, SpellData data, String version, String locale) {
        return new DummyFuture<>(handler.getSummonerSpell(id, data, version, locale));
    }

    /**
     * <p>
     * Retrieve currently supported game versions.
     * </p>
     * This method does not count towards the rate limit
     *
     * @return A list of supported game versions
     * @see <a href=https://developer.riotgames.com/api/methods#!/710/2527>Official API documentation</a>
     */
    public Future<List<String>> getVersions() {
        return new DummyFuture<>(handler.getVersions());
    }

    /**
     * <p>
     * Retrieve map information
     * </p>
     * This method does not count towards the rate limit
     *
     * @return The list of all available map.
     * @see <a href="https://developer.riotgames.com/api/methods#!/931">The official api documentation</a>
     */
    public Future<MapDataOverview> getMaps() {
        return new DummyFuture<>(handler.getMaps());
    }

    /**
     * <p>
     * Retrieve map information
     * </p>
     * This method does not count towards the rate limit
     *
     * @param version The data dragon version.
     * @param locale The locale information.
     *
     * @return The list of all available maps.
     * @see <a href="https://developer.riotgames.com/api/methods#!/931">The official api documentation</a>
     */
    public Future<MapDataOverview> getMaps(String version, String locale) {
        return new DummyFuture<>(handler.getMaps(version, locale));
    }

    /**
     * <p>
     *     Retrieve supported locales for the specified region
     * </p>
     * This method does not count towards your rate limit
     *
     * @return A list of locales
     * @see <a href="https://developer.riotgames.com/api/methods#!/931/3226">The official api documentation</a>
     */
    public Future<List<String>> getLocales() {
        return new DummyFuture<>(handler.getLocales());
    }

    /**
     * <p>
     *     Retrieve localized strings for the english locale
     * </p>
     * This method does not count towards your rate limit
     *
     * @return A list of localized message
     * @see <a href="https://developer.riotgames.com/api/methods#!/931/3226">The official api documentation</a>
     */
    public Future<LocalizedMessages> getLocalizedMessages() {
        return new DummyFuture<>(handler.getLocalizedMessages());
    }


    /**
     * <p>
     *     Retrieve localized strings for the english locale
     * </p>
     * This method does not count towards your rate limit
     *
     * @param version The data dragon version of the data
     * @param locale The locale to lookup.
     *
     * @return A list of localized message
     * @see <a href="https://developer.riotgames.com/api/methods#!/931/3226">The official api documentation</a>
     */
    public Future<LocalizedMessages> getLocalizedMessages(String version, String locale) {
        return new DummyFuture<>(handler.getLocalizedMessages(version, locale));
    }
    // </editor-fold>

    // <editor-fold desc="Status">
    /**
     * <p>
     * Retrieves general information about each shard.
     * </p><br>
     * This method does not count towards your rate limit.
     * @return A list of shard infomation.
     * @see <a href="https://developer.riotgames.com/api/methods#!/835/2939">Official API Documentation</a>
     */
    public Future<List<ShardData>> getShards() {
        return new DummyFuture<>(handler.getShards());
    }

    /**
     * <p>
     * Retrieves detailed information about the specified shard.
     * </p><br>
     * This method does not count towards your rate limit.
     * @param shard The target region
     * @return A list of shard infomation.
     * @deprecated Use #getShardStatus instead.
     * @see <a href="https://developer.riotgames.com/api/methods#!/835/2938">Official API Documentation</a>
     */
    @Deprecated
    public Future<ShardStatus> getShardSatatus(Shard shard) {
        return new DummyFuture<>(handler.getShardStatus(shard));
    }

    /**
     * <p>
     * Retrieves detailed information about the specified shard.
     * </p><br>
     * This method does not count towards your rate limit.
     * @param shard The target region
     * @return A list of shard infomation.
     * @see <a href="https://developer.riotgames.com/api/methods#!/835/2938">Official API Documentation</a>
     */
    public Future<ShardStatus> getShardStatus(Shard shard) {
        return new DummyFuture<>(handler.getShardStatus(shard));
    }
    // </editor-fold>

    // <editor-fold desc="Match">
    /**
     * Retrieves the specified match, including timeline.
     * Equivalent to <code>getMatch(matchId, true);</code>
     * @param matchId The id of the match.
     * @return The match details.
     * @see <a href="https://developer.riotgames.com/api/methods#!/806/2848">Official API Documentation</a>
     */
    public Future<MatchDetail> getMatch(long matchId) {
        return new ApiFuture<>(() -> handler.getMatch(matchId));
    }

    /**
     * Retrieves the specified match.
     * @param matchId The id of the match.
     * @param includeTimeline Whether or not the event timeline should be retrieved.
     * @return The match details.
     * @see <a href="https://developer.riotgames.com/api/methods#!/806/2848">Official API Documentation</a>
     */
    public Future<MatchDetail> getMatch(long matchId, boolean includeTimeline) {
        return new ApiFuture<>(() -> handler.getMatch(matchId, includeTimeline));
    }
    // </editor-fold>

    // <editor-fold desc="Matchhistory">
    /**
     * Retrieve a player's match history.
     * @param playerId The id of the player.
     * @return The match history of the player.
     * @see <a href="https://developer.riotgames.com/api/methods#!/805/2847">Official API Documentation</a>
     */
    public Future<List<MatchSummary>> getMatchHistory(long playerId) {
        return new ApiFuture<>(() -> handler.getMatchHistory(playerId));
    }

    /**
     * Retrieve a player's match history.
     *
     * @param playerId    The id of the player.
     * @param championIds The championIds to use for retrieval.
     * @return The match history of the player.
     * @see <a href="https://developer.riotgames.com/api/methods#!/805/2847">Official API Documentation</a>
     */
    public Future<List<MatchSummary>> getMatchHistory(long playerId, String... championIds) {
        return new ApiFuture<>(() -> handler.getMatchHistory(playerId, championIds));
    }

    /**
     * Retrieve a player's match history, filtering out all games not in the specified queues.
     *
     * @param playerId    The id of the player.
     * @param championIds The championIds to use for retrieval.
     * @param queueTypes  The queue types to retrieve (must be one of RANKED_SOLO_5x5, RANKED_TEAM_3x3 or
     *                    RANKED_TEAM_5x5).
     * @return The match history of the player.
     * @see <a href="https://developer.riotgames.com/api/methods#!/805/2847">Official API Documentation</a>
     */
    public Future<List<MatchSummary>> getMatchHistory(long playerId, String[] championIds, QueueType... queueTypes) {
        return new ApiFuture<>(() -> handler.getMatchHistory(playerId, championIds, queueTypes));
    }

    /**
     * Retrieve a player's match history, filtering out all games not in the specified queues.
     * This method only returns games starting with beginIndex and ending at endIndex.
     *
     * @param playerId    The id of the player.
     * @param championIds The championIds to use for retrieval.
     * @param queueTypes  The queue types to retrieve (must be one of RANKED_SOLO_5x5, RANKED_TEAM_3x3 or
     *                    RANKED_TEAM_5x5).
     * @param beginIndex  The index of the first game that should be retrieved.
     * @param endIndex    The index of the last game the should be retrieved.
     * @return The match history of the player.
     * @see <a href="https://developer.riotgames.com/api/methods#!/805/2847">Official API Documentation</a>
     */
    public Future<List<MatchSummary>> getMatchHistory(long playerId, String[] championIds, QueueType[] queueTypes, int beginIndex, int endIndex) {
        return new ApiFuture<>(() -> handler.getMatchHistory(playerId, championIds, queueTypes, beginIndex, endIndex));
    }
    // </editor-fold>

    // <editor-fold desc="Stats">

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

    // </editor-fold>

    // <editor-fold desc="Summoner">

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
    public Future<Map<Integer, Set<RunePage>>> getRunePagesMultipleUsers(int... ids) {
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

    // </editor-fold>

    // <editor-fold desc="Team">

    /**
     * Retrieve the ranked teams of a user
     * @param id The user's id
     * @return The ranked teams of the user
     * @see <a href=https://developer.riotgames.com/api/methods#!/594/1865>Official API documentation</a>
     */
    public Future<List<RankedTeam>> getTeams(long id) {
        return new ApiFuture<>(() -> handler.getTeamsBySummoner(id));
    }

    /**
     * Retrieve the ranked teams of the specified users
     * @param ids The users' ids
     * @return The ranked teams of the users
     * @see <a href=https://developer.riotgames.com/api/methods#!/594/1865>Official API documentation</a>
     */
    public Future<Map<Long, List<RankedTeam>>> getTeams(long... ids) {
        return new ApiFuture<>(() -> handler.getTeamsBySummoners(ids));
    }

    /**
     * Retrieve information for the specified ranked team
     * @param teamId The team to retrieve
     * @return Information about the specified team
     * @see <a href=https://developer.riotgames.com/api/methods#!/594/1866>Official API documentation</a>
     */
    public Future<RankedTeam> getTeam(String teamId) {
        return new ApiFuture<>(() -> handler.getTeam(teamId));
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

    // </editor-fold>

    // <editor-fold desc="Utility">

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

    // </editor-fold>


    /**
     * Stops the threads handling pending requests. No further requests will
     * be executed, but any running thread will complete first
     */
    public void close() {
        timer.cancel();
    }


    @AllArgsConstructor
    public static class Limit {
        public final int maxValue;
        public final int timeDeltaToMax;
        public final TimeUnit unit;
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

            if (!signal.await(timeout, unit)) {
                throw new TimeoutException("Wait time for reply exceeded " + timeout + " " + unit);
            }

            if (err != null) {
                throw new ExecutionException(err);
            }

            return value;
        }

        public Exception getErr() {
            return err;
        }
    }

    private class DummyFuture<T> implements Future<T> {

        private T value;

        private DummyFuture(T value) {
            this.value = value;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            return value;
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return value;
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
                new Limit(10, 10, TimeUnit.SECONDS),
                new Limit(500, 10, TimeUnit.MINUTES));
    }
}
