package org.jivesoftware.openfire.plugin;

import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class MessageStateProcess implements MessageState {
    @Override
    public boolean isState(Message msg) {
        return false;
    }

    @Override
    public void before(Packet packet, Session session, boolean read) {

    }

    @Override
    public void after(Packet packet, Session session, boolean read) {

    }
}
