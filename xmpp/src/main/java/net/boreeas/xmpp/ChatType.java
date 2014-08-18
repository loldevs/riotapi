package net.boreeas.xmpp;

import lombok.Getter;

public enum ChatType {

	ARRANGING_PRACTICE("ap"),
	RANKED_TEAM("tm"),
	CHAMPION_SELECT1("c1"),
	CHAMPION_SELECT2("c2"),
	PRIVATE("pr"),
	ARRANGING_GAME("ag"),
	GLOBAL("gl"),
	PUBLIC("pu"),
	CAP("cp"),
	QUEUED("aq"),
	CTA("cta"),
	POST_GAME("pg");

	private @Getter String type;

	private ChatType(String type) {
		this.type = type;
	}

	public ChatType resolve(String type) {
		for (ChatType t : values()) {
			if (t.type.equals(type)) {
				return t;
			}
		}
		return null;
	}
}
