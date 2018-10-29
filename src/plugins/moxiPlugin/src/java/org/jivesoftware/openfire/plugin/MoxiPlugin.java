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

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import java.io.File;
import java.text.MessageFormat;

/**
 * Content filter plugin.
 * 
 * @author Conor Hayes
 */
public class MoxiPlugin implements Plugin, PacketInterceptor {

    private static final Logger Log = LoggerFactory.getLogger(MoxiPlugin.class);

    private InterceptorManager interceptorManager;

    public MoxiPlugin() {
        System.out.println("MoxiPlugin is instance.");
        interceptorManager = InterceptorManager.getInstance();
    }

 

    public void initializePlugin(PluginManager pManager, File pluginDirectory) {
        System.out.println("MoxiPlugin Directory"+ pluginDirectory.getAbsolutePath());
        interceptorManager.addInterceptor(this);
    }

    @Override
    public void interceptPacket(Packet packet, Session session, boolean b, boolean b1) throws PacketRejectedException {
        if (b1 && packet instanceof  Message && !isStateMsg((Message) packet)) {
            Message source = (Message)packet;
            System.out.println("===" + source.getBody());
            Message reply = new Message();
            reply.setID(source.getID());
            reply.setTo(session.getAddress());
            reply.setFrom(source.getTo());
            reply.setType(source.getType());
            reply.setThread(source.getThread());
            reply.setBody(genStateBody(source));
            System.out.println(reply.toString());
//            session.process(reply);
        }
    }

    public void destroyPlugin() {
        // unregister with interceptor manager
//        interceptorManager.removeInterceptor(this);
    }


    protected Boolean isStateMsg(Message msg) {
        String body = msg.getBody();
        return body.startsWith("{\"type\":\"cmd\"");
    }
    protected String genStateBody(Message msg) {

        String body = MessageFormat.format("{0}10{1}{2}{3}{4}"
            ,genRandomNum(2)
            ,genRandomNum(2)
            ,getStrLength(msg.getID())
            ,msg.getID()
            ,genRandomNum(RandomUtils.nextInt(10)));
        return MessageFormat.format("{\"type\":\"cmd\",\"data\":\"{0}\"",body);
    }

    static char[] sourceChars = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    protected String genRandomNum(int length) {
        return RandomStringUtils.random(length,sourceChars);
    }

    protected String getStrLength(String str) {
        int len = str.length();
        String hexStr = Integer.toHexString(len);
        while(hexStr.length() < 4) {
            hexStr = "0"+hexStr;
        }
        return hexStr;
    }

    public static void main(String[] args) {

    }
}
