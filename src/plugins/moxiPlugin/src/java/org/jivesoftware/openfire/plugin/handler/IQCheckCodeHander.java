package org.jivesoftware.openfire.plugin.handler;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.PacketException;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.plugin.utils.SMSUtil;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.util.cache.Cache;
import org.jivesoftware.util.cache.CacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Packet;

public class IQCheckCodeHander extends IQMoxiBaseHandler {

    private static final Logger Log = LoggerFactory.getLogger(IQCheckCodeHander.class);

    public static final String NAMESPACE = "jabber:iq:checkcode";
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
            if (result.startsWith("000/")) {
                changePwdWhenLogin(packet);
            }

            replyMsg(packet,result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changePwdWhenLogin(Packet packet) {
        Element mobileElement = packet.getElement();
        String mobile = mobileElement.element("query").element("mobileNumber").getTextTrim();
        Boolean isLogining = false;
        for (Element e :mobileElement.element("query").elements()) {
            if ("type".equalsIgnoreCase(e.getName()) && "login".equalsIgnoreCase(e.getTextTrim())) {
                isLogining = true;
            }
        }

        if (isLogining) {
            try {
                User user = XMPPServer.getInstance().getUserManager().getUser(mobile);
                if (user != null) {
                    Cache<String,String> cache = CacheFactory.createCache("cache.moxiPluginCache");
                    if (cache != null) {
                        String code = cache.get(mobile);
                        if (code != null && code.length() > 0) {
                            user.setPassword(code);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public IQHandlerInfo getInfo() {
        return info;
    }


}
