/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package answer.logic;

import answer.bean.dto.Alldata;
import answer.bean.dto.Kdj;
import answer.bean.dto.Macd;
import java.util.*;
import org.apache.log4j.Logger;

/**
 *
 * @author doyin
 */
public class Analysis {

    /*
     *
     *
     */
    private static Logger log = Logger.getLogger(Analysis.class);

    public static boolean isUpCross(Kdj c, Kdj onedaybefore) {
        if (c.getJ().doubleValue() > c.getK().doubleValue()
                && c.getK().doubleValue() > c.getD().doubleValue()
                && onedaybefore.getJ().doubleValue() < onedaybefore.getK().doubleValue()
                && onedaybefore.getK().doubleValue() < onedaybefore.getD().doubleValue()) {// 上叉
            return true;
        }
        return false;
    }

    public static boolean isApprochCross(Kdj c) {
        if (((c.getD().doubleValue() - c.getK().doubleValue()) <= 1.0
                && (c.getD().doubleValue() - c.getK().doubleValue()) >= -1.0)
                && ((c.getD().doubleValue() - c.getJ().doubleValue()) < 2.0
                && (c.getD().doubleValue() - c.getJ().doubleValue()) > -2.0)) {

            return true;


        }
        return false;

    }

    public static boolean isUpWave(Kdj c, Kdj onedaybefore, Kdj twodaybefore) {


        if ((c.getJ().doubleValue() > onedaybefore.getJ().doubleValue() + 3
                && twodaybefore.getJ().doubleValue() > onedaybefore.getJ().doubleValue() + 3//
                )) {

            log.info(c.getStock_cd() + " 向上的波");
            return true;
        }

        return false;
    }

    public static boolean isOver(Kdj c) {


        if (c.getJ().doubleValue() > c.getK().doubleValue()) {
            log.info(c.getStock_cd() + " is above");
            return true;
        }
        log.info(c.getStock_cd() + " is under");

        return false;
    }

    public static double caculateAveNday(List<Alldata> alldatalist, int n) {
        double pricemount = 0.0;
        for (int t = 0; t < n; t++) {
            Alldata alldata = alldatalist.get(t);
            pricemount = pricemount + alldata.getPresent_price().doubleValue();

        }
        double average = pricemount / n;
        return average;
    }

    public static String getkdjtype(Kdj kdjtoday, Kdj kdjyesterday, Kdj kdjthedaybeforeyesterday) {
        String kdjtype = "";

        if (Analysis.isUpCross(kdjtoday, kdjyesterday)) {
            kdjtype = "upc";
        } else if (Analysis.isUpWave(kdjtoday, kdjyesterday, kdjthedaybeforeyesterday)) {
            if (Analysis.isOver(kdjtoday)) {

                if (kdjtoday.getJ().doubleValue() > 100) {
                    kdjtype = "ovupwtp";
                } else {
                    kdjtype = "ovupw";
                }
            } else {

                if (kdjtoday.getJ().doubleValue() < 0) {
                    kdjtype = "udupwbt";
                } else {
                    kdjtype = "udupw";
                }
            }
        } else if (Analysis.isApprochCross(kdjtoday) && !Analysis.isOver(kdjtoday)) {

            kdjtype = "uppc";
        }
        return kdjtype;
    }

    public static String getMacdtype(Macd macdtoday, Macd macdyesterday) {
        String macdtype = "";
        if (macdtoday.getBar().doubleValue() > macdyesterday.getBar().doubleValue()) {

            if (macdtoday.getBar().doubleValue() < 0) {
                if (macdtoday.getDif().doubleValue() < 0
                        && macdtoday.getDea().doubleValue() > 0) {// 以0为分界 分割了 dif dea
                    macdtype = "p1";
                } else if (macdtoday.getDif().doubleValue() < 0
                        && macdtoday.getDea().doubleValue() < 0) {// dif 小于0 dea 小于0
                    macdtype = "p2";
                } else if (macdtoday.getDif().doubleValue() > 0
                        && macdtoday.getDea().doubleValue() > 0) {// dif 大于0 dea 大于0
                    macdtype = "p8";
                }
            } else if (macdtoday.getBar().doubleValue() > 0 && macdyesterday.getBar().doubleValue() < 0) {
                if (macdtoday.getDif().doubleValue() < 0) {
                    macdtype = "p3";
                }
            } else if (macdtoday.getBar().doubleValue() > 0 && macdyesterday.getBar().doubleValue() > 0
                    && macdtoday.getDif().doubleValue() > macdtoday.getDea().doubleValue()
                    && macdtoday.getDif().doubleValue() < 0) {//红柱子 刚刚起步涨
                macdtype = "p4";
            }
        } else if (macdtoday.getBar().doubleValue() < macdyesterday.getBar().doubleValue()) {//柱子今日小于昨日
            if (macdyesterday.getBar().doubleValue() < 0) {//昨日日柱子小于0
                if (macdtoday.getDif().doubleValue() < 0
                        && macdtoday.getDea().doubleValue() > 0) {// dif 小于0 dea大于0
                    macdtype = "p5";
                } else if (macdtoday.getDif().doubleValue() < 0) {// dif 小于0 dea 小于0
                    macdtype = "p6";
                } else if (macdtoday.getDif().doubleValue() > 0) {// dif dea >0
                    macdtype = "p7";
                }
            }
        }
        return macdtype;
    }
    
