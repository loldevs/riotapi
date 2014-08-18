package net.boreeas.xmpp;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

import javax.net.ssl.SSLSocketFactory;

import lombok.Getter;
import net.boreeas.riotapi.Shard;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

public class XmppClient extends XMPPTCPConnection {

	private ArrayList<MultiUserChat> chatRooms;

	private @Getter Shard server;
	private @Getter String user;
	private String pass;

	public XmppClient(Shard server, String user, String pass) {
		super(buildConnectionConfiguration(server));
		this.server = server;
		this.user = user;
		this.pass = pass;
		chatRooms = new ArrayList<MultiUserChat>();
	}

	@Override
	public void connect() throws SmackException, IOException, XMPPException {
		super.connect();
		login(user, "AIR_" + pass, "xiff");

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

	public void joinChannel(String channelName, ChatType type, String password) {
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

	private String getRoomName(String roomName, ChatType type) throws Exception {
		String sha = sha1(roomName);
		return type.getType() + "~" + sha;
	}

	private String getChatRoomJID(String roomName, ChatType type, String password, boolean isPublic) throws Exception {
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


}
