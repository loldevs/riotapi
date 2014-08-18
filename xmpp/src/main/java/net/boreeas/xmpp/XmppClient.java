package net.boreeas.xmpp;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.JAXB;

import lombok.Getter;
import net.boreeas.riotapi.Shard;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class XmppClient extends XMPPTCPConnection {

	private ArrayList<XmppListener> listeners;
	private ArrayList<MultiUserChat> chatRooms;

	private @Getter Shard server;
	private @Getter String user;
	private String pass;
	
	/*static {
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
	}*/

	public XmppClient(Shard server, String user, String pass) {
		super(buildConnectionConfiguration(server));
		this.server = server;
		this.user = user;
		this.pass = pass;
		listeners = new ArrayList<XmppListener>();
		chatRooms = new ArrayList<MultiUserChat>();

		addListeners();
	}

	@Override
	public void connect() throws SmackException, IOException, XMPPException {
		
		super.connect();
		login(user, "AIR_" + pass, "xiff");

		addListeners();

		Collection<RosterEntry> entries = getRoster().getEntries();
		for (RosterEntry entry : entries) {
			String name = entry.getUser();
			String nam = entry.getName();

			System.out.println("User: "+name+"\tName: "+nam);
		}
	}

	private static ConnectionConfiguration buildConnectionConfiguration(Shard shard) {
		ConnectionConfiguration connConf = new ConnectionConfiguration(shard.chatUrl, Shard.JABBER_PORT, "pvp.net");
		connConf.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		connConf.setSocketFactory(SSLSocketFactory.getDefault());
		return connConf;
	}

	private void addListeners() {
		//Presence Listener
		addPacketListener(new PacketListener() {

			@Override
			public void processPacket(Packet packet) throws NotConnectedException {
				try {
					if (packet instanceof Presence) {
						Presence p = (Presence) packet;
						if (p.getStatus() == null) { return; }
						RiotStatus status = JAXB.unmarshal(new StringReader(p.getStatus()), RiotStatus.class);
						for (XmppListener listener : listeners) {
							listener.statusReceived(status);
						}
					} else if (packet instanceof Message) {
						Message m = (Message) packet;
						for (XmppListener listener : listeners) {
							listener.messageReceived(m);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new PacketFilter() {

			@Override
			public boolean accept(Packet packet) {
				return true;
			}
		});

		getRoster().addRosterListener(new RosterListener() {

			@Override
			public void presenceChanged(Presence presence) {}

			@Override
			public void entriesUpdated(Collection<String> addresses) {
				System.out.println("Updated:"+addresses);
			}

			@Override
			public void entriesDeleted(Collection<String> addresses) {
				System.out.println("Deleted:"+addresses);
			}

			@Override
			public void entriesAdded(Collection<String> addresses) {
				System.out.println("Added:"+addresses);
			}
		});
	}

	public void sendMessage(String to, String message) throws Exception {
		if (to.contains("~")) {
			for (MultiUserChat muc : chatRooms) {
				if (muc.getRoom().equals(to)) {
					muc.sendMessage(message);
				}
			}
		} else {
			Message aEnviar = new Message(to);
			aEnviar.setBody(message);
			aEnviar.setType(Message.Type.chat);
			aEnviar.setFrom(getUser().split("/")[0]);
			sendPacket(aEnviar);
		}
	}

	public void joinChannel(String channelName, String type, String password) {
		try {
			MultiUserChat muc = new MultiUserChat(this, getChatRoomJID(channelName, type, password, password==null));
			chatRooms.add(muc);
			if (password == null) {
				try {
					muc.join(getUser());
				} catch (NoResponseException e) {}
			} else {
				muc.join(getUser(), password);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getRoomName(String roomName, String type) throws Exception {
		String sha = sha1(roomName);
		sha = sha.replaceAll("[^a-zA-Z0-9_~]", "");
		return type + "~" + sha;
	}

	private String getChatRoomJID(String roomName, String type, String password, boolean isPublic) throws Exception {
		if (!isPublic)
			return getRoomName(roomName, type) + "@sec.pvp.net";

		if (password == null || password.isEmpty())
			return getRoomName(roomName, type) + "@lvl.pvp.net";

		return getRoomName(roomName, type) + "@conference.pvp.net";
	}

	/**
	 * Calculates the SHA1 Digest of a given input.
	 *
	 * @param input the input
	 * @return the string
	 * @throws NoSuchAlgorithmException the no such algorithm exception
	 */
	public String sha1(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	public void addListener(XmppListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

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

}
