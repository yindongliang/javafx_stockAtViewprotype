package answer.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class IP {

    public static String getLocalIP() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
        byte[] ipAddr = addr.getAddress();
        String ipAddrStr = "";
        for (int i = 0; i < ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }
            ipAddrStr += ipAddr[i] & 0xFF;
        }

        return ipAddrStr;
    }
    
     public static boolean isNetworkAvailable() {
        URL url = null;
        boolean flag = true;
        try {
            url = new URL("http://baidu.com");
            try {
                InputStream in = url.openStream();
                in.close();
                
            } catch (IOException e) {
                flag = false;
            }
        } catch (MalformedURLException e) {
             flag = false;
            e.printStackTrace();
        }
       return flag;
    }
}
