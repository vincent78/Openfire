/*
 * Copyright (C) 2004-2008 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.openfire.plugin;

import java.io.File;
import java.text.MessageFormat;
import java.util.regex.PatternSyntaxException;

import org.jivesoftware.openfire.MessageRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.util.EmailService;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

/**
 * Content filter plugin.
 * 
 * @author Conor Hayes
 */
public class MessageStatePlugin implements Plugin, PacketInterceptor {

    private static final Logger Log = LoggerFactory.getLogger(MessageStatePlugin.class);

    private InterceptorManager interceptorManager;

    public MessageStatePlugin() {
        System.out.println("MessageStatePlugin is instance.");
        interceptorManager = InterceptorManager.getInstance();
    }

 

    public void initializePlugin(PluginManager pManager, File pluginDirectory) {
        System.out.println("messageStatePlugin Directory"+ pluginDirectory.getAbsolutePath());
        interceptorManager.addInterceptor(this);
    }

    @Override
    public void interceptPacket(Packet packet, Session session, boolean b, boolean b1) throws PacketRejectedException {
        if (b1) {
            Message source = (Message)packet;
            Message reply = new Message();
            reply.setID(source.getID());
            reply.setTo(session.getAddress());
            reply.setFrom(source.getTo());
            reply.setType(source.getType());
            reply.setThread(source.getThread());
            reply.setBody(MessageFormat.format("msg:{0} send success!",source.toString()));
            System.out.println(reply.toString());
//            session.process(reply);
        }
    }

    public void destroyPlugin() {
        // unregister with interceptor manager
//        interceptorManager.removeInterceptor(this);
    }


}
