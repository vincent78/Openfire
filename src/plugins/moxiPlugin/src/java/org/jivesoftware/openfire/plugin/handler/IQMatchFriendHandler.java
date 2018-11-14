package org.jivesoftware.openfire.plugin.handler;

import org.dom4j.Element;
import org.jivesoftware.openfire.IQHandlerInfo;
import org.jivesoftware.openfire.PacketException;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.roster.RosterItem;
import org.jivesoftware.openfire.roster.RosterManager;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.cache.Cache;
import org.jivesoftware.util.cache.CacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import java.util.*;

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
        Set<String> userSet = getNeed2Friend(packet);
        System.out.println("the userSet:" + userSet);
        if (userSet != null && !userSet.isEmpty()) {
            addFriends(packet, userSet);
        }
    }


    public Set<String> getNeed2Friend(Packet packet) {
        Set<String> need2FriendSet = Collections.emptySet();
        try {
            Element mobileElement = packet.getElement();
            String mobiles = mobileElement.element("query").element("mobileNumbers").getTextTrim();
            HashSet<String> clientApply2Friend = new HashSet(Arrays.asList(mobiles.split(",")));
            if (mobiles == null || mobiles.length() == 0) {
                replyMsg(packet, "the input mobiles is null");
            } else {
                UserManager userManager = XMPPServer.getInstance().getUserManager();
                Collection<User> users = userManager.getUsers();
                Collection<RosterItem> friendUsers = userManager.getUser(sessionManager.getSession(packet.getFrom())
                                                                                       .getUsername())
                                                                .getRoster()
                                                                .getRosterItems();

                Set<String> userNameSet = new HashSet<>();
                Set<String> friendUserSet = new HashSet<>();
                if (users != null) {
                    for (User user : users) {
                        userNameSet.add(user.getUsername());
                    }
                }

                if (friendUsers != null) {
                    for (RosterItem item : friendUsers) {
                        if (item.getJid().getNode() != null) {
                            friendUserSet.add(item.getJid().getNode());
                        } else {
                            friendUserSet.add(item.getJid().toString());
                        }
                    }
                }

                need2FriendSet = new HashSet<>(userNameSet);
                need2FriendSet.removeAll(friendUserSet);
                need2FriendSet.retainAll(clientApply2Friend);

                need2FriendSet.remove(packet.getFrom().getNode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            replyMsg(packet, e.getMessage());
        }

        return need2FriendSet;
    }

    public void addFriends(Packet packet, Set<String> users) throws PacketException {
        System.out.println("addFriends has invoked.");
        String toDomain = packet.getTo().getDomain();
        if (users != null && !users.isEmpty()) {
            for (String userName : users) {
                addFriend(packet, userName);
            }
        }
    }

    public void addFriend(Packet packet, String toUserName) throws PacketException {
        System.out.println("addFriend:" + toUserName);
        String serverName = XMPPServer.getInstance().getServerInfo().getXMPPDomain();
        String toDomain = packet.getTo().getDomain();
        if ("local".equals(JiveGlobals.getProperty("plugin.subscription.level", "local"))) {
            String fromDomain = packet.getFrom().getDomain();

            if (!toDomain.equals(serverName) || !fromDomain.equals(serverName)) {
                return;
            }
        }

        Presence presence = new Presence();
        presence.setType(Presence.Type.subscribed);
        presence.setTo(toUserName);
        presence.setFrom(packet.getFrom());
        XMPPServer.getInstance().getPresenceRouter().route(presence);


        try {
            String name = toUserName;
            if (name.indexOf("@") <= 0) {
                name = name + "@" + toDomain;
            }
            addRosterItem(packet.getFrom().getNode(), new JID(name));
            addRosterItem(toUserName, packet.getFrom());


            // 缓存的刷新尤其重要，不然，只有重启服务器，你才能看到两个好友之间才是真正的订阅关系的！
            Cache cache[] = CacheFactory.getAllCaches();
            if (cache != null) {
                for (Cache cache2 : cache) {
                    if ("Roster".equals(cache2.getName())) {
                        cache2.clear();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addRosterItem(String userName, JID jid) {

        boolean alreadyFriend = false;
        try {
            Iterator<RosterItem> rosterItemIterator = RosterManager.getRosterItemProvider()
                                                                   .getItems(userName);
            if (rosterItemIterator != null) {
                while (rosterItemIterator.hasNext()) {
                    RosterItem rosterItem = (RosterItem) rosterItemIterator.next();
                    if (jid.getNode().equals(rosterItem.getJid().getNode())) {
                        rosterItem.setSubStatus(RosterItem.SUB_BOTH);
                        RosterManager.getRosterItemProvider().updateItem(userName, rosterItem);
                        alreadyFriend = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (alreadyFriend) {
            return;
        }

        // 获取昵称
        String nickName = jid.getNode();
        try {
            nickName = UserManager.getInstance().getUser(jid.getNode()).getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 添加一个分组为fans的名
        List<String> groups = new ArrayList<>();
        groups.add("fans");
        try {
            RosterItem item = new RosterItem(jid, RosterItem.SUB_BOTH, RosterItem.ASK_NONE, RosterItem.RECV_NONE, nickName, groups);
            RosterManager.getRosterItemProvider().createItem(userName, item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public IQHandlerInfo getInfo() {
        return info;
    }

}
