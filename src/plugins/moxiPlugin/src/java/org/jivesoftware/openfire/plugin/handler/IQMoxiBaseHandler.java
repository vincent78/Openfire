package org.jivesoftware.openfire.plugin.handler;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

public abstract class IQMoxiBaseHandler extends IQHandler {



    public IQMoxiBaseHandler(String moduleName) {
        super(moduleName);
    }

    protected void replyMsg(Packet packet, String result) {
        IQ reply = IQ.createResultIQ((IQ) packet);
        ClientSession session = sessionManager.getSession(packet.getFrom());
        reply.setTo((JID) null);
        Element childElement = ((IQ) packet).getChildElement().createCopy();
        Element resultElement = new DefaultElement("result");
        resultElement.setText(result);
        childElement.add(resultElement);
        reply.setChildElement(childElement);
        if (reply != null && session != null) {
            // why is this done here instead of letting the iq handler do it?
//            session.process(reply);
            XMPPServer.getInstance().getIQRouter().route(reply);
        }
    }



    @Override
    public IQ handleIQ(IQ iq) throws UnauthorizedException {
        return null;
    }

    @Override
    public void initialize(XMPPServer server) {
        super.initialize(server);
    }

}
