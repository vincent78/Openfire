package org.jivesoftware.openfire.plugin.handler;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.PacketException;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.xmpp.packet.Packet;

import java.util.List;
import java.util.Map;


public class IQModifyUserHandler extends IQMoxiBaseHandler {

    public static final String NAMESPACE = "jabber:iq:modifyUser";
    protected final IQHandlerInfo info;

    public IQModifyUserHandler(String moduleName) {
        super(moduleName);
        info = new IQHandlerInfo("modifyUser", NAMESPACE);
    }

    @Override
    public void process(Packet packet) throws PacketException {
        super.process(packet);
        UserManager manager = XMPPServer.getInstance().getUserManager();
        try {
            User user = manager.getUser(packet.getFrom().getNode());
            if (user != null) {
                List<Element> elements = packet.getElement().element("query").elements();
                Map<String,String> prop = user.getProperties();
                for(Element item : elements) {
                    prop.put(item.getName(),item.getText());
                }

                replyMsg(packet,"success.");

            }
        } catch (Exception e) {
            e.printStackTrace();
            replyMsg(packet,e.getMessage());
        }

    }

    @Override
    public IQHandlerInfo getInfo() {
        return info;
    }
}
