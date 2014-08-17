package riotapi.xmpp.com.jdiaz.xmpp;

import org.jivesoftware.smack.packet.Message;

public interface XmppListener {
	public abstract void statusReceived(RiotStatus status);
	public abstract void messageReceived(Message message);
}
