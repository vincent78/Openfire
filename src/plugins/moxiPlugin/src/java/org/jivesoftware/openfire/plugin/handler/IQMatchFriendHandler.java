package org.jivesoftware.openfire.plugin.handler;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.PacketException;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Packet;

import java.util.Collection;

public class IQMatchFriendHandler extends IQMoxiBaseHandler {

    private static final Logger Log = LoggerFactory.getLogger(IQCheckCodeHander.class);

    public static final String NAMESPACE = "jabber:iq:matchfriend";
    protected final IQHandlerInfo info;

    public IQMatchFriendHandler(String moduleName) {
        super(moduleName);
        info = new IQHandlerInfo("matchfriend", NAMESPACE);
    }

    @Override
    public void process(Packet packet) throws PacketException {
        super.process(packet);
        System.out.println("IQMatchFriendHandler process:" + packet.toXML());
        try {
            Element mobileElement = packet.getElement();
            String mobiles = mobileElement.element("query").element("mobileNumbers").getTextTrim();;
            if (mobiles == null || mobiles.length() == 0) {
                replyMsg(packet,"the input mobiles is null");
            } else {
                UserManager userManager = XMPPServer.getInstance().getUserManager();
                Collection<User> users = userManager.getUsers();
                if (users != null) {
                    for (User user :users) {
                        System.out.println("the user is :"+ user.getName());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @Override
    public IQHandlerInfo getInfo() {
        return info;
    }

}
