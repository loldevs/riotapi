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

import lombok.Data;
import org.jivesoftware.smack.packet.Presence;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;
import java.io.StringWriter;

@Data
@XmlRootElement(name = "body")
public class RiotStatus {
	private String status;
	private int profileIcon;
	private int level;
	private int wins;
	private int leaves;
	private int odinWins;
	private int odinLeaves;
	private String queueTypeString;
	private int rankedLosses;
	private int rankedRating;
	private String tier;
	private String rankedLeagueName;
	private String rankedLeagueDivision;
	private String rankedLeagueTier;
	private String rankedLeagueQueue;
	private int rankedWins;
	private String gameStatus;
	private String statusMsg;
	
	public static RiotStatus parsePresence(Presence presence) {
		return JAXB.unmarshal(new StringReader(presence.getStatus()), RiotStatus.class);
	}

	public String toSring() {
		StringWriter writer = new StringWriter();
		JAXB.marshal(this, writer);
		return writer.toString();
	}
}
