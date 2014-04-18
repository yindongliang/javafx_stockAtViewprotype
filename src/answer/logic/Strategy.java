/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package answer.logic;

import answer.bean.dto.*;
import answer.exception.Axexception;
import answer.exception.Axinfo;
import answer.logic.helper.Formater;
import answer.logic.helper.HttpHelper;
import answer.logic.helper.LogicHelper;
import answer.logic.helper.PopertiesHelper;
import answer.util.Caculator;
import answer.util.Canlendar;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Level;
import jp.terasoluna.fw.dao.QueryDAO;
import jp.terasoluna.fw.dao.UpdateDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.xvolks.jnative.exceptions.NativeException;
import stockatview.util.ComparatorAlldata;

/**
 *
 * @author doyin
 */
@Service(value = "Strategy")
public class Strategy {

    @Autowired
    protected HttpHelper httpHelper = null;
    @Autowired
    protected UpdateDAO updateDAO = null;
    @Autowired
    protected QueryDAO queryDAO = null;
    @Autowired
    protected StockBuyer stockBuyer = null;
    @Autowired
    protected PopertiesHelper popertiesHelper = null;
    private static Logger log = Logger.getLogger(Strategy.class);

    public void setNodeofkdj(String dateend, boolean onedayonly) {

        List<String> ls_shsz = popertiesHelper.getStockCds();
        List<String> stock_detail = null;
        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {


            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("stock_cd", ls_shsz.get(j));
//            condition.put("record_date_from", "2012-06-25");
            condition.put("record_date_to", dateend);
            condition.put("orderby", "desc");
            if (onedayonly) {
                condition.put("limit", 9);
            }
            List<Alldata> adls = queryDAO.executeForObjectList("download.searchdownloadResult", condition);
            ComparatorAlldata comparator = new ComparatorAlldata();


            Collections.sort(adls, comparator);

            final int intev = 9;

            for (int i = 0; i <= adls.size() - intev; i++) {
                List<Alldata> ninedaydata = null;

                ninedaydata = adls.subList(i, intev + i);
                getKeyValueOfkdj(ninedaydata,
                        ninedaydata.get(ninedaydata.size() - 1).getStock_cd(), false);

            }


        }

    }

    private Map<String, Object> getKeyValueOfkdj(List<Alldata> ninedaydata, String stock_cd, boolean forrealtime) {

        double totm = 0;
        double maxdeallot = -100;
        double mindeallot = 999999999;
        double highestprice = -500;
        double lowestprice = 500;

        double totprice = 0;
        // TODO
        Alldata adc = ninedaydata.get(ninedaydata.size() - 1);

        for (int k = 0; k < ninedaydata.size(); k++) {
            Alldata ad = ninedaydata.get(k);
            double deallots = ad.getDeal_lots();
            double hp = ad.getTd_highest_price().doubleValue();
            double lp = ad.getTd_lowest_price().doubleValue();

            totm = totm + deallots;
            totprice = totprice + ad.getPresent_price().doubleValue();

            if (deallots > maxdeallot) {
                maxdeallot = deallots;
            }
            if (deallots < mindeallot) {
                mindeallot = deallots;
            }
            if (hp > highestprice) {
                highestprice = hp;
            }
            if (lp < lowestprice) {
                lowestprice = lp;
            }


        }

        Map<String, Object> condition = new HashMap<String, Object>();
        String recorddate = ninedaydata.get(ninedaydata.size() - 2).getRecord_date();
        condition.put("stock_cd", stock_cd);
        condition.put("record_date", recorddate);
        Kdj kdj = queryDAO.executeForObject("kdj.checkKdjInsert", condition, Kdj.class);
        double kbf = 0;
        double dbf = 0;
        if (kdj == null) {
            Map<String, Object> mp = new HashMap<String, Object>();
            mp.put("stock_cd", stock_cd);
            mp.put("record_date", recorddate);
            mp.put("k", 50);
            mp.put("d", 50);
            mp.put("j", 50);
            updateDAO.execute("kdj.insertKdj", mp);
            kbf = 50;
            dbf = 50;
        } else {
            kbf = kdj.getK().doubleValue();
            dbf = kdj.getD().doubleValue();
        }
        Map<String, Object> mp = new HashMap<String, Object>();
        mp.put("stock_cd", stock_cd);
        mp.put("record_date", adc.getRecord_date());

        kdj = queryDAO.executeForObject("kdj.checkKdjInsert", mp, Kdj.class);

        if (kdj == null) {

            double rsv = Caculator.keepRound((adc.getPresent_price().doubleValue() - lowestprice) / (highestprice - lowestprice) * 100, 2);
            double k = Caculator.keepRound((2.0 / 3.0) * kbf + (1.0 / 3.0) * rsv, 2);
            double d = Caculator.keepRound((2.0 / 3.0) * dbf + (1.0 / 3.0) * k, 2);
            double j = Caculator.keepRound(3 * k - 2 * d, 2);
            mp.put("k", k);
            mp.put("d", d);
            mp.put("j", j);
            if (forrealtime) {
//                Map<String, Object> mpres = new HashMap<String, Object>();
//                mpres.put("k", k);
//                mpres.put("d", d);
//                mpres.put("j", j);

                return mp;
            } else {
                updateDAO.execute("kdj.insertKdj", mp);
                log.info(stock_cd + " - " + adc.getRecord_date() + "has been inserted");
            }

        } else {
            log.info(stock_cd + " - " + adc.getRecord_date() + "already existing");
        }
        return null;

    }

    public void setNodeOfMacd(String dateend, boolean onedayonly) {

        List<String> ls_shsz = popertiesHelper.getStockCds();

        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {


            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("stock_cd", ls_shsz.get(j));

            condition.put("record_date_to", dateend);
            condition.put("orderby", "desc");
            if (onedayonly) {
                condition.put("limit", 2);
            }
            List<Alldata> adls = queryDAO.executeForObjectList("download.searchdownloadResult", condition);
            ComparatorAlldata comparator = new ComparatorAlldata();
            // 升序排列
            Collections.sort(adls, comparator);

            final int intev = 2;

            for (int i = 0; i <= adls.size() - intev; i++) {
                List<Alldata> intevdata = null;

                intevdata = adls.subList(i, intev + i);
                getKeyValueOfMacd(intevdata,
                        intevdata.get(intevdata.size() - 1).getStock_cd(), false);
            }
        }

    }

