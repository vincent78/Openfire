package org.jivesoftware.openfire.plugin.utils;

import org.apache.commons.lang.RandomStringUtils;
import org.jivesoftware.util.cache.Cache;
import org.jivesoftware.util.cache.CacheFactory;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashMap;

public class SMSUtil {

    public static Cache<String,String> checkCodeCache;

    public static String send(String mobile) {
        String checkCode;
        try {
            checkCode = genCheckCode();
            StringBuffer buf = new StringBuffer();
            buf.append("http://service.winic.org:8009/sys_port/gateway/index.asp")
               .append("?")
               .append("id=moxi80021")
               .append("&")
               .append("pwd=x998486")
               .append("&")
               .append("to=").append(mobile)
               .append("&")
               .append("content=").append(URLEncoder.encode(genSMSContent(checkCode), "gb2312"));
            String result = HttpClientUtil.sendHttpGet(buf.toString());
            if (result.startsWith("000/")) {
                if (checkCodeCache == null) {
                    checkCodeCache = CacheFactory.createCache("cache.moxiPluginCache");
                }
                checkCodeCache.put(mobile, checkCode);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    protected static String genSMSContent(String checkCode) {

        return MessageFormat.format("[魔曦IM] 您的验证码为{0},请勿向他人泄露。", checkCode);
    }


    static char[] CHECKCODES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    protected static String genCheckCode() {
        return RandomStringUtils.random(6, CHECKCODES);
    }

}
