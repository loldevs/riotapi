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
import net.boreeas.riotapi.com.riotgames.platform.game.*;
import net.boreeas.riotapi.com.riotgames.platform.game.practice.PracticeGameSearchResult;
import net.boreeas.riotapi.constants.PlayerSide;
import net.boreeas.riotapi.rtmp.RtmpClient;

import java.util.List;

/**
 * This services handles custom game creation and management, champion select and accepting queues.
 * Retrieving spectator info is also done here
 */
@AllArgsConstructor
public class GameService {
    public static final String SERVICE = "gameService";
    private RtmpClient client;

    /**
     * All currently open custom games
     * @return The list of all custon games
     */
    public List<PracticeGameSearchResult> listAllPracticeGame() {
        return client.sendRpcAndWait(SERVICE, "listAllPracticeGames");
    }

    /**
     * Join a public game
     * @param id The id of the game
     * @return unknown
     */
    public Object joinGame(long id) {
        return client.sendRpcAndWait(SERVICE, "joinGame", id, null);
    }

    /**
     * Join a password-protected game.
     * @param id The id of the game
     * @param password The password of the game
     * @return unknown
     */
    public Object joinGame(long id, String password) {
        return client.sendRpcAndWait(SERVICE, "joinGame", id, password);
    }

    /**
     * Join a game as a spectator
     * @param id The game id
     * @return unknown
     */
    public Object observeGame(long id) {
        return client.sendRpcAndWait(SERVICE, "observeGame", id, null);
    }

    /**
     * Join a password-protected game as a spectator
     * @param id The game id
     * @param password The password of the game
     * @return unknown
     */
    public Object observeGame(long id, String password) {
        return client.sendRpcAndWait(SERVICE, "observeGame", id, password);
    }

    /**
     * Switch teams
     * @param id Unknown - team id? Game id?
     * @return unknown - True if the switch succeeded?
     */
    public boolean switchTeams(long id) {
        return client.sendRpcAndWait(SERVICE, "switchTeams", id);
    }

    /**
     * Switch to observer
     * @param id Unknown - game id?
     * @return unknown - True if the switch succeeded?
     */
    public boolean switchToObserver(long id) {
        return client.sendRpcAndWait(SERVICE, "switchPlayerToObserver", id);
    }

    /**
     * Switch from observer to player
     * @param id The id of the game
     * @param team The team to join
     * @return unknown - true if the switch succeeded?
     */
    public boolean switchToPlayer(long id, PlayerSide team) {
        return client.sendRpcAndWait(SERVICE, "switchObserverToPlayer", id, team.id);
    }

    /**
     * Quit the current game
     * @return unknown
     */
    public Object quitGame() {
        return client.sendRpcAndWait(SERVICE, "quitGame");
    }

    /**
     * Create a custom game
     * @param config The parameters of the custom game
     * @return The created game
     */
    public Game createPracticeGame(PracticeGameConfig config) {
        return client.sendRpcAndWait(SERVICE, "createPracticeGame", config);
    }

    /**
     * Start a custom game
     * @param gameId The id of the game
     * @param lock Unknown
     * @return Champion select info
     */
    public StartChampSelect startChampionSelect(long gameId, long lock) {
        return client.sendRpcAndWait(SERVICE, "startChampionSelection", gameId, lock);
    }

    /**
     * Unknown?
     * @param gameId The id of the game
     * @param argument Unknown
     * @return Unknown
     */
    public Object setClientReceivedGameMessage(long gameId, String argument) {
        return client.sendRpcAndWait(SERVICE, "setClientReceivedGameMessage", gameId, argument);
    }

    /**
     * Not sure
     * @param gameId The id of the game
     * @param state The current game state
     * @param pickTurn Unknown
     * @return Game info
     */
    public Game getLatestGameTimerState(double gameId, String state, int pickTurn) {
        return client.sendRpcAndWait(SERVICE, "getLatestGameTimerState", gameId, state, pickTurn);
    }

    /**
     * Select the summoner spells for this game
     * @param spell1 The id of the first spell
     * @param spell2 The id of the second spell
     * @return Unknown
     */
    public Object selectSpells(int spell1, int spell2) {
        return client.sendRpcAndWait(SERVICE, "selectSpells", spell1, spell2);
    }

    /**
     * Select a champion in champ select.
     * @param championId The id of the champion
     * @return Unknown
     */
    public Object selectChampion(int championId) {
        return client.sendRpcAndWait(SERVICE, "selectChampion", championId);
    }

    /**
     * Select a skin for the target champion.
     * @param championId The id of the champion
     * @param skinId The id of the skin
     * @return Unknown
     */
    public Object selectChampionSkin(int championId, int skinId) {
        return client.sendRpcAndWait(SERVICE, "selectChampionSkin", championId, skinId);
    }

    /**
     * Lock in the current choice
     * @return Unknown
     */
    public Object championSelectCompleted() {
        return client.sendRpcAndWait(SERVICE, "championSelectCompleted");
    }

    /**
     * Retrieve spectator info for the target summoner
     * @param summonerName The name of the player
     * @return The spectator info for the game
     */
    public PlatformGameLifecycle retrieveInProgressSpectatorGameInfo(String summonerName) {
        return client.sendRpcAndWait(SERVICE, "retrieveInProgressSpectatorGameInfo", summonerName);
    }

    /**
     * Accept or decline a popped queue
     * @param accept <code>true</code> if the game should be accepted, false otherwise
     * @return Unknown
     */
    public Object acceptPoppedGame(boolean accept) {
        return client.sendRpcAndWait(SERVICE, "acceptPoppedGame", accept);
    }

    /**
     * Kick a player from a game
     * @param gameId The id of the game
     * @param accountId The players account id
     * @return Unknown
     */
    public Object banUserFromGame(long gameId, long accountId) {
        return client.sendRpcAndWait(SERVICE, "banUserFromGame", gameId, accountId);
    }

    /**
     * Kick a spectator from a custom game
     * @param gameId The id of the game
     * @param accountId The spectators account id
     * @return unknown
     */
    public Object banObserverFromGame(long gameId, long accountId) {
        return client.sendRpcAndWait(SERVICE, "banObserverFromGame", gameId, accountId);
    }

    /**
     * Ban a champion from the current game
     * @param championId The id of the champion that should be banned.
     * @return unknown
     */
    public Object banChampion(int championId) {
        return client.sendRpcAndWait(SERVICE, "banChampion", championId);
    }

    /**
     * Retrieve all bannable champions
     * @return A list of champion ban info
     */
    public List<ChampionBanInfo> getChampionsForBan() {
        return client.sendRpcAndWait(SERVICE, "getChampionsForBan");
    }
}