    public void generateBankuaiDataToFile(String bankuai_cd) {


        List<Bankuai> bankuais = queryDAO.executeForObjectList("bankuaiDetail.selectDatabankuai", null);
        for (Bankuai bk : bankuais) {
            Map<String, Object> mp = new HashMap<String, Object>();
            mp.put("bankuai_cd", bk.getBankuai_cd());
            List<BankuaiDetail> bankuaiDetaills = queryDAO.executeForObjectList("bankuaiDetail.selectAllCond", mp);

            StringBuilder sb = new StringBuilder();
            try {
                File f = new File("D:\\Y_private\\stock\\bankuai_data\\" + bk.getBankuai_cd() + ".txt");
                if (!f.exists()) {
                    f.createNewFile();//不存在则创建
                }
                BufferedWriter output = new BufferedWriter(new FileWriter(f));
                for (BankuaiDetail bkdetail : bankuaiDetaills) {
                    sb.append(bkdetail.getBankuai_cd());
                    sb.append(",");
                    sb.append(bkdetail.getRecord_date().replace("-", ""));
                    sb.append(",");
                    sb.append(bkdetail.getAv_over_cnt().doubleValue());
                    sb.append(",");
                    sb.append(bkdetail.getAv_over_cnt().doubleValue());
                    sb.append(",");
                    sb.append(bkdetail.getAv_over_cnt().doubleValue());
                    sb.append(",");
                    sb.append(bkdetail.getAv_over_cnt().doubleValue());
                    sb.append(",");
                    sb.append(bkdetail.getDeal_lots().doubleValue());
                    sb.append("\r\n");

                }
                output.write(sb.toString());
                output.close();
                log.info("file writing is finished");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        
        
    }

    public void generateBankuaiData(String dateend, boolean onedayonly) {
        List<Bankuai> bankuais = queryDAO.executeForObjectList("bankuaiDetail.selectDatabankuai", null);
        for (Bankuai bk : bankuais) {
            Map<String, Object> mp = new HashMap<String, Object>();
            mp.put("bankuai_cd", "%" + bk.getBankuai_cd() + "%");
            List<BankuaiStock> bankuaistocks = queryDAO.executeForObjectList("bankuaiDetail.selectDatabankuaistocktarget", mp);

            Map<String, Object> con = new HashMap<String, Object>();
            con.put("orderby", "desc");
//            con.put("limit", 20);
            // 目标日期
            List<String> datelist;
            if (onedayonly) {
                datelist = new ArrayList<String>();
                datelist.add(dateend);
            } else {
                datelist = queryDAO.executeForObjectList("bankuaiDetail.selectDataSpeci", con);
            }
            for (String date : datelist) {

                int cntstocks = 0;
                double cntover = 0;
                long tottodaybankdeallots = 0;
                for (BankuaiStock bkstock : bankuaistocks) {

                    String stock_cd = bkstock.getStock_cd();

                    Map<String, Object> condition = new HashMap<String, Object>();
                    condition.put("stock_cd", stock_cd);
                    condition.put("record_date_to", date);
                    condition.put("orderby", "desc");

                    condition.put("limit", 60);

                    List<Alldata> adls = queryDAO.executeForObjectList("download.searchdownloadResult", condition);
                    if (adls.size() > 55 && adls.get(0).getRecord_date().equals(date)) {
                        double today_ave_5 = Analysis.caculateAveNday(adls.subList(0, +5), 5);
                        double today_ave_13 = Analysis.caculateAveNday(adls.subList(0, 0 + 13), 13);
                        double today_ave_21 = Analysis.caculateAveNday(adls.subList(0, 0 + 21), 21);
                        double today_ave_34 = Analysis.caculateAveNday(adls.subList(0, 0 + 34), 34);
                        double today_ave_55 = Analysis.caculateAveNday(adls.subList(0, 0 + 55), 55);
                        double today_price = adls.get(0).getPresent_price().doubleValue();
                        double cnt = 0;
                        if (today_price >= today_ave_5) {
                            cnt++;

                        }
                        if (today_price >= today_ave_13) {
                            cnt++;
                        }
                        if (today_price >= today_ave_21) {
                            cnt++;
                        }
                        if (today_price >= today_ave_34) {
                            cnt++;
                        }
                        if (today_price >= today_ave_55) {
                            cnt++;
                        }

                        tottodaybankdeallots = tottodaybankdeallots + adls.get(0).getDeal_lots();
                        cntover = cntover + cnt;
                        cntstocks++;


                    }

                }
                if (cntstocks == 0) {
                    continue;
                }
                // 
                Map<String, Object> mpdata = new HashMap<String, Object>();
                mpdata.put("bankuai_cd", bk.getBankuai_cd());
                mpdata.put("record_date", date);
                mpdata.put("deal_lots", tottodaybankdeallots / cntstocks);
                mpdata.put("over_line_cnt", cntover);
                mpdata.put("stock_number", cntstocks);
                mpdata.put("av_over_cnt", Caculator.keepRound(cntover / cntstocks, 2));
                try {
                    updateDAO.execute("bankuaiDetail.insertAll", mpdata);
                    log.info(bk.getBankuai_cd() + "-" + date + " is inserted");
                } catch (Exception e) {
                    if (e instanceof DataIntegrityViolationException) {
                        log.info(bk.getBankuai_cd() + "-" + date + " is existing");
                    } else {
                        throw e;
                    }
                }


            }


        }

    }

    public void setNodeOfMacdWeek(boolean oneWeekonly) {

        List<String> ls_shsz = popertiesHelper.getStockCds();
        List<String> stock_detail = null;
        List<String> datelist = Canlendar.getAllfridaybynow("2012-01-01");
        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {


            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("stock_cd", ls_shsz.get(j));

            condition.put("record_date_to", datelist.get(datelist.size() - 1));
            condition.put("orderby", "desc");

            if (oneWeekonly) {

                condition.put("limit", 10);
            }
            List<Alldata> adls = new ArrayList<Alldata>();
            List<Alldata> adlstemp = queryDAO.executeForObjectList("download.searchdownloadResult", condition);
            int idx = 0;
            for (int i = datelist.size() - 1; i >= 0; i--) {

                for (int m = idx; m < adlstemp.size(); m++) {
                    idx++;
                    if (adlstemp.get(m).getRecord_date().compareTo(datelist.get(i)) == 0) {
                        adls.add(adlstemp.get(m));

                        break;
                    } else if (adlstemp.get(m).getRecord_date().compareTo(datelist.get(i)) < 0
                            && !Canlendar.isCurrentWeek(adlstemp.get(m).getRecord_date())) {
                        if (i > 0 && adlstemp.get(m).getRecord_date().compareTo(datelist.get(i - 1)) > 0) {
                            adls.add(adlstemp.get(m));
                            break;
                        }
                        idx--;
                        break;

                    } else if (adlstemp.get(m).getRecord_date().compareTo(datelist.get(i)) > 0) {
                        continue;
                    }
                }
            }
            ComparatorAlldata comparator = new ComparatorAlldata();
            // 升序排列
            Collections.sort(adls, comparator);

            final int intev = 2;

            for (int i = 0; i <= adls.size() - intev; i++) {
                List<Alldata> intevdata = null;

                intevdata = adls.subList(i, intev + i);
                getKeyValueOfMacdweek(intevdata,
                        intevdata.get(intevdata.size() - 1).getStock_cd(), false);
            }
        }

    }

    public void generateWeekData(boolean oneWeekonly) {

        List<String> ls_shsz = popertiesHelper.getStockCds();

        List<String> datelist = Canlendar.getAllfridaybynow("2012-01-01");
        List<Alldata> adlsOneweek;
        String mondaydate;
        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {


            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("stock_cd", ls_shsz.get(j));

            condition.put("record_date_to", datelist.get(datelist.size() - 1));
            condition.put("orderby", "desc");

            if (oneWeekonly) {

                condition.put("limit", 10);
            }
            List<WeekData> adls = new ArrayList<WeekData>();

            List<Alldata> adlstemp = queryDAO.executeForObjectList("download.searchdownloadResult", condition);
            int idx = 0;
            for (int i = datelist.size() - 1; i >= 0; i--) {

                mondaydate = Canlendar.getMondayOfSpeciWeek(datelist.get(i));
                adlsOneweek = new ArrayList<Alldata>();
                for (int m = idx; m < adlstemp.size(); m++) {
                    idx++;
                    if (adlstemp.get(m).getRecord_date().compareTo(datelist.get(i)) <= 0
                            && adlstemp.get(m).getRecord_date().compareTo(mondaydate) >= 0
                            && !Canlendar.isCurrentWeek(adlstemp.get(m).getRecord_date())) {

                        adlsOneweek.add(adlstemp.get(m));
                        continue;
                    } else if (Canlendar.isCurrentWeek(adlstemp.get(m).getRecord_date())) {
                        if (oneWeekonly) {
                            Calendar ca= Calendar.getInstance();
                            if (ca.get(Calendar.DAY_OF_WEEK) >= 6
                                    || ca.get(Calendar.DAY_OF_WEEK) == 1) {
                                adlsOneweek.add(adlstemp.get(m));
                            }
                        }

                        continue;
                    } else if (adlstemp.get(m).getRecord_date().compareTo(mondaydate) < 0) {
                        idx--;
                        break;
                    } else {
                        break;
                    }
                }
                if (adlsOneweek.isEmpty()) {
                    continue;
                }
                Map<String, Object> weekAlldata = Analysis.weekAlldataValue(adlsOneweek, adlsOneweek.size());
                WeekData adweek = new WeekData();
                adweek.setRecord_date(adlsOneweek.get(0).getRecord_date());
                adweek.setStock_cd(adlsOneweek.get(0).getStock_cd());
                adweek.setOpen_price(new BigDecimal(adlsOneweek.get(adlsOneweek.size() - 1).getTd_open_price()));
                adweek.setClose_price(adlsOneweek.get(0).getPresent_price());
                adweek.setHigh_price(new BigDecimal((double) weekAlldata.get("max")));
                adweek.setLow_price(new BigDecimal((double) weekAlldata.get("min")));

                adweek.setDeal_lots(new BigDecimal((long) weekAlldata.get("deallots")));
                adls.add(adweek);
                if (oneWeekonly) {
                    break;
                }
            }
//            ComparatorAlldata comparator = new ComparatorAlldata();
            // 升序排列
//            Collections.sort(adls, comparator);



            for (WeekData adwk : adls) {
                Map<String, Object> mpdata = new HashMap<String, Object>();

                mpdata.put("stock_cd", adwk.getStock_cd());
                mpdata.put("record_date", adwk.getRecord_date());
                mpdata.put("deal_lots", adwk.getDeal_lots());
                mpdata.put("open_price", adwk.getOpen_price());
                mpdata.put("close_price", adwk.getClose_price());
                mpdata.put("high_price", adwk.getHigh_price());
                mpdata.put("low_price", adwk.getLow_price());

                try {
                    updateDAO.execute("weekData.insertAll", mpdata);
                    log.info(adwk.getStock_cd() + "-" + adwk.getRecord_date() + " is inserted");
                } catch (Exception e) {
                    if (e instanceof DataIntegrityViolationException) {
                        log.info(adwk.getStock_cd() + "-" + adwk.getRecord_date() + " is existing");
                    } else {
                        throw e;
                    }
                }
            }
        }

    }

    private Map<String, Object> getKeyValueOfMacd(List<Alldata> intevdata, String stock_cd, boolean forrealtime) {


        // 计算的当天
        Alldata adc = intevdata.get(intevdata.size() - 1);


        Map<String, Object> mptarget = new HashMap<String, Object>();
        mptarget.put("stock_cd", stock_cd);
        mptarget.put("record_date", adc.getRecord_date());

        Macd macdtarget = queryDAO.executeForObject("macd.selectAllCond", mptarget, Macd.class);

        if (macdtarget == null) {
            // 计算的前一天
            Alldata adbf = intevdata.get(intevdata.size() - 2);


            Map<String, Object> mapbf = new HashMap<String, Object>();
            // 前一天的macd数据
            String recorddate = adbf.getRecord_date();
            mapbf.put("stock_cd", stock_cd);
            mapbf.put("record_date", recorddate);
            Macd macd = queryDAO.executeForObject("macd.selectAllCond", mapbf, Macd.class);

            int s = 12;
            int l = 26;
            double sav = 2.0 / (s + 1);
            double lav = 2.0 / (l + 1);
            double sema = 0;
            double lema = 0;

            if (macd == null) {

                if (adbf.getYt_close_price().doubleValue() < 0.001) {

                    return null;
                }

                double dif = 0;
                Map<String, Object> mp = new HashMap<String, Object>();
                mp.put("stock_cd", stock_cd);
                mp.put("record_date", recorddate);
                mp.put("dif", 0);
                if (stock_cd.equals("si000001")) {
                    mp.put("sema", adbf.getPresent_price().doubleValue() / 10);
                    mp.put("lema", adbf.getPresent_price().doubleValue() / 10);
                } else {
                    mp.put("sema", adbf.getPresent_price().doubleValue());
                    mp.put("lema", adbf.getPresent_price().doubleValue());
                }

                mp.put("dea", 0.2 * dif);
                mp.put("bar", 2 * dif);

                updateDAO.execute("macd.insertAll", mp);

                macd = queryDAO.executeForObject("macd.selectAllCond", mapbf, Macd.class);

            }

            if (stock_cd.equals("si000001")) {
                sema = sav * adc.getPresent_price().doubleValue() / 10 + (1.0 - sav) * macd.getSema().doubleValue();
                lema = lav * adc.getPresent_price().doubleValue() / 10 + (1.0 - lav) * macd.getLema().doubleValue();
            } else {
                sema = sav * adc.getPresent_price().doubleValue() + (1.0 - sav) * macd.getSema().doubleValue();
                lema = lav * adc.getPresent_price().doubleValue() + (1.0 - lav) * macd.getLema().doubleValue();
            }
            double dif = sema - lema;
            double dea = 0.2 * dif + macd.getDea().doubleValue() * 0.8;
            double bar = 2 * (dif - dea);
            Map<String, Object> mp2 = new HashMap<String, Object>();
            mp2.put("stock_cd", stock_cd);
            mp2.put("record_date", adc.getRecord_date());
            mp2.put("dif", dif);
            mp2.put("sema", sema);
            mp2.put("lema", lema);
            mp2.put("dea", dea);
            mp2.put("bar", bar);
            updateDAO.execute("macd.insertAll", mp2);
            log.info(stock_cd + " - " + adc.getRecord_date() + "macd has been inserted");

        } else {
            log.info(stock_cd + " - " + adc.getRecord_date() + "macd already existing");
        }
        return null;

    }

    private Map<String, Object> getKeyValueOfMacdweek(List<Alldata> intevdata, String stock_cd, boolean forrealtime) {


        // 计算的当天
        Alldata adc = intevdata.get(intevdata.size() - 1);


        Map<String, Object> mptarget = new HashMap<String, Object>();
        mptarget.put("stock_cd", stock_cd);
        mptarget.put("record_date", adc.getRecord_date());

        Macd macdtarget = queryDAO.executeForObject("macdweek.selectAllCond", mptarget, Macd.class);

        if (macdtarget == null) {
            // 计算的前一天
            Alldata adbf = intevdata.get(intevdata.size() - 2);


            Map<String, Object> mapbf = new HashMap<String, Object>();
            // 前一天的macd数据
            String recorddate = adbf.getRecord_date();
            mapbf.put("stock_cd", stock_cd);
            mapbf.put("record_date", recorddate);
            Macd macd = queryDAO.executeForObject("macdweek.selectAllCond", mapbf, Macd.class);

            int s = 12;
            int l = 26;
            double sav = 2.0 / (s + 1);
            double lav = 2.0 / (l + 1);
            double sema = 0;
            double lema = 0;

            if (macd == null) {

                if (adbf.getYt_close_price().doubleValue() < 0.001) {

                    return null;
                }

                double dif = 0;
                Map<String, Object> mp = new HashMap<String, Object>();
                mp.put("stock_cd", stock_cd);
                mp.put("record_date", recorddate);
                mp.put("dif", 0);
                if (stock_cd.equals("si000001")) {
                    mp.put("sema", adbf.getPresent_price().doubleValue() / 10);
                    mp.put("lema", adbf.getPresent_price().doubleValue() / 10);
                } else {
                    mp.put("sema", adbf.getPresent_price().doubleValue());
                    mp.put("lema", adbf.getPresent_price().doubleValue());
                }

                mp.put("dea", 0.2 * dif);
                mp.put("bar", 2 * dif);
                updateDAO.execute("macdweek.insertAll", mp);
                macd = queryDAO.executeForObject("macdweek.selectAllCond", mapbf, Macd.class);

            }


            if (stock_cd.equals("si000001")) {
                sema = sav * adc.getPresent_price().doubleValue() / 10 + (1.0 - sav) * macd.getSema().doubleValue();
                lema = lav * adc.getPresent_price().doubleValue() / 10 + (1.0 - lav) * macd.getLema().doubleValue();
            } else {
                sema = sav * adc.getPresent_price().doubleValue() + (1.0 - sav) * macd.getSema().doubleValue();
                lema = lav * adc.getPresent_price().doubleValue() + (1.0 - lav) * macd.getLema().doubleValue();
            }

            double dif = sema - lema;
            double dea = 0.2 * dif + macd.getDea().doubleValue() * 0.8;
            double bar = 2 * (dif - dea);
            Map<String, Object> mp2 = new HashMap<String, Object>();
            mp2.put("stock_cd", stock_cd);
            mp2.put("record_date", adc.getRecord_date());
            mp2.put("dif", dif);
            mp2.put("sema", sema);
            mp2.put("lema", lema);
            mp2.put("dea", dea);
            mp2.put("bar", bar);
            updateDAO.execute("macdweek.insertAll", mp2);
            log.info(stock_cd + " - " + adc.getRecord_date() + "macdweek has been inserted");

        } else {
            log.info(stock_cd + " - " + adc.getRecord_date() + "macdweek already existing");
        }
        return null;

    }

    public void checkKdj(List<String> ls_shsz, String date, boolean forrealtime) {


//        List<String> ls_shsz = popertiesHelper.getStockCds();
        List<String> stock_detail = null;
        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {


            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("stock_cd", ls_shsz.get(j));
            condition.put("record_date", date);
            condition.put("tendayturnover", 3);

            condition.put("orderby", "desc");
            condition.put("limit", 10);
            List<Kdj> kdjls = queryDAO.executeForObjectList("kdj.selectKdj", condition);
            if (kdjls == null || kdjls.size() == 0) {
                continue;
            }
            Kdj kdj = null;
            Kdj kdj2 = null;
            Kdj kdj3 = null;
            if (forrealtime) {

                stock_detail = httpHelper.sendRequest(ls_shsz.get(j), popertiesHelper.getStocksProperties());

                if (!LogicHelper.isOpening(stock_detail) || !LogicHelper.isStock(stock_detail)) {
                    continue;
                }
                Map<String, Object> con = new HashMap<String, Object>();
                con.put("stock_cd", ls_shsz.get(j));

                con.put("record_date_to", date);
                con.put("orderby", "desc");
                con.put("limit", 8);
                List<Alldata> adls = queryDAO.executeForObjectList("download.searchdownloadResult", con);

                Alldata alldata = Formater.changeList2bean(stock_detail);
                List<Alldata> adls2 = new ArrayList<Alldata>();
                ComparatorAlldata comparator = new ComparatorAlldata();

                adls2.add(alldata);
                adls2.addAll(adls);
                Collections.sort(adls2, comparator);

                Map mpres = getKeyValueOfkdj(adls2,
                        ls_shsz.get(j), true);
                kdj = new Kdj();
                kdj.setStock_cd(ls_shsz.get(j));

                kdj.setK(new BigDecimal((double) mpres.get("k")));
                kdj.setD(new BigDecimal((double) mpres.get("d")));
                kdj.setJ(new BigDecimal((double) mpres.get("j")));
                kdj2 = kdjls.get(0);
                kdj3 = kdjls.get(1);

            } else {
                kdj = kdjls.get(0);
                kdj2 = kdjls.get(1);
                kdj3 = kdjls.get(2);
            }


            if (kdj.getJ().doubleValue() > kdj.getK().doubleValue()
                    && kdj.getK().doubleValue() > kdj.getD().doubleValue()
                    && kdj2.getJ().doubleValue() < kdj2.getK().doubleValue()
                    && kdj2.getK().doubleValue() < kdj2.getD().doubleValue()) {// 上叉

                isSatisfyrule(kdj, "d", true);
                log.info(kdj.getStock_cd() + "j=" + Caculator.keepRound(kdj.getJ().doubleValue(), 2)
                        + "k=" + Caculator.keepRound(kdj.getK().doubleValue(), 2)
                        + "d=" + Caculator.keepRound(kdj.getD().doubleValue(), 2) + "  go up");

            } else if (kdj2.getJ().doubleValue() > kdj2.getK().doubleValue()
                    && kdj2.getK().doubleValue() > kdj2.getD().doubleValue()
                    && kdj.getJ().doubleValue() < kdj.getK().doubleValue()
                    && kdj.getK().doubleValue() < kdj.getD().doubleValue()) {// 下叉
//                isSatisfyrule(kdj,"0");
//                log.info(kdj.getStock_cd() + "j=" + kdj.getJ() + "k=" + kdj.getK() + "d=" + kdj.getD() + " go down");
            } else if (((kdj.getD().doubleValue() - kdj.getK().doubleValue()) <= 1.0
                    && (kdj.getD().doubleValue() - kdj.getK().doubleValue()) >= -1.0)
                    && ((kdj.getD().doubleValue() - kdj.getJ().doubleValue()) < 2.0
                    && (kdj.getD().doubleValue() - kdj.getJ().doubleValue()) > -2.0)) {
                if (kdj.getK().doubleValue() > kdj2.getK().doubleValue()) {//向上叉走势

                    isSatisfyrule(kdj, "d", false);
                } else {//向下叉走势
                    isSatisfyrule(kdj, "u", false);
                }

            } else if ((kdj2.getD().doubleValue() - kdj2.getJ().doubleValue()) - (kdj.getD().doubleValue() - kdj.getJ().doubleValue()) > 10
                    && kdj2.getD().doubleValue() > kdj2.getJ().doubleValue()// 
                    && kdj3.getJ().doubleValue() > kdj2.getJ().doubleValue()//
                    ) {//由底部顶点向上收拢趋势
                // 分析在下叉点之前，有几个浪了。如果有一个就可以买入了。
                int lancount = 0;
                for (int t = 0; t < kdjls.size() - 2; t++) {
                    Kdj temp1 = kdjls.get(t);
                    Kdj temp2 = kdjls.get(t + 1);
                    Kdj temp3 = kdjls.get(t + 2);
                    if (temp2.getJ().doubleValue() > temp2.getK().doubleValue()
                            && temp2.getK().doubleValue() > temp2.getD().doubleValue()
                            && temp1.getJ().doubleValue() < temp1.getK().doubleValue()
                            && temp1.getK().doubleValue() < temp1.getD().doubleValue()) {// 下叉
                        break;
//               
                    } else if ((temp2.getD().doubleValue() - temp2.getJ().doubleValue()) - (temp1.getD().doubleValue() - temp1.getJ().doubleValue()) > 10
                            && temp2.getD().doubleValue() > temp2.getJ().doubleValue()// 
                            && temp3.getJ().doubleValue() > temp2.getJ().doubleValue()//
                            ) {
                        log.info(temp1.getStock_cd() + " " + temp1.getRecord_date() + "j=" + Caculator.keepRound(temp1.getJ().doubleValue(), 2)
                                + "k=" + Caculator.keepRound(temp1.getK().doubleValue(), 2)
                                + "d=" + Caculator.keepRound(temp1.getD().doubleValue(), 2) + "  wave");
                        lancount++;
                    }

                }

                if (lancount == 0) {//没有一个浪
                    isSatisfyrule(kdj, "d", false);
                } else {
                    double curprice = Double.parseDouble(stock_detail.get(3));
                    double ydprice = Double.parseDouble(stock_detail.get(2));

                    double incr = Caculator.keepRound((curprice - ydprice) * 100 / ydprice, 2);
                    log.info(stock_detail.get(0) + "current price:" + curprice + " ,it's current increase is " + incr + "%");
                    log.info(kdj.getStock_cd() + "j=" + Caculator.keepRound(kdj.getJ().doubleValue(), 2)
                            + "k=" + Caculator.keepRound(kdj.getK().doubleValue(), 2)
                            + "d=" + Caculator.keepRound(kdj.getD().doubleValue(), 2) + "  go up from the bottom point");
                    List<Analyzedresultdata> analyzedresultdatals = new ArrayList<Analyzedresultdata>();
                    Analyzedresultdata analydt = new Analyzedresultdata();
                    analydt.setStock_cd(kdj.getStock_cd());
                    analyzedresultdatals.add(analydt);
                    try {
                        stockBuyer.buy(analyzedresultdatals, 10000);
                    } catch (NativeException ex) {
                        java.util.logging.Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Axexception ex) {
                        java.util.logging.Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Axinfo ex) {
                        java.util.logging.Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }




            } else if ((kdj.getD().doubleValue() - kdj.getJ().doubleValue()) - (kdj2.getD().doubleValue() - kdj2.getJ().doubleValue()) > 10
                    && kdj2.getD().doubleValue() < kdj2.getJ().doubleValue()
                    && ((kdj2.getD().doubleValue() - kdj2.getK().doubleValue()) <= 1.0
                    && (kdj2.getD().doubleValue() - kdj2.getK().doubleValue()) >= -1.0)
                    && ((kdj2.getD().doubleValue() - kdj2.getJ().doubleValue()) < 2.0
                    && (kdj2.getD().doubleValue() - kdj2.getJ().doubleValue()) > -2.0)) {//由节点向上发散趋势



//                isSatisfyrule(kdj, "u", false);
                double curprice = Double.parseDouble(stock_detail.get(3));
                double ydprice = Double.parseDouble(stock_detail.get(2));

                double incr = Caculator.keepRound((curprice - ydprice) * 100 / ydprice, 2);
                log.info(stock_detail.get(0) + "current price:" + curprice + " ,it's current increase is " + incr + "%");

                log.info(kdj.getStock_cd() + "j=" + Caculator.keepRound(kdj.getJ().doubleValue(), 2)
                        + "k=" + Caculator.keepRound(kdj.getK().doubleValue(), 2)
                        + "d=" + Caculator.keepRound(kdj.getD().doubleValue(), 2) + "  go up from the cross point");

                List<Analyzedresultdata> analyzedresultdatals = new ArrayList<Analyzedresultdata>();
                Analyzedresultdata analydt = new Analyzedresultdata();
                analydt.setStock_cd(kdj.getStock_cd());
                analyzedresultdatals.add(analydt);
                try {
                    stockBuyer.buy(analyzedresultdatals, 10000);
                } catch (NativeException ex) {
                    java.util.logging.Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Axexception ex) {
                    java.util.logging.Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Axinfo ex) {
                    java.util.logging.Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
                }
            }


        }
    }

    private void isSatisfyrule(Kdj kdj, String firstaction, boolean overnode) {

        Map<String, Object> mp = new HashMap<String, Object>();
        mp.put("stock_cd", kdj.getStock_cd());
        mp.put("kqujian", kqujianjudge(kdj.getK().doubleValue()));
        mp.put("first_action", firstaction);
        Kdjrule kdjrule = queryDAO.executeForObject("kdjrule.serchExsitingkdjrule", mp, Kdjrule.class);
        /*
         * 用于计算概率 这只股票有多少概率是1天为间隔的上下的 平均非1天为间隔的一个上下中间，有几个cheat,有几个浪
         *
         */
        if (kdjrule != null) {
            if (kdjrule.getMax_cheat_num() >= 1) {
                if (firstaction.equals("u")) {//往
                    // 15收盘时点买入
//                    log.info(kdj.getStock_cd() +"can buy because cheating");
                }
            } else {
                if (firstaction.equals("d")) {//上叉
                    double lostrate = (double) kdjrule.getOne_intervelday_count() / (double) kdjrule.getMatch_count();
                    log.info(kdj.getStock_cd() + "can buy and winrate is" + Caculator.keepRound(100 - (lostrate * 100), 2) + "%");
                    if (lostrate < 0.5) {
                        if (kdjrule.getGather_spread_count() != 0 || kdjrule.getMax_cheat_num() != 0) {
                            return;
                        }
                        if (kdj.getD().doubleValue() > 50 || kdj.getD().doubleValue() < 20) {

                            List<String> stock_detail = httpHelper.sendRequest(kdj.getStock_cd(), popertiesHelper.getStocksProperties());

                            if (!LogicHelper.isOpening(stock_detail) || !LogicHelper.isStock(stock_detail)) {
                                return;
                            }

                            double curprice = Double.parseDouble(stock_detail.get(3));
                            double ydprice = Double.parseDouble(stock_detail.get(2));

                            double incr = Caculator.keepRound((curprice - ydprice) * 100 / ydprice, 2);
                            log.info(stock_detail.get(0) + "current price:" + curprice + " ,it's current increase is " + incr + "%");
                            log.info(kdj.getStock_cd() + "j=" + Caculator.keepRound(kdj.getJ().doubleValue(), 2)
                                    + "k=" + Caculator.keepRound(kdj.getK().doubleValue(), 2)
                                    + "d=" + Caculator.keepRound(kdj.getD().doubleValue(), 2) + " is over node " + overnode);
                            log.info(kdj.getStock_cd() + "can buy and winrate is" + Caculator.keepRound(100 - (lostrate * 100), 2) + "%");
                            List<Analyzedresultdata> analyzedresultdatals = new ArrayList<Analyzedresultdata>();
                            Analyzedresultdata analydt = new Analyzedresultdata();
                            analydt.setStock_cd(kdj.getStock_cd());
                            analyzedresultdatals.add(analydt);
                            try {
                                stockBuyer.buy(analyzedresultdatals, 10000);
                            } catch (NativeException ex) {
                                java.util.logging.Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Axexception ex) {
                                java.util.logging.Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Axinfo ex) {
                                java.util.logging.Logger.getLogger(Strategy.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    } else {
                    }



                } else {
                }

            }
        }


    }

    /*
     * 对换手率为指定以上的 所有股票进行分析
     */
    public void doanly(String dateto, double turnoverrate) {
        List<String> ls_shsz = popertiesHelper.getStockCds();
        List<String> stock_detail = null;
        // get the detail of the stocks
        updateDAO.execute("kdjrule.deletekdjrule", null);
        log.info("kdjrule's data has been deleted");

        for (int j = 0; j < ls_shsz.size(); j++) {


            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("stock_cd", ls_shsz.get(j));
            condition.put("record_date", dateto);
            condition.put("tendayturnover", turnoverrate);

            condition.put("orderby", "desc");

//                condition.put("limit", dayrange);


            List<Kdj> adls = queryDAO.executeForObjectList("kdj.selectKdj", condition);
            if (adls == null || adls.size() == 0) {
                continue;
            }

            stockKdjRuleAnalyze(adls);

        }
    }

    private void stockKdjRuleAnalyze(List<Kdj> kdjls) {

        log.info("checking " + kdjls.size() + " histories");
        boolean scstart = false;
        boolean xcstart = false;
        int scindex = 0;
        int xcindex = 0;

        int pxcountup = 0;
        int pxcountdown = 0;

        int xsslks = 0;//向上收敛扩散
        int xxslks = 0;//向下收敛扩散
        for (int i = 0; i < kdjls.size() - 1; i++) {
            Kdj kdj = kdjls.get(i);// 现在的点
            Kdj kdj2 = kdjls.get(i + 1);// 前1天的点
            Kdj kdj3 = null;
            Kdj kdj4 = null;
            if (i >= 1) {
                kdj4 = kdjls.get(i - 1);// 后1天的点
            }
            if (i < kdjls.size() - 2) {
                kdj3 = kdjls.get(i + 2);// 前2天的点
            }
            // 从右往左边数
            if (kdj.getJ().doubleValue() > kdj.getK().doubleValue()
                    && kdj.getK().doubleValue() > kdj.getD().doubleValue()
                    && kdj2.getJ().doubleValue() < kdj2.getK().doubleValue()
                    && kdj2.getK().doubleValue() < kdj2.getD().doubleValue()) {

                if (xcstart) {// 从左边一个下叉到右边一个下叉之间，所以是先降后升
                    int inteval = i - xcindex;
                    updaterule(kdj, pxcountup, inteval, xsslks, "d");
                    pxcountup = 0;
                    xsslks = 0;
                }

                scstart = true;
                xcstart = false;
                scindex = i;

            } else if (kdj2.getJ().doubleValue() > kdj2.getK().doubleValue()
                    && kdj2.getK().doubleValue() > kdj2.getD().doubleValue()
                    && kdj.getJ().doubleValue() < kdj.getK().doubleValue()
                    && kdj.getK().doubleValue() < kdj.getD().doubleValue()) {
                if (scstart) {// 从左边一个上叉到右边相邻的一个下叉之间，所以是先升后降
                    int inteval = i - scindex;
                    updaterule(kdj, pxcountdown, inteval, xxslks, "u");
                    pxcountdown = 0;
                    xxslks = 0;
                }
                xcstart = true;
                scstart = false;
                xcindex = i;

            } else if (((kdj.getD().doubleValue() - kdj.getK().doubleValue()) <= 1.0
                    && (kdj.getD().doubleValue() - kdj.getK().doubleValue()) >= -1.0)
                    && ((kdj.getD().doubleValue() - kdj.getJ().doubleValue()) < 2.0
                    && (kdj.getD().doubleValue() - kdj.getJ().doubleValue()) > -2.0)) {// 骗线点
                if (kdj.getK().doubleValue() > kdj2.getK().doubleValue()) {//向上叉走势
                    if (xcstart || scstart) {
                        log.info(kdj.getStock_cd() + " " + kdj.getRecord_date() + "j=" + Caculator.keepRound(kdj.getJ().doubleValue(), 2)
                                + "k=" + Caculator.keepRound(kdj.getK().doubleValue(), 2)
                                + "d=" + Caculator.keepRound(kdj.getD().doubleValue(), 2) + "  go up px");
                        pxcountup++;
                    }

                } else {//向下叉走势
                    if (xcstart || scstart) {
                        log.info(kdj.getStock_cd() + " " + kdj.getRecord_date() + "j=" + Caculator.keepRound(kdj.getJ().doubleValue(), 2)
                                + "k=" + Caculator.keepRound(kdj.getK().doubleValue(), 2)
                                + "d=" + Caculator.keepRound(kdj.getD().doubleValue(), 2) + "  go down px");
                        pxcountdown++;
                    }

                }

            } else if (kdj4 != null && kdj3 != null) {
                if ((kdj2.getD().doubleValue() - kdj2.getJ().doubleValue()) - (kdj.getD().doubleValue() - kdj.getJ().doubleValue()) > 10
                        && kdj2.getD().doubleValue() > kdj2.getJ().doubleValue()//低点向上收敛
                        && kdj3.getJ().doubleValue() > kdj2.getJ().doubleValue()//
                        && (kdj4.getD().doubleValue() - kdj4.getJ().doubleValue()) - (kdj.getD().doubleValue() - kdj.getJ().doubleValue()) > 10//发散
                        && kdj4.getJ().doubleValue() < kdj.getJ().doubleValue()//J值下降
                        ) {// 计算先向上收敛后再马上发散的点
                    log.info(kdj.getStock_cd() + " " + kdj.getRecord_date() + "j=" + Caculator.keepRound(kdj.getJ().doubleValue(), 2)
                            + "k=" + Caculator.keepRound(kdj.getK().doubleValue(), 2)
                            + "d=" + Caculator.keepRound(kdj.getD().doubleValue(), 2) + "  向上收敛发散");
                    xsslks++;

                } else if ((kdj2.getJ().doubleValue() - kdj2.getD().doubleValue()) - (kdj.getJ().doubleValue() - kdj.getD().doubleValue()) > 10
                        && kdj2.getJ().doubleValue() > kdj2.getD().doubleValue()//高点向下收敛
                        && kdj3.getJ().doubleValue() < kdj2.getJ().doubleValue()//
                        && (kdj4.getJ().doubleValue() - kdj4.getD().doubleValue()) - (kdj.getJ().doubleValue() - kdj.getD().doubleValue()) > 10//发散
                        && kdj4.getJ().doubleValue() > kdj.getJ().doubleValue()//J值上升
                        ) {// 计算先向下收敛后再马上发散的点
                    log.info(kdj.getStock_cd() + " " + kdj.getRecord_date() + "j=" + Caculator.keepRound(kdj.getJ().doubleValue(), 2)
                            + "k=" + Caculator.keepRound(kdj.getK().doubleValue(), 2)
                            + "d=" + Caculator.keepRound(kdj.getD().doubleValue(), 2) + "  向下收敛发散");
                    xxslks++;

                }
            }

        }




    }

    private String kqujianjudge(double k) {
        String resultstr = "";
        if (k <= 20) {
            resultstr = "0020";
        } else if (20 < k && k <= 50) {
            resultstr = "2050";
        } else if (50 < k && k <= 80) {
            resultstr = "5080";
        } else if (80 < k) {
            resultstr = "8000";
        }

        return resultstr;

    }

    /*
     * Kdj rule 更新和插入
     */
    private void updaterule(Kdj kdj, int pxcount, int inteval, int gather_spread_count, String fristaction) {
        // k值区间，firstaction, pianxian ,secondaction,inteval,count
        Map<String, Object> mp = new HashMap<String, Object>();

        mp.put("stock_cd", kdj.getStock_cd());
        mp.put("kqujian", kqujianjudge(kdj.getK().doubleValue()));
        mp.put("first_action", fristaction);
        Kdjrule kdjrule = queryDAO.executeForObject("kdjrule.serchExsitingkdjrule", mp, Kdjrule.class);
        if (kdjrule == null) {
            mp.put("shortest_inteval_days", inteval);
            mp.put("longest_inteval_days", inteval);
            mp.put("max_cheat_num", pxcount);
            if (fristaction.equals("d")) {//
                mp.put("second_action", "u");//
            } else {
                mp.put("second_action", "d");
            }
            if (inteval == 1) {
                mp.put("one_intervelday_count", 1);
            } else {
                mp.put("one_intervelday_count", 0);
            }
            mp.put("gather_spread_count", gather_spread_count);
            mp.put("match_count", 1);
            updateDAO.execute("kdjrule.insert2kdjrule", mp);
            log.info(kdj.getStock_cd() + " kdjrule has been inserted");
        } else {
            if (kdjrule.getShortest_inteval_days() > inteval) {
                mp.put("shortest_inteval_days", inteval);
            } else if (kdjrule.getLongest_inteval_days() < inteval) {
                mp.put("longest_inteval_days", inteval);
            }

            mp.put("max_cheat_num", kdjrule.getMax_cheat_num() + pxcount);//总共多少个cheat
            if (inteval == 1) {
                mp.put("one_intervelday_count", kdjrule.getOne_intervelday_count() + 1);// 总共多少个1天为间隔的上下
            }
            mp.put("gather_spread_count", kdjrule.getGather_spread_count() + gather_spread_count);//总共多少个浪
            mp.put("match_count", kdjrule.getMatch_count() + 1);//总共多少个上下
            /*
             * 用于计算概率 这只股票有多少概率是1天为间隔的上下的 平均非1天为间隔的一个上下中间，有几个cheat,有几个浪
             *
             */
            updateDAO.execute("kdjrule.updateKdjrule", mp);
            log.info(kdj.getStock_cd() + " kdjrule has been updated");
        }
    }

    public void kdjMacd(String dateend, boolean onedayonly) {


        List<String> ls_shsz = popertiesHelper.getStockCds();
        List<String> stock_detail = null;
        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {
            //kdjlist
            Map<String, Object> conditionkdj = new HashMap<String, Object>();
            conditionkdj.put("stock_cd", ls_shsz.get(j));
            conditionkdj.put("record_date", dateend);

            conditionkdj.put("orderby", "desc");
            if (onedayonly) {
                conditionkdj.put("limit", 4);
            }

            List<Kdj> kdjls = queryDAO.executeForObjectList("kdj.selectKdj", conditionkdj);

            //macdlist
            Map<String, Object> conditionMacd = new HashMap<String, Object>();
            conditionMacd.put("stock_cd", ls_shsz.get(j));
            conditionMacd.put("record_date_to", dateend);
            conditionMacd.put("order", "desc");
            if (onedayonly) {
                conditionMacd.put("limit", 4);
            }
            List<Macd> macdls = queryDAO.executeForObjectList("macd.selectAllCond", conditionMacd);
            //alldatalist

            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("stock_cd", ls_shsz.get(j));
            condition.put("record_date_to", dateend);
            condition.put("orderby", "desc");
            if (onedayonly) {
                condition.put("limit", 4);
            }
            List<Alldata> adls = queryDAO.executeForObjectList("download.searchdownloadResult", condition);
            ComparatorAlldata comparator = new ComparatorAlldata();


//            Collections.sort(adls, comparator);
            //对每个按时间进行查询
            for (int t = 1; t < kdjls.size() - 2; t++) {

                Macd macdtoday = macdls.get(t);
                Macd macdyesterday = macdls.get(t + 1);
//                Macd macdthedaybeforeyesterday = macdls.get(t + 2);

                Alldata adtomorrow = adls.get(t - 1);
                Alldata adtoday = adls.get(t);
//                Alldata adyesterday = adls.get(t + 1);
//                Alldata adthedaybeforeyesterday = adls.get(t + 2);

                Kdj kdjtoday = kdjls.get(t);
                Kdj kdjyesterday = kdjls.get(t + 1);
                Kdj kdjthedaybeforeyesterday = kdjls.get(t + 2);

                String kdjtype = Analysis.getkdjtype(kdjtoday, kdjyesterday, kdjthedaybeforeyesterday);
                String macdtype = Analysis.getMacdtype(macdtoday, macdyesterday);
////                String afterdayincr = "";
//                if (Analysis.isUpCross(kdjtoday, kdjyesterday)) {
//                    kdjtype = "upc";
//                } else if (Analysis.isUpWave(kdjtoday, kdjyesterday, kdjthedaybeforeyesterday)) {
//                    if (Analysis.isOver(kdjtoday)) {
//
//                        if (kdjtoday.getJ().doubleValue() > 100) {
//                            kdjtype = "ovupwtp";
//                        } else {
//                            kdjtype = "ovupw";
//                        }
//                    } else {
//
//                        if (kdjtoday.getJ().doubleValue() < 0) {
//                            kdjtype = "udupwbt";
//                        } else {
//                            kdjtype = "udupw";
//                        }
//                    }
//                } else if (Analysis.isApprochCross(kdjtoday) && !Analysis.isOver(kdjtoday)) {
//
//                    kdjtype = "uppc";
//                }
//
//
//                if (macdtoday.getBar().doubleValue() > macdyesterday.getBar().doubleValue()) {// 柱子今日大于昨日
//
//                    if (macdtoday.getBar().doubleValue() < 0) {//今日柱子小于0
//                        if (macdtoday.getDif().doubleValue() < 0
//                                && macdtoday.getDea().doubleValue() > 0) {// dif 小于0 dea大于0
//                            macdtype = "p1";
//                        } else if (macdtoday.getDif().doubleValue() < 0
//                                && macdtoday.getDea().doubleValue() < 0) {// dif 小于0 dea 小于0
//                            macdtype = "p2";
//                        } else if (macdtoday.getDif().doubleValue() > 0
//                                && macdtoday.getDea().doubleValue() > 0) {// dif 大于0 dea 大于0
//                            macdtype = "p8";
//                        }
//                    } else if (macdtoday.getBar().doubleValue() > 0 && macdyesterday.getBar().doubleValue() < 0) {// 蓝红交替
//                        if (macdtoday.getDif().doubleValue() < 0) {
//                            macdtype = "p3";
//                        }
//                    } else if (macdtoday.getBar().doubleValue() > 0 && macdyesterday.getBar().doubleValue() > 0
//                            && macdtoday.getDif().doubleValue() > macdtoday.getDea().doubleValue()
//                            && macdtoday.getDif().doubleValue() < 0) {//红柱子 刚刚起步涨
//                        macdtype = "p4";
//                    }
//                } else if (macdtoday.getBar().doubleValue() < macdyesterday.getBar().doubleValue()) {//柱子今日小于昨日
//                    if (macdyesterday.getBar().doubleValue() < 0) {//昨日日柱子小于0
//                        if (macdtoday.getDif().doubleValue() < 0
//                                && macdtoday.getDea().doubleValue() > 0) {// dif 小于0 dea大于0
//                            macdtype = "p5";
//                        } else if (macdtoday.getDif().doubleValue() < 0) {// dif 小于0 dea 小于0
//                            macdtype = "p6";
//                        } else if (macdtoday.getDif().doubleValue() > 0) {// dif dea >0
//                            macdtype = "p7";
//                        }
//                    }
//                }

                if (!kdjtype.equals("") && !macdtype.equals("")) {
                    double incr = (adtomorrow.getTd_highest_price().doubleValue() - adtoday.getPresent_price().doubleValue())
                            / adtoday.getPresent_price().doubleValue() * 100;

                    Map<String, Object> mp = new HashMap<String, Object>();
                    mp.put("stock_cd", adtoday.getStock_cd());
                    mp.put("record_date", adtoday.getRecord_date());

                    KdjMacdMatchHistory KdjMacdMatchHistory = queryDAO.executeForObject("kdjMacdMatchHistory.selectAllCond", mp, KdjMacdMatchHistory.class);
                    if (KdjMacdMatchHistory == null) {
                        mp.put("kdj_type", kdjtype);
                        mp.put("macd_type", macdtype);
                        mp.put("second_day_incr", Caculator.keepRound(incr, 2));
                        updateDAO.execute("kdjMacdMatchHistory.insertAll", mp);
                        log.info(adtoday.getStock_cd() + " " + adtoday.getRecord_date() + " has been inserted to kdjMacdMatchHistory");
                    } else {
                        log.info(adtoday.getStock_cd() + " " + adtoday.getRecord_date() + "is already exsisting in kdjMacdMatchHistory");
                    }

                }


            }

        }


    }

    public void fiveLinePerformance(String dateend, boolean onedayonly) {

        List<String> ls_shsz = popertiesHelper.getStockCds();
        List<String> stock_detail = null;
        // get the detail of the stocks
        for (int j = 0; j < ls_shsz.size(); j++) {
            //kdjlist
            Map<String, Object> conditionkdj = new HashMap<String, Object>();
            conditionkdj.put("stock_cd", ls_shsz.get(j));
            conditionkdj.put("record_date", dateend);

            conditionkdj.put("orderby", "desc");
            if (onedayonly) {
                conditionkdj.put("limit", 10);
            }

            List<Kdj> kdjls = queryDAO.executeForObjectList("kdj.selectKdj", conditionkdj);

            //macdlist
            Map<String, Object> conditionMacd = new HashMap<String, Object>();
            conditionMacd.put("stock_cd", ls_shsz.get(j));
            conditionMacd.put("record_date_to", dateend);
            conditionMacd.put("order", "desc");
            if (onedayonly) {
                conditionMacd.put("limit", 10);
            }
            List<Macd> macdls = queryDAO.executeForObjectList("macd.selectAllCond", conditionMacd);
            //alldatalist

            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("stock_cd", ls_shsz.get(j));
            condition.put("record_date_to", dateend);
            condition.put("orderby", "desc");
            if (onedayonly) {
                condition.put("limit", 10);
            }
            List<Alldata> adls = queryDAO.executeForObjectList("download.searchdownloadResult", condition);
            ComparatorAlldata comparator = new ComparatorAlldata();


//            Collections.sort(adls, comparator);
            //对每个按时间进行查询
            for (int t = 3; t < kdjls.size() - 4; t++) {



                Alldata onedaylater = adls.get(t - 1);
                Alldata twodaylater = adls.get(t - 2);
                Alldata threedaylater = adls.get(t - 3);
                Alldata adtoday = adls.get(t);
                Alldata adyesterday = adls.get(t + 1);

                double today_ave = Analysis.caculateAveNday(adls.subList(t, t + 5), 5);
                double yesterday_ave = Analysis.caculateAveNday(adls.subList(t + 1, t + 6), 5);
                double twodaybefore_ave = Analysis.caculateAveNday(adls.subList(t + 2, t + 7), 5);


//                if ((today_ave-yesterday_ave)/yesterday_ave > 0.00168
//                        && twodaybefore_ave > yesterday_ave) {
                if (adtoday.getPresent_price().doubleValue() > today_ave
                        && adyesterday.getPresent_price().doubleValue() <= yesterday_ave) {


                    Kdj kdjtoday = kdjls.get(t);
                    Kdj kdjyesterday = kdjls.get(t + 1);
                    Kdj kdjthedaybeforeyesterday = kdjls.get(t + 2);

                    String kdjtype = Analysis.getkdjtype(kdjtoday, kdjyesterday, kdjthedaybeforeyesterday);

                    Macd macdtoday = macdls.get(t);
                    Macd macdyesterday = macdls.get(t + 1);
                    String macdtype = Analysis.getMacdtype(macdtoday, macdyesterday);

                    double threedayMaxPrice = Analysis.maxValue(onedaylater.getPresent_price().doubleValue(),
                            twodaylater.getPresent_price().doubleValue(),
                            threedaylater.getPresent_price().doubleValue());
                    // 3天后的最大涨幅
                    double three_day_maxincr = Caculator.keepRound((threedayMaxPrice - adtoday.getPresent_price().doubleValue()) / adtoday.getPresent_price().doubleValue() * 100, 2);
                    // 相对于昨日涨幅
                    double incr = Caculator.keepRound((adtoday.getPresent_price().doubleValue() - adyesterday.getPresent_price().doubleValue()) / adyesterday.getPresent_price().doubleValue() * 100, 2);
                    // 相对于昨日成交量倍数
                    double deallotsbeishu = Caculator.keepRound((adtoday.getDeal_lots() + 0.0) / adyesterday.getDeal_lots(), 2);


                    Map<String, Object> mp = new HashMap<String, Object>();
                    mp.put("stock_cd", adtoday.getStock_cd());
                    mp.put("record_date", adtoday.getRecord_date());

                    Fivelineupw fivelineupw = queryDAO.executeForObject("fivelineupw.selectAllCond", mp, Fivelineupw.class);
                    if (fivelineupw == null) {
                        mp.put("kdj_type", kdjtype);
                        mp.put("macd_type", macdtype);
                        mp.put("price_incr", incr);
                        mp.put("deallots_beishu", deallotsbeishu);
                        mp.put("three_day_maxincr", three_day_maxincr);
                        updateDAO.execute("fivelineupw.insertAll", mp);
                        log.info(adtoday.getStock_cd() + " " + adtoday.getRecord_date() + " has been inserted to fivelineupw");
                    } else {
                        log.info(adtoday.getStock_cd() + " " + adtoday.getRecord_date() + "is already exsisting in fivelineupw");
                    }

                    String startDayagoDate = null;
                    String endDayagoDate = null;

                    if (t + 7 < adls.size()) {
                        startDayagoDate = adls.get(t + 7).getRecord_date();
                    }
                    if (t + 23 < adls.size()) {
                        endDayagoDate = adls.get(t + 23).getRecord_date();
                    }



                    Map<String, Object> mp2 = new HashMap<String, Object>();
                    mp2.put("stock_cd", adtoday.getStock_cd());
                    mp2.put("record_date_lessthan", startDayagoDate);
                    mp2.put("record_date_greaterthan", endDayagoDate);

                    List<Fivelineupw> fivelineupwagoList = queryDAO.executeForObjectList("fivelineupw.selectAllCond", mp2);
                    if (fivelineupwagoList == null || fivelineupwagoList.size() == 0) {
                        continue;
                    }

                    for (int x = fivelineupwagoList.size() - 1; x >= 0; x--) {
                        String compareTargetDate = fivelineupwagoList.get(x).getRecord_date();
                        Alldata onedayBeforeOftarget = null;
                        int inteval = 0;
                        double pricemount = 0;
                        for (int i = t; i < adls.size(); i++) {
                            inteval++;
                            pricemount = pricemount + adls.get(i + 1).getPresent_price().doubleValue();
                            if (adls.get(i).getRecord_date().equals(compareTargetDate)) {
                                onedayBeforeOftarget = adls.get(i + 1);

                                break;
                            }
                        }
                        double price1 = Math.min(Double.parseDouble(onedayBeforeOftarget.getTd_open_price()), onedayBeforeOftarget.getPresent_price().doubleValue());
                        double price2 = Math.min(Double.parseDouble(adyesterday.getTd_open_price()), adyesterday.getPresent_price().doubleValue());


                        if (Math.abs(((price2 - price1) / price1)) < inteval * 0.002
                                && (price2 + price1) / 2 * inteval < pricemount) {
                            log.info(adtoday.getStock_cd() + " " + adtoday.getRecord_date() + " compare date is " + compareTargetDate + " " + +inteval + " days ago" + ",price_incr=" + incr + ",deallots_beishu=" + deallotsbeishu
                                    + ",three_day_maxincr=" + three_day_maxincr);
                        }

                    }



                }


            }

        }


    }

    public void checkhistorywithconditions(String dateend, boolean onedayonly, int[] timewindow) {

        List<String> ls_shsz = popertiesHelper.getStockCds();
        List<String> stock_detail = null;
        // get the detail of the stocks
        int cntsf1 = 0;
        double sumincrsf1 = 0;
        double sumdecrsf1 = 0;
        int cntsf2 = 0;
        double sumincrsf2 = 0;
        double sumdecrsf2 = 0;
        int cntsf3 = 0;
        double sumincrsf3 = 0;
        double sumdecrsf3 = 0;
        int cntsf4 = 0;
        double sumincrsf4 = 0;
        double sumdecrsf4 = 0;
        int cntsf5 = 0;
        double sumincrsf5 = 0;
        double sumdecrsf5 = 0;
        int cntsf6 = 0;
        double sumincrsf6 = 0;
        double sumdecrsf6 = 0;
        int cntsf7 = 0;
        double sumincrsf7 = 0;
        double sumdecrsf7 = 0;
        int cntsf8 = 0;
        double sumincrsf8 = 0;
        double sumdecrsf8 = 0;
        int cntsf9 = 0;
        double sumincrsf9 = 0;
        double sumdecrsf9 = 0;
        int cntsf10 = 0;
        double sumincrsf10 = 0;
        double sumdecrsf10 = 0;
        int cntsf11 = 0;
        double sumincrsf11 = 0;
        double sumdecrsf11 = 0;
        int cntsf12 = 0;
        double sumincrsf12 = 0;
        double sumdecrsf12 = 0;
        int cntsf13 = 0;
        double sumincrsf13 = 0;
        double sumdecrsf13 = 0;
        int cntsf14 = 0;
        double sumincrsf14 = 0;
        double sumdecrsf14 = 0;
        int cntsf15 = 0;
        double sumincrsf15 = 0;
        double sumdecrsf15 = 0;
        int cntsf16 = 0;
        double sumincrsf16 = 0;
        double sumdecrsf16 = 0;
        int cntsf17 = 0;
        double sumincrsf17 = 0;
        double sumdecrsf17 = 0;
        int cntsf18 = 0;
        double sumincrsf18 = 0;
        double sumdecrsf18 = 0;
        int cntsf21 = 0;
        double sumincrsf21 = 0;
        double sumdecrsf21 = 0;
        int cntsf22 = 0;
        double sumincrsf22 = 0;
        double sumdecrsf22 = 0;
        Map<String, Object> group = new HashMap<String, Object>();
        for (int j = 0; j < ls_shsz.size(); j++) {
            //kdjlist
            Map<String, Object> conditionkdj = new HashMap<String, Object>();
            conditionkdj.put("stock_cd", ls_shsz.get(j));
            conditionkdj.put("record_date", dateend);
            conditionkdj.put("tendayturnover", 0);

            conditionkdj.put("orderby", "desc");
            conditionkdj.put("limit", 26);
            List<Kdj> kdjls = queryDAO.executeForObjectList("kdj.selectKdj", conditionkdj);

            //macdlist
            Map<String, Object> conditionMacd = new HashMap<String, Object>();
            conditionMacd.put("stock_cd", ls_shsz.get(j));
            conditionMacd.put("record_date_to", dateend);
            conditionMacd.put("order", "desc");
            if (onedayonly) {
                conditionMacd.put("limit", 26);
            }
            List<Macd> macdls = queryDAO.executeForObjectList("macd.selectAllCond", conditionMacd);
            List<Macd> macdweekls = queryDAO.executeForObjectList("macdweek.selectAllCond", conditionMacd);

            //alldatalist

            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("stock_cd", ls_shsz.get(j));
            condition.put("record_date_to", dateend);
            condition.put("orderby", "desc");
            if (onedayonly) {
                condition.put("limit", 26);
            }
            List<Alldata> adls = queryDAO.executeForObjectList("download.searchdownloadResult", condition);

            int[] mmarr = null;
            for (int kk = 1; kk < macdweekls.size() - 2; kk++) {

                double prnextweek = 0;
                double prcurrentweek = 0;
                double prlastweek = 0;
                double prlast2week = 0;
                for (Alldata ad : adls) {
                    if (ad.getRecord_date().equals(macdweekls.get(kk).getRecord_date())) {
                        prcurrentweek = ad.getPresent_price().doubleValue();
                    } else if (ad.getRecord_date().equals(macdweekls.get(kk + 1).getRecord_date())) {
                        prlastweek = ad.getPresent_price().doubleValue();
                    } else if (ad.getRecord_date().equals(macdweekls.get(kk + 2).getRecord_date())) {
                        prlast2week = ad.getPresent_price().doubleValue();
                    } else if (ad.getRecord_date().equals(macdweekls.get(kk - 1).getRecord_date())) {
                        prnextweek = ad.getPresent_price().doubleValue();
                    }
                    if (prcurrentweek * prlastweek * prnextweek * prlast2week != 0) {
                        break;
                    }
                }
                if (macdweekls.get(kk).getBar().doubleValue() < macdweekls.get(kk + 1).getBar().doubleValue()) {
                    if (prcurrentweek > prlastweek
                            && prlast2week > prlastweek
                            && macdweekls.get(kk).getBar().doubleValue() > 0
                            && macdweekls.get(kk).getDea().doubleValue() < 0
                            && macdweekls.get(kk).getDea().doubleValue() > macdweekls.get(kk + 1).getDea().doubleValue()) {
                        log.info("\t" + "算法macd周线" + "\t" + macdweekls.get(kk).getStock_cd() + "\t" + macdweekls.get(kk).getRecord_date() + "\t背离"
                                + " nextincr=" + "\t" + Caculator.keepRound((prnextweek - prcurrentweek) / prcurrentweek * 100, 2));
                    }
                }
                if (macdweekls.get(kk).getBar().doubleValue() > macdweekls.get(kk + 1).getBar().doubleValue()) {
                    if (prcurrentweek > prlastweek
                            && prlast2week > prlastweek
                            && macdweekls.get(kk).getBar().doubleValue() > 0
                            && macdweekls.get(kk).getDea().doubleValue() < 0
                            && macdweekls.get(kk).getDea().doubleValue() > macdweekls.get(kk + 1).getDea().doubleValue()) {
                        log.info("\t" + "算法macd周线" + "\t" + macdweekls.get(kk).getStock_cd() + "\t" + macdweekls.get(kk).getRecord_date() + "\t背离"
                                + " nextincr=" + "\t" + Caculator.keepRound((prnextweek - prcurrentweek) / prcurrentweek * 100, 2));
                    }
                }
            }


            //对每个按时间进行查询
            for (int t = 5; t < macdls.size() - 26; t++) {



                Alldata onedaylater = adls.get(t - 1);
                Alldata twodaylater = adls.get(t - 2);
                Alldata threedaylater = adls.get(t - 3);
                Alldata fourdaylater = adls.get(t - 4);
                Alldata fivedaylater = adls.get(t - 5);
                Alldata adtoday = adls.get(t);
                Alldata adyesterday = adls.get(t + 1);
                Alldata twodaybefore = adls.get(t + 2);
                Alldata threedaybefore = adls.get(t + 3);
                Alldata fourdaybefore = adls.get(t + 4);
                Alldata fivedaybefore = adls.get(t + 5);
                double curprice = adtoday.getPresent_price().doubleValue();
                double ydprice = adtoday.getYt_close_price().doubleValue();

                double openprice = Double.parseDouble(adtoday.getTd_open_price());
                double lowestprice = adtoday.getTd_lowest_price().doubleValue();
                double highestprice = adtoday.getTd_highest_price().doubleValue();

                double incr = Caculator.keepRound((curprice - ydprice) * 100 / ydprice, 2);


                // 相对于昨日成交量倍数
                double deallotsbeishu = Caculator.keepRound((adtoday.getDeal_lots() + 0.0) / adyesterday.getDeal_lots(), 2);


                double today_ave_5 = Analysis.caculateAveNday(adls.subList(t, t + 5), 5);
                double yesterday_ave_5 = Analysis.caculateAveNday(adls.subList(t + 1, t + 6), 5);
                double twodaybefore_ave_5 = Analysis.caculateAveNday(adls.subList(t + 2, t + 7), 5);
                double threedaybefore_ave_5 = Analysis.caculateAveNday(adls.subList(t + 3, t + 8), 5);
                double fourdaybefore_ave_5 = Analysis.caculateAveNday(adls.subList(t + 4, t + 9), 5);
                double fivedaybefore_ave_5 = Analysis.caculateAveNday(adls.subList(t + 5, t + 10), 5);

                double today_ave_10 = Analysis.caculateAveNday(adls.subList(t, t + 10), 10);
                double yesterday_ave_10 = Analysis.caculateAveNday(adls.subList(t + 1, t + 11), 10);
                double twodaybefore_ave_10 = Analysis.caculateAveNday(adls.subList(t + 2, t + 12), 10);
                double threedaybefore_ave_10 = Analysis.caculateAveNday(adls.subList(t + 3, t + 13), 10);
                double fourdaybefore_ave_10 = Analysis.caculateAveNday(adls.subList(t + 4, t + 14), 10);
                double fivedaybefore_ave_10 = Analysis.caculateAveNday(adls.subList(t + 5, t + 15), 10);

                double today_ave_20 = Analysis.caculateAveNday(adls.subList(t, t + 20), 20);
                double yesterday_ave_20 = Analysis.caculateAveNday(adls.subList(t + 1, t + 21), 20);
                double twodaybefore_ave_20 = Analysis.caculateAveNday(adls.subList(t + 2, t + 22), 20);
                double threedaybefore_ave_20 = Analysis.caculateAveNday(adls.subList(t + 3, t + 23), 20);
                double fourdaybefore_ave_20 = Analysis.caculateAveNday(adls.subList(t + 4, t + 24), 20);
                double fivedaybefore_ave_20 = Analysis.caculateAveNday(adls.subList(t + 5, t + 25), 20);

                double ndayMaxPrice = Analysis.maxValue(onedaylater.getPresent_price().doubleValue(),
                        twodaylater.getPresent_price().doubleValue(),
                        threedaylater.getPresent_price().doubleValue(),
                        fourdaylater.getPresent_price().doubleValue(),
                        fivedaylater.getPresent_price().doubleValue());
                double ndayMinPrice = Analysis.minValue(onedaylater.getPresent_price().doubleValue(),
                        twodaylater.getPresent_price().doubleValue(),
                        threedaylater.getPresent_price().doubleValue(),
                        fourdaylater.getPresent_price().doubleValue(),
                        fivedaylater.getPresent_price().doubleValue());
                double mxinc = Caculator.keepRound((ndayMaxPrice - adtoday.getPresent_price().doubleValue()) / adtoday.getPresent_price().doubleValue() * 100, 2);
                double miinc = Caculator.keepRound((ndayMinPrice - adtoday.getPresent_price().doubleValue()) / adtoday.getPresent_price().doubleValue() * 100, 2);


                String content = "";
                if (fivedaylater.getPresent_price().doubleValue() >= adtoday.getPresent_price().doubleValue()) {
                    content = content + "曲线向上";
                }
                if (fivedaylater.getPresent_price().doubleValue() < adtoday.getPresent_price().doubleValue()) {
                    content = content + "曲线向下";
                }

                // ****************** 算法1开始******************
                mmarr = new int[]{8, 13, 21};
                if ((today_ave_5 < yesterday_ave_5
                        && today_ave_10 < yesterday_ave_10)) {
                    if (deallotsbeishu <= 3
                            && Analysis.timeWindow(adls.subList(t, t + 22), mmarr, curprice, 22)
                            && incr > 0
                            && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                            && adyesterday.getPresent_price().doubleValue() < adyesterday.getYt_close_price().doubleValue()
                            && macdls.get(t).getBar().doubleValue() < macdls.get(t + 1).getBar().doubleValue()
                            && macdls.get(t + 1).getBar().doubleValue() < 0
                            && (macdls.get(t + 8).getBar().doubleValue() < 0
                            && (macdls.get(t + 10).getBar().doubleValue() < 0
                            && macdls.get(t + 5).getBar().doubleValue() > 0
                            && macdls.get(t + 3).getBar().doubleValue() > 0))) {

                        log.info("\t" + "算法1" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                                + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                        sumincrsf1 = sumincrsf1 + mxinc;
                        sumdecrsf1 = sumdecrsf1 + miinc;
                        cntsf1++;

                    }

                }

                // ****************** 算法1结束******************

                // ****************** 算法2开始******************

                mmarr = new int[]{8, 12, 13, 20, 21};
                if ((today_ave_5 < yesterday_ave_5)) {
                    if (deallotsbeishu <= 3
                            && Analysis.timeWindow(adls.subList(t, t + 22), mmarr, curprice, 22)
                            && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                            && incr > 0
                            && (macdls.get(t).getBar().doubleValue() < 0
                            || (macdls.get(t).getBar().doubleValue() > 0
                            && macdls.get(t + 8).getBar().doubleValue() < 0
                            && macdls.get(t + 5).getBar().doubleValue() < 0))
                            && adyesterday.getPresent_price().doubleValue() < yesterday_ave_20 * 0.85
                            && adtoday.getPresent_price().doubleValue() > today_ave_20 * 0.85
                            && adyesterday.getPresent_price().doubleValue() < adyesterday.getYt_close_price().doubleValue()) {
                        log.info("\t" + "算法2" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                                + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                        sumincrsf2 = sumincrsf2 + mxinc;
                        sumdecrsf2 = sumdecrsf2 + miinc;
                        cntsf2++;

                    }
                }

                // ****************** 算法2结束******************



                String upcontent = "";
                String upcontent2 = "";
                int up = 0;
                int up2 = 0;
                int down = 0;
                if (Double.parseDouble(adtoday.getTd_open_price()) < today_ave_5 && adtoday.getPresent_price().doubleValue() > today_ave_5) {
                    upcontent = upcontent + "实体穿过5日线，";
                    up++;
                }
                if (Double.parseDouble(adtoday.getTd_open_price()) < today_ave_10 && adtoday.getPresent_price().doubleValue() > today_ave_10) {
                    upcontent = upcontent + "实体穿过10日线，";
                    up++;
                }
                if (Double.parseDouble(adtoday.getTd_open_price()) < today_ave_20 && adtoday.getPresent_price().doubleValue() > today_ave_20) {
                    upcontent = upcontent + "实体穿过20日线，";
                    up++;
                }
                if (Double.parseDouble(adtoday.getTd_open_price()) < today_ave_5 * 1.005 && adtoday.getPresent_price().doubleValue() > today_ave_5 * 0.995) {
                    upcontent2 = upcontent2 + "实体穿过加强5日线，";
                    up2++;
                }
                if (Double.parseDouble(adtoday.getTd_open_price()) < today_ave_10 * 1.005 && adtoday.getPresent_price().doubleValue() > today_ave_10 * 0.995) {
                    upcontent2 = upcontent2 + "实体穿过加强10日线，";
                    up2++;
                }
                if (Double.parseDouble(adtoday.getTd_open_price()) < today_ave_20 * 1.005 && adtoday.getPresent_price().doubleValue() > today_ave_20 * 0.995) {
                    upcontent2 = upcontent2 + "实体穿过加强20日线，";
                    up2++;
                }

                if (Double.parseDouble(adtoday.getTd_open_price()) > today_ave_5 && adtoday.getPresent_price().doubleValue() < today_ave_5) {
                    upcontent = upcontent + "实体穿过5日线，";
                    down++;
                }
                if (Double.parseDouble(adtoday.getTd_open_price()) > today_ave_10 && adtoday.getPresent_price().doubleValue() < today_ave_10) {
                    upcontent = upcontent + "实体穿过10日线，";
                    down++;
                }
                if (Double.parseDouble(adtoday.getTd_open_price()) > today_ave_20 && adtoday.getPresent_price().doubleValue() < today_ave_20) {
                    upcontent = upcontent + "实体穿过20日线，";
                    down++;
                }

                Map<String, Object> mpmm = Analysis.mmValue(adls.subList(t, t + 22), 22);
                Map<String, Object> mpmmsec = Analysis.mmValue(adls.subList(t + 1, t + 22), 21);
                int mx = (Integer) mpmm.get("max");
                int mi = (Integer) mpmm.get("min");
                int secmx = (Integer) mpmmsec.get("max");

                double mxpr = adls.subList(t, t + 22).get(mx).getPresent_price().doubleValue();
                double mipr = adls.subList(t, t + 22).get(mi).getPresent_price().doubleValue();
                // ****************** 算法3开始******************
                mmarr = new int[]{5, 8, 13, 21};
                if (today_ave_10 > yesterday_ave_10
                        && today_ave_20 > yesterday_ave_20) {
                    if (deallotsbeishu <= 3
                            && Analysis.timeWindow(adls.subList(t, t + 22), mmarr, curprice, 22)
                            && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                            && ((macdls.get(t).getBar().doubleValue() > 0
                            && macdls.get(t + 8).getBar().doubleValue() < 0)
                            || (macdls.get(t).getBar().doubleValue() < 0
                            && macdls.get(t + 8).getBar().doubleValue() > 0)
                            && macdls.get(t).getBar().doubleValue() < macdls.get(t + 1).getBar().doubleValue()
                            && macdls.get(t + 1).getBar().doubleValue() < macdls.get(t + 2).getBar().doubleValue())
                            && incr > 0
                            && incr < 3
                            && up >= 2
                            && mx < mi
                            && (adtoday.getPresent_price().doubleValue() - mipr) / mipr * 100 < 25
                            && (adtoday.getPresent_price().doubleValue() - mxpr) / mxpr * 100 > -4
                            && adyesterday.getPresent_price().doubleValue() < adyesterday.getYt_close_price().doubleValue()
                            && adyesterday.getPresent_price().doubleValue() < Double.parseDouble(adyesterday.getTd_open_price())) {

                        log.info("\t" + "算法3" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                                + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                        sumincrsf3 = sumincrsf3 + mxinc;
                        sumdecrsf3 = sumdecrsf3 + miinc;
                        cntsf3++;
                    }


                }
                // ****************** 算法3结束******************
                // ****************** 算法4开始******************
                mmarr = new int[]{5, 8, 13, 21};

                if (today_ave_10 > yesterday_ave_10
                        && today_ave_20 > yesterday_ave_20) {
                    if (deallotsbeishu <= 3
                            && Analysis.timeWindow(adls.subList(t, t + 22), mmarr, curprice, 22)
                            && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                            && macdls.get(t).getBar().doubleValue() < macdls.get(t + 1).getBar().doubleValue()
                            && macdls.get(t).getBar().doubleValue() < 0
                            && macdls.get(t + 8).getBar().doubleValue() > 0
                            && incr > 0
                            && mx < mi
                            && up == 0
                            && adtoday.getPresent_price().doubleValue() > today_ave_20
                            && adtoday.getPresent_price().doubleValue() < today_ave_10
                            && adtoday.getPresent_price().doubleValue() < today_ave_5
                            && (adtoday.getPresent_price().doubleValue() - mipr) / mipr * 100 < 25
                            && (adtoday.getPresent_price().doubleValue() - mxpr) / mxpr * 100 > -4
                            && adyesterday.getPresent_price().doubleValue() < adyesterday.getYt_close_price().doubleValue()
                            && adyesterday.getPresent_price().doubleValue() < Double.parseDouble(adyesterday.getTd_open_price())) {

                        log.info("\t" + "算法4" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                                + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                        sumincrsf4 = sumincrsf4 + mxinc;
                        sumdecrsf4 = sumdecrsf4 + miinc;
                        cntsf4++;
                    }


                }

                // ****************** 算法4结束******************
                // ****************** 算法5开始******************
                mmarr = new int[]{5, 8, 13, 21};
                if (today_ave_10 > yesterday_ave_10
                        && today_ave_20 > yesterday_ave_20) {
                    if (deallotsbeishu < 3
                            && Analysis.timeWindow(adls.subList(t, t + 22), mmarr, curprice, 22)
                            && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                            && macdls.get(t).getBar().doubleValue() < macdls.get(t + 1).getBar().doubleValue()
                            && ((macdls.get(t).getBar().doubleValue() < 0
                            && macdls.get(t + 3).getBar().doubleValue() > 0)
                            || (macdls.get(t).getBar().doubleValue() > 0
                            && macdls.get(t + 8).getBar().doubleValue() < 0))
                            && incr > 0
                            && mx < mi
                            && mx != 0
                            && adyesterday.getPresent_price().doubleValue() < yesterday_ave_10
                            && (adtoday.getPresent_price().doubleValue() - mipr) / mipr * 100 < 25
                            && (adtoday.getPresent_price().doubleValue() - mxpr) / mxpr * 100 > -4
                            && adyesterday.getPresent_price().doubleValue() < adyesterday.getYt_close_price().doubleValue()
                            && adyesterday.getPresent_price().doubleValue() < Double.parseDouble(adyesterday.getTd_open_price())) {

                        log.info("\t" + "算法5" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                                + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                        sumincrsf5 = sumincrsf5 + mxinc;
                        sumdecrsf5 = sumdecrsf5 + miinc;
                        cntsf5++;
                    }


                }
                // ****************** 算法5结束******************

                // ****************** 算法6开始******************
                mmarr = new int[]{8, 12, 13, 20, 21};
                double mxfivedaymacd = Analysis.maxValue(
                        macdls.get(t).getBar().doubleValue(),
                        macdls.get(t + 1).getBar().doubleValue(),
                        macdls.get(t + 2).getBar().doubleValue());
                double maxav = Analysis.maxValue(today_ave_20, today_ave_10, today_ave_5);
                double minav = Analysis.minValue(today_ave_20, today_ave_10, today_ave_5);
                double yestoc = (yesterday_ave_20 - yesterday_ave_10) / (yesterday_ave_20 - yesterday_ave_5);
                double todayoc = (today_ave_20 - today_ave_10) / (today_ave_20 - today_ave_5);
                double av = Math.abs(yestoc - todayoc) / adyesterday.getPresent_price().doubleValue();
                if (today_ave_10 > today_ave_5
                        && today_ave_20 > today_ave_10) {
                    if (deallotsbeishu < 3
                            && Analysis.timeWindow(adls.subList(t, t + 22), mmarr, curprice, 22)
                            && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                            && macdls.get(t).getBar().doubleValue() < macdls.get(t + 1).getBar().doubleValue()
                            && (today_ave_20 - yesterday_ave_20) / yesterday_ave_20 < -0.00618
                            && (today_ave_20 - yesterday_ave_20) / yesterday_ave_20 > -0.007
                            && incr > 0
                            && (today_ave_5 < yesterday_ave_5)
                            && adyesterday.getPresent_price().doubleValue() < adyesterday.getYt_close_price().doubleValue()
                            && adyesterday.getPresent_price().doubleValue() < Double.parseDouble(adyesterday.getTd_open_price())) {




                        log.info("\t" + "算法6" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                                + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                        sumincrsf6 = sumincrsf6 + mxinc;
                        sumdecrsf6 = sumdecrsf6 + miinc;
                        cntsf6++;
                    }
                }
                // ****************** 算法7开始******************

                if (today_ave_10 > today_ave_5
                        && today_ave_20 > today_ave_10) {
                    if (deallotsbeishu < 3
                            && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                            && (today_ave_20 - yesterday_ave_20) / yesterday_ave_20 < -0.00618
                            && (today_ave_20 - yesterday_ave_20) / yesterday_ave_20 > -0.007
                            && incr > 0
                            && up2 >= 2
                            && (today_ave_5 < yesterday_ave_5)
                            && adyesterday.getPresent_price().doubleValue() < adyesterday.getYt_close_price().doubleValue()
                            && adyesterday.getPresent_price().doubleValue() < Double.parseDouble(adyesterday.getTd_open_price())) {

                        log.info("\t" + "算法7" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                                + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                        sumincrsf7 = sumincrsf7 + mxinc;
                        sumdecrsf7 = sumdecrsf7 + miinc;
                        cntsf7++;
                    }
                }


                // ****************** 算法8开始******************

                if (today_ave_20 > yesterday_ave_20
                        && Caculator.keepRound(yesterday_ave_20 - twodaybefore_ave_20, 2) > Caculator.keepRound(today_ave_20 - yesterday_ave_20, 2)
                        && today_ave_10 < yesterday_ave_10
                        && minav == today_ave_20
                        && maxav == today_ave_10
                        && adyesterday.getPresent_price().doubleValue() < yesterday_ave_20
                        && adyesterday.getPresent_price().doubleValue() < adyesterday.getYt_close_price().doubleValue() * 0.99
                        && adyesterday.getPresent_price().doubleValue() < Double.parseDouble(adyesterday.getTd_open_price())
                        && macdls.get(t).getBar().doubleValue() < 0
                        && macdls.get(t + 5).getBar().doubleValue() > 0
                        && macdls.get(t + 8).getBar().doubleValue() > 0
                        && macdls.get(t + 13).getBar().doubleValue() > 0
                        && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                        && incr > 1
                        && mx > 4
                        && (adtoday.getPresent_price().doubleValue() - mipr) / mipr * 100 < 25) {


                    log.info("\t" + "算法8" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                            + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                    sumincrsf8 = sumincrsf8 + mxinc;
                    sumdecrsf8 = sumdecrsf8 + miinc;
                    cntsf8++;

                }

                // ****************** 算法8结束******************


                // ****************** 算法9开始******************
                if (today_ave_20 > yesterday_ave_20
                        && Caculator.keepRound(yesterday_ave_10 - twodaybefore_ave_10, 2) < Caculator.keepRound(today_ave_10 - yesterday_ave_10, 2)
                        && Caculator.keepRound(yesterday_ave_20 - twodaybefore_ave_20, 2) > Caculator.keepRound(today_ave_20 - yesterday_ave_20, 2)
                        && today_ave_5 < yesterday_ave_5
                        && minav == today_ave_20
                        && maxav == today_ave_5
                        && deallotsbeishu <= 3
                        && Math.abs((today_ave_5 - today_ave_10) / today_ave_10) > 0.004
                        && Math.abs((today_ave_5 - today_ave_10) / today_ave_10) < 0.0168
                        && adyesterday.getPresent_price().doubleValue() < yesterday_ave_10
                        && adyesterday.getPresent_price().doubleValue() < adyesterday.getYt_close_price().doubleValue() * 0.99
                        && adyesterday.getPresent_price().doubleValue() < Double.parseDouble(adyesterday.getTd_open_price())
                        && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                        && Double.parseDouble(adtoday.getTd_open_price()) > today_ave_20
                        && incr <= 1
                        && incr > 0
                        && (adtoday.getPresent_price().doubleValue() - mipr) / mipr * 100 < 25) {

                    log.info("\t" + "算法9" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                            + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                    sumincrsf9 = sumincrsf9 + mxinc;
                    sumdecrsf9 = sumdecrsf9 + miinc;
                    cntsf9++;



                }


                // ****************** 算法9结束******************

                // ****************** 算法10开始******************

                double maxoc1 = Analysis.maxValue(adls.get(t + 1).getPresent_price().doubleValue(),
                        Double.parseDouble(adls.get(t + 1).getTd_open_price()));
                double maxoc2 = Analysis.maxValue(adls.get(t + 2).getPresent_price().doubleValue(),
                        Double.parseDouble(adls.get(t + 2).getTd_open_price()));
                double maxoc3 = Analysis.maxValue(adls.get(t + 3).getPresent_price().doubleValue(),
                        Double.parseDouble(adls.get(t + 3).getTd_open_price()));

                double maxocf = Analysis.maxValue(maxoc1, maxoc2, maxoc3);
                double minocf = Analysis.minValue(maxoc1, maxoc2, maxoc3);


                if (adtoday.getPresent_price().doubleValue() - today_ave_5 > 0
                        && adls.get(t + 1).getPresent_price().doubleValue() - yesterday_ave_5 < 0
                        && adls.get(t + 2).getPresent_price().doubleValue() - twodaybefore_ave_5 > 0
                        && today_ave_20 > yesterday_ave_20
                        && today_ave_5 > yesterday_ave_5
                        && yesterday_ave_5 > twodaybefore_ave_5
                        && macdls.get(t).getBar().doubleValue() > 0
                        && macdls.get(t + 5).getBar().doubleValue() > 0
                        && macdls.get(t).getBar().doubleValue() < macdls.get(t + 1).getBar().doubleValue()
                        && macdls.get(t).getDif().doubleValue() > 2 * macdls.get(t).getBar().doubleValue()
                        && deallotsbeishu < 3
                        && incr > 0.8
                        && incr < 1.2
                        && adyesterday.getPresent_price().doubleValue() > yesterday_ave_10
                        && (adtoday.getPresent_price().doubleValue() - mipr) / mipr * 100 < 25) {

                    log.info("\t" + "算法10" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                            + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                    sumincrsf10 = sumincrsf10 + mxinc;
                    sumdecrsf10 = sumdecrsf10 + miinc;
                    cntsf10++;



                }




                // ****************** 算法10结束******************
                // ****************** 算法11开始******************

                double midav = 0;
                if (maxav == today_ave_20) {
                    midav = Analysis.maxValue(today_ave_10, today_ave_5);
                } else if (maxav == today_ave_10) {
                    midav = Analysis.maxValue(today_ave_20, today_ave_5);
                } else if (maxav == today_ave_5) {
                    midav = Analysis.maxValue(today_ave_10, today_ave_20);
                }


                if (deallotsbeishu < 3
                        //                            && deallotsbeishu > 2
                        && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                        //                        && !(macdls.get(t).getBar().doubleValue() > 0
                        //                        && macdls.get(t + 1).getBar().doubleValue() < 0)
                        && incr > 9.9
                        && up == 3) {

                    log.info("\t" + "算法11" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                            + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                    sumincrsf11 = sumincrsf11 + mxinc;
                    sumdecrsf11 = sumdecrsf11 + miinc;
                    cntsf11++;
                }



                // ****************** 算法11结束******************

                // ****************** 算法12开始******************




                if (down == 3
                        && adtoday.getPresent_price().doubleValue() < Double.parseDouble(adtoday.getTd_open_price())
                        //                        && (today_ave_5 + today_ave_10 + today_ave_20) / 3 < adtoday.getPresent_price().doubleValue() * 1.005
                        && Double.parseDouble(adtoday.getTd_open_price()) - (today_ave_5 + today_ave_10 + today_ave_20) / 3 > (today_ave_5 + today_ave_10 + today_ave_20) / 3 - adtoday.getPresent_price().doubleValue()
                        //                        && macdls.get(t).getBar().doubleValue() > 0
                        //                        && macdls.get(t).getDea().doubleValue() < 0
                        //                        && incr < -1.18
                        && maxav < 1.005 * minav) {
                    double onedaylatermxincr = Caculator.keepRound((onedaylater.getTd_highest_price().doubleValue() - onedaylater.getYt_close_price().doubleValue()) * 100 / onedaylater.getYt_close_price().doubleValue(), 2);
                    double onedaylatermiincr = Caculator.keepRound((onedaylater.getTd_lowest_price().doubleValue() - onedaylater.getYt_close_price().doubleValue()) * 100 / onedaylater.getYt_close_price().doubleValue(), 2);

//                    log.info("\t" + "算法12" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
//                            + " mxincr=" + "\t" + onedaylatermxincr + "\t" + " minincr=" + "\t" + onedaylatermiincr);
//                    sumincrsf12 = sumincrsf12 + onedaylatermxincr;
//                    sumdecrsf12 = sumdecrsf12 + onedaylatermiincr;
                    cntsf12++;
                    log.info("\t" + "算法12" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                            + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                    sumincrsf12 = sumincrsf12 + mxinc;
                    sumdecrsf12 = sumdecrsf12 + miinc;
                }



                // ****************** 算法12结束******************

                // ****************** 算法13开始******************


                if (adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                        && mx == 0
                        && incr > 0
                        && today_ave_20 > yesterday_ave_20 * 1.002
                        && macdls.get(t).getBar().doubleValue() > 0
                        && macdls.get(t).getDea().doubleValue() < 0
                        && adyesterday.getPresent_price().doubleValue() - adyesterday.getYt_close_price().doubleValue() > 0
                        && (adyesterday.getPresent_price().doubleValue() - mipr) / mipr * 100 < 18
                        && (adyesterday.getPresent_price().doubleValue() - mipr) / mipr * 100 > 8) {

                    int cnt1 = 0;
                    int cnt2 = 0;
                    for (Alldata ad : adls.subList(t + 1, t + 22)) {
                        if (twodaybefore.getPresent_price().doubleValue() <= ad.getTd_highest_price().doubleValue()) {
                            cnt1++;
                        }
                        if (threedaybefore.getPresent_price().doubleValue() <= ad.getTd_highest_price().doubleValue()) {
                            cnt2++;
                        }
                    }

                    if (cnt1 >= 3 || cnt2 >= 3) {
                        log.info("\t" + "算法13" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                                + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                        sumincrsf13 = sumincrsf13 + mxinc;
                        sumdecrsf13 = sumdecrsf13 + miinc;
                        cntsf13++;
                    }


                }

                // ****************** 算法13结束******************


                // ****************** 算法14开始******************

                Map<String, Object> mpmm13 = Analysis.mmValue(adls.subList(t, t + 13), 13);

                int mx13 = (Integer) mpmm13.get("max");
                int mi13 = (Integer) mpmm13.get("min");
                double mxpr13 = adls.subList(t, t + 13).get(mx13).getPresent_price().doubleValue();
                double incr13 = adls.subList(t, t + 13).get(mx13).getPresent_price().doubleValue() / adls.subList(t, t + 13).get(mx13).getYt_close_price().doubleValue();
                if (macdls.get(t).getDea().doubleValue() > macdls.get(t + 1).getDea().doubleValue()
                        && macdls.get(t).getBar().doubleValue() > 0
                        && macdls.get(t).getDea().doubleValue() < 0
                        && incr13 < 1.06
                        && mx13 < 7
                        && incr < 0
                        && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                        && minav == today_ave_20
                        && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                        && (adtoday.getPresent_price().doubleValue() < maxav
                        || adtoday.getPresent_price().doubleValue() < today_ave_10)) {

//}
//                if (adtoday.getPresent_price().doubleValue() <Double.parseDouble(adtoday.getTd_open_price())
//                        && mx ==1
//                        && incr < 0
//                        && adtoday.getPresent_price().doubleValue()>today_ave_10
//                        &&  adtoday.getPresent_price().doubleValue()<today_ave_5
//                        && today_ave_20 < today_ave_10
//                        && today_ave_10<today_ave_5
//                        && macdls.get(t).getBar().doubleValue() > 0
//                        && macdls.get(t).getBar().doubleValue() > macdls.get(t).getDea().doubleValue()
//                        && (adyesterday.getPresent_price().doubleValue() - mipr) / mipr * 100 < 25
//                        && (adyesterday.getPresent_price().doubleValue() - mipr) / mipr * 100 > 8) {


                    log.info("\t" + "算法14" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                            + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                    sumincrsf14 = sumincrsf14 + mxinc;
                    sumdecrsf14 = sumdecrsf14 + miinc;
                    cntsf14++;



                }


                // ****************** 算法14结束******************

                // ****************** 算法15开始******************

                if (deallotsbeishu < 8
                        && macdls.get(t).getBar().doubleValue() - macdls.get(t + 1).getBar().doubleValue() > adtoday.getPresent_price().doubleValue() / 100
                        && macdls.get(t + 1).getBar().doubleValue() < adyesterday.getPresent_price().doubleValue() / 100
                        && macdls.get(t).getBar().doubleValue() > 0
                        && up == 3
                        && incr > 0
                        && adtoday.getPresent_price().doubleValue() > maxav
                        && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())) {

                    log.info("\t" + "算法15" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                            + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                    sumincrsf15 = sumincrsf15 + mxinc;
                    sumdecrsf15 = sumdecrsf15 + miinc;
                    cntsf15++;



                }


                // ****************** 算法15结束******************
                // ****************** 算法16开始******************

                if (deallotsbeishu < 3
                        && macdls.get(t + 1).getBar().doubleValue() < 0
                        && macdls.get(t).getBar().doubleValue() > 0
                        && Math.abs(macdls.get(t).getDea().doubleValue()) < adtoday.getPresent_price().doubleValue() * 0.03
                        && incr > 1.68
                        && today_ave_20 > yesterday_ave_20
                        && yesterday_ave_20 > twodaybefore_ave_20
                        && up == 3
                        && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())) {

                    log.info("\t" + "算法16" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                            + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                    sumincrsf16 = sumincrsf16 + mxinc;
                    sumdecrsf16 = sumdecrsf16 + miinc;
                    cntsf16++;



                }


                // ****************** 算法16结束******************

                // ****************** 算法17开始******************
                int mark = -1;
                for (int h = 0; h < 23; h++) {
                    if ((macdls.get(t + h).getDif().doubleValue() > 0
                            && macdls.get(t + h).getDea().doubleValue() > 0)
                            && (macdls.get(t + h + 1).getDif().doubleValue() > 0
                            && macdls.get(t + h + 1).getDea().doubleValue() < 0)
                            && macdls.get(t + h).getBar().doubleValue() > 0) {
                        mark = h;
                        break;
                    }
                }

                if (deallotsbeishu < 2
                        && incr > 0
                        && adtoday.getPresent_price().doubleValue() > Double.parseDouble(adtoday.getTd_open_price())
                        && adyesterday.getPresent_price().doubleValue() < adyesterday.getYt_close_price().doubleValue()
                        && macdls.get(t).getBar().doubleValue() > macdls.get(t + 1).getBar().doubleValue()
                        && macdls.get(t).getBar().doubleValue() < 0
                        //                        && macdls.get(t).getBar().doubleValue() > macdls.get(t).getDif().doubleValue()
                        && macdls.get(t).getDif().doubleValue() > 0
                        && macdls.get(t).getDea().doubleValue() > 0
                        && mark != -1) {

                    double onedaylatermxincr = Caculator.keepRound((onedaylater.getTd_highest_price().doubleValue() - onedaylater.getYt_close_price().doubleValue()) * 100 / onedaylater.getYt_close_price().doubleValue(), 2);
                    double onedaylatermiincr = Caculator.keepRound((onedaylater.getTd_lowest_price().doubleValue() - onedaylater.getYt_close_price().doubleValue()) * 100 / onedaylater.getYt_close_price().doubleValue(), 2);

                    log.info("\t" + "算法17" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                            + " mxincr=" + "\t" + onedaylatermxincr + "\t" + " minincr=" + "\t" + onedaylatermiincr);
                    sumincrsf17 = sumincrsf17 + onedaylatermxincr;
                    sumdecrsf17 = sumdecrsf17 + onedaylatermiincr;
                    cntsf17++;



                }


                // ****************** 算法17结束******************

                // ****************** 算法18开始******************


                if (deallotsbeishu < 2
                        && incr < 0
                        && adtoday.getPresent_price().doubleValue() < today_ave_20
                        && macdls.get(t).getDif().doubleValue() > 0
                        && macdls.get(t).getDea().doubleValue() > 0
                        && mark != -1
                        && mark < 14) {


                    log.info("\t" + "算法18" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                            + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                    sumincrsf18 = sumincrsf18 + mxinc;
                    sumdecrsf18 = sumdecrsf18 + miinc;
                    cntsf18++;



                }


                // ****************** 算法18结束******************

                // ****************** 算法21开始******************


                if (deallotsbeishu < 5
                        && macdls.get(t).getDif().doubleValue() > 0
                        && macdls.get(t).getDea().doubleValue() > 0
                        && macdls.get(t).getBar().doubleValue() > macdls.get(t + 1).getBar().doubleValue()
                        && mark != -1) {
                    if (t < kdjls.size() - 1
                            && Analysis.isUpCross(kdjls.get(t), kdjls.get(t + 1))
                            && kdjls.get(t).getK().doubleValue() < 50) {
                        log.info("\t" + "算法21" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                                + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                        sumincrsf21 = sumincrsf21 + mxinc;
                        sumdecrsf21 = sumdecrsf21 + miinc;
                        cntsf21++;
                    }





                }


                // ****************** 算法21结束******************
                // ****************** 算法22开始******************
                if (deallotsbeishu < 5 //                        && macdls.get(t).getDif().doubleValue() > 0
                        //                        && macdls.get(t).getDea().doubleValue() > 0
                        //                        && macdls.get(t).getBar().doubleValue() > macdls.get(t + 1).getBar().doubleValue()
                        //                        && mark != -1
                        ) {
                    if (t < kdjls.size() - 1
                            && ((incr > 0
                            && kdjls.get(t).getJ().doubleValue() < kdjls.get(t + 1).getJ().doubleValue()
                            && kdjls.get(t).getD().doubleValue() < 50
                            && macdls.get(t).getBar().doubleValue() < macdls.get(t + 1).getBar().doubleValue()
                            && macdls.get(t).getBar().doubleValue() < 0)
                            || (incr < 0
                            && kdjls.get(t).getJ().doubleValue() > kdjls.get(t + 1).getJ().doubleValue())
                            //                             && kdjls.get(t+1).getJ().doubleValue()< kdjls.get(t+1).getK().doubleValue()
                            && kdjls.get(t).getD().doubleValue() > 50
                            && macdls.get(t).getBar().doubleValue() > macdls.get(t + 1).getBar().doubleValue()
                            && macdls.get(t).getBar().doubleValue() > 0)) {
                        log.info("\t" + "算法22" + "\t" + adtoday.getStock_cd() + "\t" + adtoday.getRecord_date() + "\t" + content + "\t"
                                + " mxincr=" + "\t" + mxinc + "\t" + " minincr=" + "\t" + miinc);
                        sumincrsf22 = sumincrsf22 + mxinc;
                        sumdecrsf22 = sumdecrsf22 + miinc;
                        cntsf22++;
                    }

                }


                // ****************** 算法22结束******************
                // ****************** 统计类似走势开始******************

                if (adtoday.getPresent_price().doubleValue() > adtoday.getYt_close_price().doubleValue() * 1.099
                        && adyesterday.getPresent_price().doubleValue() > adyesterday.getYt_close_price().doubleValue() * 1.099
                        && twodaybefore.getPresent_price().doubleValue() < twodaybefore.getYt_close_price().doubleValue() * 1.09) {


                    log.info("\t" + "统计类似走势" + "\t" + adyesterday.getStock_cd() + "\t" + adtoday.getRecord_date());




                }


                // ****************** 统计类似走势结束******************



            }



        }



        log.info("算法1：" + cntsf1);
        log.info(sumincrsf1 / cntsf1);
        log.info(sumdecrsf1 / cntsf1);
        log.info("算法2：" + cntsf2);
        log.info(sumincrsf2 / cntsf2);
        log.info(sumdecrsf2 / cntsf2);
        log.info("算法3：" + cntsf3);
        log.info(sumincrsf3 / cntsf3);
        log.info(sumdecrsf3 / cntsf3);
        log.info("算法4：" + cntsf4);
        log.info(sumincrsf4 / cntsf4);
        log.info(sumdecrsf4 / cntsf4);
        log.info("算法5：" + cntsf5);
        log.info(sumincrsf5 / cntsf5);
        log.info(sumdecrsf5 / cntsf5);
        log.info("算法6：" + cntsf6);
        log.info(sumincrsf6 / cntsf6);
        log.info(sumdecrsf6 / cntsf6);
        log.info("算法7：" + cntsf7);
        log.info(sumincrsf7 / cntsf7);
        log.info(sumdecrsf7 / cntsf7);
        log.info("算法8：" + cntsf8);
        log.info(sumincrsf8 / cntsf8);
        log.info(sumdecrsf8 / cntsf8);
        log.info("算法9：" + cntsf9);
        log.info(sumincrsf9 / cntsf9);
        log.info(sumdecrsf9 / cntsf9);
        log.info("算法10：" + cntsf10);
        log.info(sumincrsf10 / cntsf10);
        log.info(sumdecrsf10 / cntsf10);
        log.info("算法11：" + cntsf11);
        log.info(sumincrsf11 / cntsf11);
        log.info(sumdecrsf11 / cntsf11);
        log.info("算法12：" + cntsf12);
        log.info(sumincrsf12 / cntsf12);
        log.info(sumdecrsf12 / cntsf12);
        log.info("算法13：" + cntsf13);
        log.info(sumincrsf13 / cntsf13);
        log.info(sumdecrsf13 / cntsf13);
        log.info("算法14：" + cntsf14);
        log.info(sumincrsf14 / cntsf14);
        log.info(sumdecrsf14 / cntsf14);
        log.info("算法15：" + cntsf15);
        log.info(sumincrsf15 / cntsf15);
        log.info(sumdecrsf15 / cntsf15);
        log.info("算法16：" + cntsf16);
        log.info(sumincrsf16 / cntsf16);
        log.info(sumdecrsf16 / cntsf16);
        log.info("算法17：" + cntsf17);
        log.info(sumincrsf17 / cntsf17);
        log.info(sumdecrsf17 / cntsf17);
        log.info("算法18：" + cntsf18);
        log.info(sumincrsf18 / cntsf18);
        log.info(sumdecrsf18 / cntsf18);
        log.info("算法21：" + cntsf21);
        log.info(sumincrsf21 / cntsf21);
        log.info(sumdecrsf21 / cntsf21);
        log.info("算法22：" + cntsf22);
        log.info(sumincrsf22 / cntsf22);
        log.info(sumdecrsf22 / cntsf22);
    }
}
