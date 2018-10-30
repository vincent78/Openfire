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
import java.util.ArrayList;
import java.util.List;

/**
 * Content filter plugin.
 *
 * @author Conor Hayes
 */
public class MoxiPlugin implements Plugin, PacketInterceptor {

    private static final Logger Log = LoggerFactory.getLogger(MoxiPlugin.class);

    private InterceptorManager interceptorManager;

    private List<MoxiIntercept> intercepts;

    public MoxiPlugin() {
        System.out.println("MoxiPlugin is instance.");
        interceptorManager = InterceptorManager.getInstance();
        intercepts = new ArrayList<>();

        intercepts.add(new MsgStateIntercept());
    }


    public void initializePlugin(PluginManager pManager, File pluginDirectory) {
        System.out.println("MoxiPlugin Directory" + pluginDirectory.getAbsolutePath());
        interceptorManager.addInterceptor(this);
    }

    @Override
    public void interceptPacket(Packet packet, Session session, boolean b, boolean b1) throws PacketRejectedException {
        for(MoxiIntercept intercept : intercepts) {
            if (intercept.isCatch(packet)) {
                if (b1) {
                    intercept.after(packet, session, b);
                } else {
                    intercept.before(packet, session, b);
                }
            }
        }
    }

    public void destroyPlugin() {
        // unregister with interceptor manager
//        interceptorManager.removeInterceptor(this);
    }
}
