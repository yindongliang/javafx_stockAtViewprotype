package answer.logic.helper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
public class HttpHelper {

    static DefaultHttpClient httpclient;
    static String[] dataserver = null;
    static HttpHost target = null;

    static {
        final PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(200);

        Thread tr = new Thread() {

            @Override
            public void run() {
                while (true) {
//                    try {
//                        sleep(30*1000);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                    cm.closeIdleConnections(100, TimeUnit.MILLISECONDS);

                }
            }
        };
        tr.start();
        httpclient = new DefaultHttpClient(cm);
    }

    public List<String> sendRequest(String requestkey, PropertiesConfiguration stocksProperties) {
        HttpGet req = null;
        HttpResponse rsp = null;
        if (dataserver == null) {

            dataserver = stocksProperties.getStringArray("dataserver");
        }

        // request server
        try {

            if (target == null) {

                target = new HttpHost(dataserver[0], Integer.parseInt(dataserver[1]), dataserver[2]);
            }

//	        HttpHost proxy=null;
            // configuration setting
            // set the proxy server
//			if (IP.getLocalIP().startsWith("10")) {
//				String[] proxyset=stocksProperties.getStringArray("proxy.setting");
//				if(proxyset!=null && proxyset.length==3){
//					
//					proxy = new HttpHost(proxyset[0], Integer.parseInt(proxyset[1]), proxyset[2]);
//				}
//				httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//			}
            if (requestkey.startsWith("60")) {
                req = new HttpGet("/list=sh" + requestkey);
            } else if (requestkey.startsWith("30") || requestkey.startsWith("00")) {
                req = new HttpGet("/list=sz" + requestkey);
            } else if (requestkey.equals(stocksProperties.getString("shindice_code"))) {
                req = new HttpGet("/list=sh" + requestkey.substring(2));
            }

//            if (stocksProperties.getList("stocks.sh").contains(requestkey)) {
//
//                req = new HttpGet("/list=sh" + requestkey);
//
//            } else if (requestkey.equals(stocksProperties.getString("shindice_code"))) {
//
//                req = new HttpGet("/list=sh" + requestkey.substring(2));
//
//            } else {
//
//                req = new HttpGet("/list=sz" + requestkey);
//
//            }
            try {
                rsp = httpclient.execute(target, req);
            } catch (IOException ex) {
                Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, null, ex);
            }

            String datas = "";
            try {
                datas = EntityUtils.toString(rsp.getEntity());
            } catch (IOException ex) {
                Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, null, ex);
            }

            return Formater.str2List(datas);

        } finally {
            //httpclient.getConnectionManager().shutdown();
        }
    }
}
