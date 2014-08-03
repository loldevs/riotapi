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

package net.boreeas.riotapi.rtmp.services;

import lombok.AllArgsConstructor;
import net.boreeas.riotapi.com.riotgames.platform.game.*;
import net.boreeas.riotapi.com.riotgames.platform.game.practice.PracticeGameSearchResult;
import net.boreeas.riotapi.rest.Team;
import net.boreeas.riotapi.rtmp.RtmpClient;

import java.util.List;

/**
 * Created by malte on 7/18/2014.
 */
@AllArgsConstructor
public class GameService {
    private static final String SERVICE = "gameService";
    private RtmpClient client;

    public List<PracticeGameSearchResult> listAllPracticeGame() {
        return client.sendRpcAndWait(SERVICE, "listAllPracticeGames");
    }

    public Object joinGame(double id) {
        return client.sendRpcAndWait(SERVICE, "joinGame", id, null);
    }

    public Object joinGame(double id, String password) {
        return client.sendRpcAndWait(SERVICE, "joinGame", id, password);
    }

    public Object observeGame(double id) {
        return client.sendRpcAndWait(SERVICE, "observeGame", id, null);
    }

    public Object observeGame(double id, String password) {
        return client.sendRpcAndWait(SERVICE, "observeGame", id, password);
    }

    public boolean switchTeams(double id) {
        return client.sendRpcAndWait(SERVICE, "switchTeams", id);
    }

    public boolean switchToObserver(double id) {
        return client.sendRpcAndWait(SERVICE, "switchPlayerToObserver", id);
    }

    public boolean switchToPlayer(double id, Team team) {
        return client.sendRpcAndWait(SERVICE, "switchObserverToPlayer", id, team.id);
    }

    public Object quitGame() {
        return client.sendRpcAndWait(SERVICE, "quitGame");
    }

    public Game createPracticeGame(PracticeGameConfig config) {
        return client.sendRpcAndWait(SERVICE, "createPracticeGame", config);
    }

    public StartChampSelect startChampionSelect(double gameId, double lock) {
        return client.sendRpcAndWait(SERVICE, "startChampionSelect", gameId, lock);
    }

    public Object setClientReceivedGameMessage(double gameId, String argument) {
        return client.sendRpcAndWait(SERVICE, "setClientReceivedGameMessage", gameId, argument);
    }

    public Game getLatestGameTimerState(double gameId, String state, int pickTurn) {
        return client.sendRpcAndWait(SERVICE, "getLatestGameTimerState", gameId, state, pickTurn);
    }

    public Object selectSpells(int spell1, int spell2) {
        return client.sendRpcAndWait(SERVICE, "selectSpells", spell1, spell2);
    }

    public Object selectChampion(int championId) {
        return client.sendRpcAndWait(SERVICE, "selectChampion", championId);
    }

    public Object selectChampionSkin(int championId, int skinId) {
        return client.sendRpcAndWait(SERVICE, "selectChampionSkin", championId, skinId);
    }

    public Object championSelectCompleted() {
        return client.sendRpcAndWait(SERVICE, "championSelectCompleted");
    }

    public PlatformGameLifecycle retrieveInProgressSpectatorGameInfo(String summonerInfo) {
        return client.sendRpcAndWait(SERVICE, "retrieveInProgressSpectatorGameInfo", summonerInfo);
    }

    public Object acceptPoppedGame(boolean accept) {
        return client.sendRpcAndWait(SERVICE, "acceptPoppedGame", accept);
    }

    public Object banUserFromGame(double gameId, double accountId) {
        return client.sendRpcAndWait(SERVICE, "banUserFromGame", gameId, accountId);
    }

    public Object banObserverFromGame(double gameId, double accountId) {
        return client.sendRpcAndWait(SERVICE, "banObserverFromGame", gameId, accountId);
    }

    public Object banChampion(int championId) {
        return client.sendRpcAndWait(SERVICE, "banChampion", championId);
    }

    public List<ChampionBanInfo> getChampionsForBan() {
        return client.sendRpcAndWait(SERVICE, "getChampionsForBan");
    }
}
