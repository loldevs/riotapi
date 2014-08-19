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

package net.boreeas.xmpp;

public enum GameStatus {
	OUT_OF_GAME("outOfGame"),
	IN_QUEUE("inQueue"),
	SPECTATING("spectating"),
	CHAMPION_SELECT("championSelect"),
	IN_GAME("inGame"),
	HOSTING_PRACTICE_GAME("hostingPracticeGame");
	
	public final String status;
	
	private GameStatus(String status) {
		this.status = status;
	}
	
	public GameStatus resolve(String status) {
		for (GameStatus t : values()) {
			if (t.status.equals(status)) {
				return t;
			}
		}
		return null;
	}
}