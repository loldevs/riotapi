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

package net.boreeas.riotapi.loginqueue.newlq;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by malte on 7/12/2014.
 */
@Getter
@ToString
public class IngameCredentials {
    private String other;
    private String fingerprint;
    private String signature;
    private long timestamp;
    private String uuid;
    private String resources;
    private long account_id;
    private String account_name;
}
