package com.wheel.ctgu.common;

import com.wheel.ctgu.common.constants.ProtocolConstants;
import com.wheel.ctgu.common.exception.ProtocolException;

/**
 * @author jack_yun
 * @version 1.0
 * @description: TODO
 * @date 2022/5/28 19:14
 */
public class URLStrParser {
    /**
     *
     *
     * @param url
     * @return com.wheel.ctgu.common.URL
     * @author long_yun
     * @date 2022/5/29 13:25
     * @describe parse decoded url string, formatted protocol://username:password@host:port/path?k1=v1&k2=v2
     */

    public static URL parseDecodedStr(String url) {
        String protocol = url.substring(0,url.indexOf("//"));
        if(protocol.equals(ProtocolConstants.ZOOKEEPER)||protocol.equals(ProtocolConstants.REDIS)){
            url = url.substring(url.indexOf("//")+1);
            String username = url.substring(0,url.indexOf(":"));
            url = url.substring(url.indexOf(":")+1);
            String password = url.substring(0,url.indexOf("@"));
            url = url.substring(url.indexOf("@")+1);
            String host = url.substring(0,url.indexOf(":"));
            url = url.substring(url.indexOf(":")+1);
            String port = url.substring(0,url.indexOf("/"));
            String path = url.substring(url.indexOf("/")+1);
            URL url1 = new URL(protocol,username,password,host,Integer.valueOf(port),path);
            return url1;
        } else{
            throw new ProtocolException(protocol+"is not in wheel");
        }
    }
}
