package org.jivesoftware.openfire.plugin.handler;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.PacketException;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.handler.IQHandler;
import org.jivesoftware.openfire.plugin.utils.SMSUtil;
import org.jivesoftware.openfire.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

public class IQCheckCodeHander extends IQHandler {

    private static final Logger Log = LoggerFactory.getLogger(IQCheckCodeHander.class);

    public static final String NAMESPACE = "jabber:iq:checkcode";
    private String serverName;
    private final IQHandlerInfo info;

    public IQCheckCodeHander(String moduleName) {
        super(moduleName);
        info = new IQHandlerInfo("checkcode", NAMESPACE);
    }

    @Override
    public void process(Packet packet) throws PacketException {
        super.process(packet);
        System.out.println("IQCheckCodeHander process:" + packet.toXML());
        Element mobileElement = packet.getElement();
        try{
            String mobile = mobileElement.element("query").element("mobileNumber").getTextTrim();
            String result = SMSUtil.send(mobile);
            IQ reply = IQ.createResultIQ((IQ) packet);
            reply.setTo((JID) null);
            Element childElement = ((IQ) packet).getChildElement().createCopy();
            Element resultElement = new DefaultElement("result");
            resultElement.setText(result);
            childElement.add(resultElement);
            reply.setChildElement(childElement);
            if (reply != null) {
                // why is this done here instead of letting the iq handler do it?
                ClientSession session = sessionManager.getSession(packet.getFrom());
                session.process(reply);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(XMPPServer server) {
        super.initialize(server);
        serverName = server.getServerInfo().getXMPPDomain();
        Log.debug("IQCheckCodeHander initialize:" + serverName);
    }

    @Override
    public IQ handleIQ(IQ iq) throws UnauthorizedException {
        return null;
    }

    @Override
    public IQHandlerInfo getInfo() {
        return info;
    }


}
