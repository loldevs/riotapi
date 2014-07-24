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

package net.boreeas.riotapi.com.riotgames.platform.gameinvite.contract;

import lombok.Data;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/19/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform.gameinvite.contract.LobbyStatus")
public class LobbyStatus {
    private String chatKey;
    private String gameMetaData;
    private Player owner;
    private List<Double> members = new ArrayList<>();
    private List<Invitee> invitees = new ArrayList<>();
    private String invitationId;
}
