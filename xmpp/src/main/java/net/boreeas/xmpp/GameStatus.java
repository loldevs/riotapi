package net.boreeas.xmpp;

import lombok.Getter;

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