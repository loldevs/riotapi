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

package net.boreeas.riotapi.com.riotgames.platform.account.impl;

/**
 * Created on 8/2/2014.
 */
public enum AccountState {
    CREATING,
    ENABLED,
    TRANSFERRING_IN,
    TRANSFERRING_OUT,
    TRANSFERRED_OUT,
    GENERATING;

    public static AccountState getByName(String name) {
        switch (name) {
            case "CREATING":        return CREATING;
            case "ENABLED":         return ENABLED;
            case "TRANSFERRING_IN": return TRANSFERRING_IN;
            case "TRANSFERRING_OUT":return TRANSFERRING_OUT;
            case "TRANSFERRED_OUT": return TRANSFERRED_OUT;
            case "GENERATING":      return GENERATING;
            default:                return null;
        }
    }
}
