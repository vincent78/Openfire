package org.jivesoftware.openfire.plugin.handler;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.PacketException;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Packet;

public class IQGetPublicKeyHandler extends IQMoxiBaseHandler {

    private static final Logger Log = LoggerFactory.getLogger(IQSetPublicKeyHandler.class);
    public static final String NAMESPACE = "jabber:iq:getpublickey";
    private final IQHandlerInfo info;


    public IQGetPublicKeyHandler(String moduleName) {
        super(moduleName);
        info = new IQHandlerInfo("getpublickey", NAMESPACE);
    }

    @Override
    public void process(Packet packet) throws PacketException {
        super.process(packet);
        Log.debug("IQGetPublicKeyHandler process:" + packet.toXML());
        try{
            Element clientElement = packet.getElement();
            String userName = clientElement.element("query").element("userName").getTextTrim();
            String publicKey;
            UserManager userManager = XMPPServer.getInstance().getUserManager();
            User user = userManager.getUser(userName);
            if (user != null) {
                publicKey = user.getProperties().get("moxiPublicKey");
                if (publicKey == null) {
                    publicKey = "";
                }
            } else {
                publicKey = "";
            }
            replyMsg(packet,publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public IQHandlerInfo getInfo() {
        return info;
    }
}
