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

public class IQSetPublicKeyHandler extends IQMoxiBaseHandler {

    private static final Logger Log = LoggerFactory.getLogger(IQSetPublicKeyHandler.class);
    public static final String NAMESPACE = "jabber:iq:setpublickey";
    private final IQHandlerInfo info;


    public IQSetPublicKeyHandler(String moduleName) {
        super(moduleName);
        info = new IQHandlerInfo("setpublickey", NAMESPACE);
    }

    @Override
    public void process(Packet packet) throws PacketException {
        super.process(packet);
        Log.debug("IQSetPublicKeyHandler process:" + packet.toXML());
        Element publicKeyElement = packet.getElement();
        try{
            String result;
            String publickey = publicKeyElement.element("query").element("publickey").getTextTrim();
            UserManager userManager = XMPPServer.getInstance().getUserManager();
            ClientSession session = sessionManager.getSession(packet.getFrom());
            User user = userManager.getUser(session.getUsername());
            if (user != null) {
                user.getProperties().put("moxiPublicKey", publickey);
                result = "save publicKey success";
            } else {
                result = "save publicKey failure";
            }


            replyMsg(packet,result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    public IQHandlerInfo getInfo() {
        return info;
    }
}
