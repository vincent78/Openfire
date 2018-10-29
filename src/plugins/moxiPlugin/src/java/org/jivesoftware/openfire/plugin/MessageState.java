package org.jivesoftware.openfire.plugin;

import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public interface MessageState {

    boolean isState(Message msg);

    void before(Packet packet, Session session, boolean read);

    void after(Packet packet, Session session, boolean read);
}
