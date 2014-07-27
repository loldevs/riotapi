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

package net.boreeas.riotapi.com.riotgames.platform.account;

import lombok.Data;
import net.boreeas.riotapi.rtmp.serialization.Serialization;

/**
 * Created on 7/19/2014.
 */
@Data
@Serialization(name = "com.riotgames.platform.account.AccountSummary")
public class AccountSummary {
    private int groupCount;
    private String username;
    private long accountId;
    private String summonerInternalName;
    private boolean admin;
    private boolean hasBetaAccess;
    private String summonerName;
    private boolean partnerMode;
    private boolean needsPasswordReset;
    private int dataVersion;
    private Object futureData;
}
