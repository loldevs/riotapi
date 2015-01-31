/*
 * Copyright 2015 The LolDevs team (https://github.com/loldevs)
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

package net.boreeas.riotapi.rest.api;

import net.boreeas.riotapi.rest.CurrentGameInfo;

/**
 * @author Malte Schütze
 */
public interface CurrentGameHandler extends Versionable {
    /**
     * <p>
     *     Retrieves information about a game that is currently in progress.
     * </p>
     * <p>
     *     Note that this method throws a RequestException with error code 404 if the player isn't currently in a game
     * </p>
     * @param summoner The summoner for whom to check.
     * @return The current game information
     * @see <a href="https://developer.riotgames.com/api/methods#!/950/3281">The offical API documentation</a>
     */
    CurrentGameInfo getCurrentGameInfo(long summoner);
}
