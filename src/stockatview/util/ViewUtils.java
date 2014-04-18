/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stockatview.util;

import answer.bean.dto.Chosendata;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import answer.logic.StockMonitor;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.client.ClientProtocolException;
import stockatview.StockATView;

/**
 *
 * @author doyin
 */
public class ViewUtils {

    public static String dateFormater(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        String dateStr = sdf.format(date);
        return dateStr;
    }

    public static String timer(long startTime) {
        long longtime = (System.currentTimeMillis() - startTime) / 1000;
        String time = String.format("%02d:%02d:%02d", longtime / 3600, longtime / 60, longtime % 60);
        return time;
    }

    public static boolean isOpening(ObservableList<Chosendata> dataFortable) {

        boolean flag = true;
        String tradedate = "";

        if (dataFortable == null || dataFortable.size() == 0) {
            return false;
        }

        tradedate = ((StockMonitor) StockATView.ac.getBean("StockMonitor")).getCurrentTradedateOfstock(dataFortable.get(0));


        if (tradedate.compareTo(dateFormater("yyyy-MM-dd")) != 0) {
            return false;
        }
        if (dateFormater("HH:mm:ss").compareTo("11:30:00") >= 0
                && dateFormater("HH:mm:ss").compareTo("13:00:00") <= 0) {
            flag = false;
        }

        if (dateFormater("HH:mm:ss").compareTo("09:30:00") < 0
                || dateFormater("HH:mm:ss").compareTo("15:00:00") > 0) {
            flag = false;
        }

        return flag;
    }
}
