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

import net.boreeas.riotapi.rest.FeaturedGames;

/**
 * @author Malte Schütze
 */
public interface FeaturedGamesHandler extends Versionable {
    /**
     * Retrieve a list of featured games as well as information how often the listing changes.
     * @return Featured game information
     * @see <a href="https://developer.riotgames.com/api/methods#!/951/3282">The official API documentation</a>
     */
    FeaturedGames getFeaturedGames();
}
