/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wmain;

import answer.logic.Stock2DB;
import answer.logic.helper.Formater;
import answer.logic.helper.HttpHelper;
import answer.logic.helper.PinYin;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author doyin
 */
public class stockBankuai {

//    static DefaultHttpClient httpclient;
//
//    static {
//        final PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
//        cm.setMaxTotal(200);
//        cm.setDefaultMaxPerRoute(200);
//
//        Thread tr = new Thread() {
//
//            @Override
//            public void run() {
//                while (true) {
//
//                    cm.closeIdleConnections(1000, TimeUnit.MILLISECONDS);
//
//                }
//            }
//        };
//        tr.start();
//        httpclient = new DefaultHttpClient(cm);
//    }
    static public List<String> sendRequest(String requestkey) {
        HttpGet req = null;
        HttpResponse rsp = null;
        DefaultHttpClient httpclient = new DefaultHttpClient();

        req = new HttpGet("http://biz.finance.sina.com.cn/qmx/industry.php?industry=" + requestkey);


        try {
            rsp = httpclient.execute(req);
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
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        String temp = "";
        String[] arr = null;
        temp = datas.substring(datas.indexOf("var qmx_symbols = \""));
        temp = temp.substring(temp.indexOf("\"") + 1, temp.indexOf("\";"));
        arr = temp.split(",");
        return Arrays.asList(arr);

    }

    public static void main(String[] args) throws InterruptedException {
        // TODO Auto-generated method stub
        ApplicationContext ac = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml", "dataAccessContext-local.xml"});
        List<String> list = new ArrayList<String>();
        list.add("水泥");
        list.add("房地产");
        list.add("地产及租赁");
        list.add("建筑材料");
        list.add("轻工制造业");
        list.add("建筑装饰");
        list.add("建筑工程");
        list.add("其他轻工");
        list.add("新能源");
        list.add("运输设备");
        list.add("电子元器件");
        list.add("非汽车交运设备");
        list.add("汽车及配件");
        list.add("运输服务");
        list.add("造纸");
        list.add("公共交通");
        list.add("纸业");
        list.add("计算机软件");
        list.add("非金属制品");
        list.add("汽车及零部件");
        list.add("普通机械");
        list.add("有色金属");
        list.add("餐饮旅游");
        list.add("交通运输");
        list.add("社会服务");
        list.add("包装印刷");
        list.add("出版传媒");
        list.add("交运设施");
        list.add("纺织服装");
        list.add("燃气水务");
        list.add("电力");
        list.add("公用事业");
        list.add("石油化工");
        list.add("化学工业");
        list.add("软件服务");
        list.add("通信设备");
        list.add("专用设备");
        list.add("煤炭开采");
        list.add("钢铁");
        list.add("运输设施");
        list.add("旅游餐饮");
        list.add("综合");
        list.add("医药生物");
        list.add("商业贸易");
        list.add("计算机设备");
        list.add("家用电器");
        list.add("通用设备");
        list.add("煤炭");
        list.add("金属制品");
        list.add("网络服务");
        list.add("石油开采");
        list.add("非银行金融");
        list.add("电气设备");
        list.add("农牧渔");
        list.add("金融");
        list.add("林业");
        list.add("信息设备");
        list.add("航空制造");
        list.add("电信服务");
        list.add("银行");
        list.add("仪器仪表");
        list.add("信息服务");
        list.add("食品饮料");
        ((Stock2DB) ac.getBean("Stock2DB")).deleteBankStocks();
        for (String str : list) {
            String bankuai_cd = PinYin.getPinYinHeadChar(str);
            if (bankuai_cd.contains("yx")) {
                bankuai_cd = bankuai_cd.replace("yx", "yh");
            }
            ((Stock2DB) ac.getBean("Stock2DB")).insertBankFromSina(bankuai_cd, str, sendRequest(bankuai_cd), false);
        }

    }
}
