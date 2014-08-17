package riotapi.xmpp.com.jdiaz.xmpp;

import javax.xml.bind.annotation.XmlRootElement;

import riotapi.xmpp.com.jdiaz.xmpp.XmppClient.ChatType;
import lombok.Data;
import lombok.Getter;

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
	
	public enum GameStatus {
		OUT_OF_GAME("outOfGame"),
		IN_QUEUE("inQueue"),
		SPECTATING("spectating"),
		CHAMPION_SELECT("championSelect"),
		IN_GAME("inGame"),
		HOSTING_PRACTICE_GAME("hostingPracticeGame");
		
		private @Getter String status;
		
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
}
