package answer.logic.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import answer.bean.dto.Alldata;
import java.math.BigDecimal;

public class Formater {
    /*
     * 0:股票代码 1:今日开盘价 2:昨日收盘价 3:当前价格 4:今日最高
     *
     *
     */

    public static List<String> str2List(String input) {

        int start = input.indexOf("\"");

        int end = input.lastIndexOf("\"");

        int mid = input.indexOf("=");

        String body = input.substring(start - 1, end);

        if (body.length() == 2) {
            return null;
        }
        String stock_name = body.substring(2, body.indexOf(","));

        body = body.substring(body.indexOf(",") + 1);
        String code = input.substring(mid - 6, mid);

        String[] dataArr = body.split(",");

        if (dataArr[30].startsWith("15")) {

            
            dataArr[30] = "15:00:00";
            

        }
        
        if (dataArr.length > 2) {

            if (dataArr[1].length() >= 7) {
                code = "si" + code;
            }
        }
        List<String> ls = new ArrayList<String>();

        ls.add(code);

        ls.addAll(Arrays.asList(dataArr));
        if (ls.size()>32){
            for (int b=32 ;b<ls.size();b++){
                ls.remove(b);

            }
        }
        ls.add(stock_name);
         
        return ls;
    }

    public static Alldata editCriteria(List<String> stock_detail) {
        Alldata ad = new Alldata();
        ad.setStock_cd(stock_detail.get(0));
        // record_date
        ad.setRecord_date(stock_detail.get(30));
        // record_time
        ad.setRecord_time(stock_detail.get(31));
        return ad;
    }

    public static Alldata editCriteriaForules(List<String> stock_detail, int limit_rec) {
        Alldata ad = new Alldata();
        ad.setStock_cd(stock_detail.get(0));
        if (stock_detail.size() > 31) {
            // record_date

            ad.setRecord_date(stock_detail.get(30));
            // record_time
            ad.setRecord_time(stock_detail.get(31));
        }

        ad.setLimit_rec(limit_rec);
        return ad;
    }

    public static String dateFormater(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        String dateStr = sdf.format(date);
        return dateStr;
    }

    public static String stringdateChange(String org) {
        StringBuilder sb = new StringBuilder();
        sb.append(org.substring(0, 3));
        sb.append("-");
        sb.append(org.substring(4, 5));
        sb.append("-");
        sb.append(org.substring(6));

        return sb.toString();
    }
    
     public static Alldata changeList2bean(List<String> stock_detail) {
        Alldata ad = new Alldata();
        ad.setStock_cd(stock_detail.get(0));
        ad.setTd_open_price(stock_detail.get(1));
        ad.setYt_close_price(new BigDecimal(stock_detail.get(2)));
        
        ad.setPresent_price(new BigDecimal(stock_detail.get(3)));
        
        ad.setTd_highest_price(new BigDecimal(stock_detail.get(4)));
        ad.setTd_lowest_price(new BigDecimal(stock_detail.get(5)));
        ad.setDeal_lots(Long.parseLong(stock_detail.get(8)));
        
        // record_date
        ad.setRecord_date(stock_detail.get(30));
        // record_time
        ad.setRecord_time(stock_detail.get(31));
        return ad;
    }
    
    
}
