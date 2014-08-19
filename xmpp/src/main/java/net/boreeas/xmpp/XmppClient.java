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

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

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

	private HashMap<String, MultiUserChat> chatRooms;

	private @Getter Shard server;
	private @Getter String user;
	private String pass;

	public XmppClient(Shard server, String user, String pass) {
		super(buildConnectionConfiguration(server));
		this.server = server;
		this.user = user;
		this.pass = pass;
		chatRooms = new HashMap<String, MultiUserChat>();
	}

	@Override
	public void connect() throws SmackException, IOException, XMPPException {
		super.connect();
		login(user, "AIR_" + pass, "xiff");
	}

	private static ConnectionConfiguration buildConnectionConfiguration(Shard shard) {
		ConnectionConfiguration connConf = new ConnectionConfiguration(shard.chatUrl, Shard.JABBER_PORT, "pvp.net");
		connConf.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		connConf.setSocketFactory(SSLSocketFactory.getDefault());
		return connConf;
	}
	
	public Set<MultiUserChat> getJoinedRooms() {
		Set<MultiUserChat> rooms = new HashSet<MultiUserChat>();
		for (Entry<String, MultiUserChat> roomEntry : chatRooms.entrySet()) {
			rooms.add(roomEntry.getValue());
		}
		return rooms;
	}
	
	public void sendToUser(String to, String message) throws Exception {
		Message packet = new Message(to);
		packet.setBody(message);
		packet.setType(Message.Type.chat);
		packet.setFrom(getUser().split("/")[0]);
		sendPacket(packet);
	}
	
	public void sendToChannel(String roomName, String message) throws Exception {
		chatRooms.get(roomName).sendMessage(message);
	}

	public void joinChannel(String channelName, ChatType type, String password) {
		try {
			MultiUserChat room = new MultiUserChat(this, getChatRoomJID(channelName, type, password, password==null));
			chatRooms.put(channelName, room);
			if (password == null) {
				try {
					room.join(getUser());
				} catch (NoResponseException e) {}
			} else {
				room.join(getUser(), password);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getRoomName(String roomName, ChatType type) throws Exception {
		String sha = sha1(roomName);
		return type.type + "~" + sha;
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