    public static double maxValue(double ...values){

        double maxv=0;
        for (double v:values){
            maxv=Math.max(maxv, v);
        }
        return maxv;
    }
   
     public static double minValue(double ...values){

        double minv=10000;
        for (double v:values){
            minv=Math.min(minv, v);
        }
        return minv;
    }
     
    
    public static Map<String,Object> mmValue( List<Alldata> listalldata,int t){
         
        double maxv=0;
        double minv=10000;
        
        int idxmax=0;
        int idxmin=0;
       
        for (int i=0;i<t;i++){
            Alldata v= listalldata.get(i);
            double maxvorigin=maxv;
            double minvorigin= minv;
            maxv=Math.max(maxv, v.getPresent_price().doubleValue());
            minv=Math.min(minv, v.getPresent_price().doubleValue());
            if (maxv!=maxvorigin){
                
                idxmax=i;
                
            }
            if (minv!=minvorigin){
                idxmin=i;
            }
            
        }
        Map res = new HashMap<String,Integer>();
        res.put("max", idxmax);
        res.put("min", idxmin);
        
        return res;
    }
    
    public static Map<String,Object> weekAlldataValue( List<Alldata> listalldata,int t){
         
        double maxv=0;
        double minv=10000;
        
        int idxmax=0;
        int idxmin=0;
        
        long deallots=0;
        for (int i=0;i<t;i++){
            Alldata v= listalldata.get(i);
            deallots=deallots+v.getDeal_lots();
            
            double maxvorigin=maxv;
            double minvorigin= minv;
            maxv=Math.max(maxv, v.getTd_highest_price().doubleValue());
            minv=Math.min(minv, v.getTd_lowest_price().doubleValue());
//            if (maxv!=maxvorigin){
//                
//                idxmax=i;
//                
//            }
//            if (minv!=minvorigin){
//                idxmin=i;
//            }
            
        }
        Map res = new HashMap<String,Integer>();
        res.put("max", maxv);
        res.put("min", minv);
        res.put("deallots", deallots);
        
        return res;
    }
    
    
    public static boolean timeWindow(List<Alldata> alldatalist,int[] mmrr,double curprice,int cnt){
         Map<String, Object> mpmm = Analysis.mmValue(alldatalist, cnt);
        int mx = (Integer)mpmm.get("max");
        int mi = (Integer)mpmm.get("min");

        double mxpr = alldatalist.get(mx).getPresent_price().doubleValue();
        double mipr = alldatalist.get(mi).getPresent_price().doubleValue();
        boolean flg=false;
        for (int i =0;i<mmrr.length;i++){
            if (mmrr[i]==mx||mmrr[i]==mi){
                flg=true;
                break;
            }
        }
        if(!flg){
            return false;
        }
        if ((mxpr-mipr)/mipr*100<14){
            return false;
        }
        
//        if (curprice<mxpr){
//            return false;
//        }
        return true;
    }
    
    public static boolean zhongsu(List<Alldata> alldatalist,int[] mmrr,double yesterdayprice){
         Map<String, Object> mpmm = Analysis.mmValue(alldatalist, 22);
        int mx = (Integer)mpmm.get("max");
        int mi = (Integer)mpmm.get("min");

        double mxpr = alldatalist.get(mx).getPresent_price().doubleValue();
        double mipr = alldatalist.get(mi).getPresent_price().doubleValue();
        
       
        if (yesterdayprice<mxpr){
            return true;
        }
        return false;
    }
    
    
    
    
}
