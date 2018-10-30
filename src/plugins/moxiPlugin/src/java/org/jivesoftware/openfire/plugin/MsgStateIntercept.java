package org.jivesoftware.openfire.plugin;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import java.text.MessageFormat;

public class MsgStateIntercept implements MoxiIntercept {
    @Override
    public boolean isCatch(Packet msg) {
        return needSendStateCmd(msg);
    }

    @Override
    public void before(Packet packet, Session session, boolean read) {

    }

    @Override
    public void after(Packet packet, Session session, boolean read) {
        if (packet instanceof Message && needSendStateCmd(packet)) {
            Message source = (Message) packet;
            System.out.println("=== source:\n" + source.toString());
            Message reply = new Message();
            reply.setID(source.getID());
            reply.setTo(session.getAddress());
            reply.setFrom(source.getTo());
            reply.setType(source.getType());
            reply.setThread(source.getThread());
            reply.setBody(genStateBody(source));
            System.out.println("=== reply:\n" + reply.toString());
            session.process(reply);
        }
    }




    protected String genStateBody(Packet param) {
        if (param instanceof  Message) {
            Message msg = (Message) param;
            String context = "";
            if (msg.getType() == Message.Type.error) {
                context = "05" + msg.getID();
            } else {
                context = "03" + msg.getID();
            }

            String body = MessageFormat.format("{0}10{1}{2}{3}{4}"
                , genRandomNum(2)
                , genRandomNum(2)
                , getStrLength(context)
                , context
                , genRandomNum(RandomUtils.nextInt(10)));
            return "{\"type\":\"cmd\",\"data\":\"" + body + "\"}";
        }
        return "";
    }

    static char[] sourceChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    protected String genRandomNum(int length) {
        return RandomStringUtils.random(length, sourceChars);
    }

    protected String getStrLength(String str) {
        int len = str.length();
        String hexStr = Integer.toHexString(len);
        while (hexStr.length() < 4) {
            hexStr = "0" + hexStr;
        }
        return hexStr;
    }


    protected Boolean needSendStateCmd(Packet param) {
        if (param instanceof Message) {
            Message msg = (Message) param;
            String body = msg.getBody();
            if (body == null) {
                return true;
            }
            return !body.startsWith("{\"type\":\"cmd\"");
        } else {
            return false;
        }
    }
}
