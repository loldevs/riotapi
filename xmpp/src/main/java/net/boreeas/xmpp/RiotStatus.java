package net.boreeas.xmpp;

import java.io.StringReader;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

import org.jivesoftware.smack.packet.Presence;

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
}
